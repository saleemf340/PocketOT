package za.co.pocketot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static za.co.pocketot.domain.PractitionerAsserts.*;
import static za.co.pocketot.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import za.co.pocketot.IntegrationTest;
import za.co.pocketot.domain.Practitioner;
import za.co.pocketot.domain.enumeration.IdDocument;
import za.co.pocketot.domain.enumeration.PractitionerType;
import za.co.pocketot.repository.EntityManager;
import za.co.pocketot.repository.PractitionerRepository;
import za.co.pocketot.service.dto.PractitionerDTO;
import za.co.pocketot.service.mapper.PractitionerMapper;

/**
 * Integration tests for the {@link PractitionerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PractitionerResourceIT {

    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_UUID = UUID.randomUUID();

    private static final IdDocument DEFAULT_ID_DOCUMENT = IdDocument.SOUTH_AFRICAN_ID;
    private static final IdDocument UPDATED_ID_DOCUMENT = IdDocument.PASSPORT;

    private static final String DEFAULT_ID_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ID_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final PractitionerType DEFAULT_PRACTITIONER_TYPE = PractitionerType.OCCUPATIONAL_THERAPIST;
    private static final PractitionerType UPDATED_PRACTITIONER_TYPE = PractitionerType.PHYSIOTHERAPRIST;

    private static final String ENTITY_API_URL = "/api/practitioners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private PractitionerMapper practitionerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Practitioner practitioner;

    private Practitioner insertedPractitioner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Practitioner createEntity() {
        return new Practitioner()
            .uuid(DEFAULT_UUID)
            .idDocument(DEFAULT_ID_DOCUMENT)
            .idNumber(DEFAULT_ID_NUMBER)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .registrationNumber(DEFAULT_REGISTRATION_NUMBER)
            .verified(DEFAULT_VERIFIED)
            .practitionerType(DEFAULT_PRACTITIONER_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Practitioner createUpdatedEntity() {
        return new Practitioner()
            .uuid(UPDATED_UUID)
            .idDocument(UPDATED_ID_DOCUMENT)
            .idNumber(UPDATED_ID_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .verified(UPDATED_VERIFIED)
            .practitionerType(UPDATED_PRACTITIONER_TYPE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Practitioner.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    void initTest() {
        practitioner = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPractitioner != null) {
            practitionerRepository.delete(insertedPractitioner).block();
            insertedPractitioner = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPractitioner() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Practitioner
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);
        var returnedPractitionerDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PractitionerDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Practitioner in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPractitioner = practitionerMapper.toEntity(returnedPractitionerDTO);
        assertPractitionerUpdatableFieldsEquals(returnedPractitioner, getPersistedPractitioner(returnedPractitioner));

        insertedPractitioner = returnedPractitioner;
    }

    @Test
    void createPractitionerWithExistingId() throws Exception {
        // Create the Practitioner with an existing ID
        practitioner.setId(1L);
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPractitionersAsStream() {
        // Initialize the database
        practitionerRepository.save(practitioner).block();

        List<Practitioner> practitionerList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PractitionerDTO.class)
            .getResponseBody()
            .map(practitionerMapper::toEntity)
            .filter(practitioner::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(practitionerList).isNotNull();
        assertThat(practitionerList).hasSize(1);
        Practitioner testPractitioner = practitionerList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertPractitionerAllPropertiesEquals(practitioner, testPractitioner);
        assertPractitionerUpdatableFieldsEquals(practitioner, testPractitioner);
    }

    @Test
    void getAllPractitioners() {
        // Initialize the database
        insertedPractitioner = practitionerRepository.save(practitioner).block();

        // Get all the practitionerList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(practitioner.getId().intValue()))
            .jsonPath("$.[*].uuid")
            .value(hasItem(DEFAULT_UUID.toString()))
            .jsonPath("$.[*].idDocument")
            .value(hasItem(DEFAULT_ID_DOCUMENT.toString()))
            .jsonPath("$.[*].idNumber")
            .value(hasItem(DEFAULT_ID_NUMBER))
            .jsonPath("$.[*].dateOfBirth")
            .value(hasItem(DEFAULT_DATE_OF_BIRTH.toString()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].registrationNumber")
            .value(hasItem(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.[*].verified")
            .value(hasItem(DEFAULT_VERIFIED))
            .jsonPath("$.[*].practitionerType")
            .value(hasItem(DEFAULT_PRACTITIONER_TYPE.toString()));
    }

    @Test
    void getPractitioner() {
        // Initialize the database
        insertedPractitioner = practitionerRepository.save(practitioner).block();

        // Get the practitioner
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, practitioner.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(practitioner.getId().intValue()))
            .jsonPath("$.uuid")
            .value(is(DEFAULT_UUID.toString()))
            .jsonPath("$.idDocument")
            .value(is(DEFAULT_ID_DOCUMENT.toString()))
            .jsonPath("$.idNumber")
            .value(is(DEFAULT_ID_NUMBER))
            .jsonPath("$.dateOfBirth")
            .value(is(DEFAULT_DATE_OF_BIRTH.toString()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.registrationNumber")
            .value(is(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.verified")
            .value(is(DEFAULT_VERIFIED))
            .jsonPath("$.practitionerType")
            .value(is(DEFAULT_PRACTITIONER_TYPE.toString()));
    }

    @Test
    void getNonExistingPractitioner() {
        // Get the practitioner
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPractitioner() throws Exception {
        // Initialize the database
        insertedPractitioner = practitionerRepository.save(practitioner).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the practitioner
        Practitioner updatedPractitioner = practitionerRepository.findById(practitioner.getId()).block();
        updatedPractitioner
            .uuid(UPDATED_UUID)
            .idDocument(UPDATED_ID_DOCUMENT)
            .idNumber(UPDATED_ID_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .verified(UPDATED_VERIFIED)
            .practitionerType(UPDATED_PRACTITIONER_TYPE);
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(updatedPractitioner);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, practitionerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPractitionerToMatchAllProperties(updatedPractitioner);
    }

    @Test
    void putNonExistingPractitioner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        practitioner.setId(longCount.incrementAndGet());

        // Create the Practitioner
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, practitionerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPractitioner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        practitioner.setId(longCount.incrementAndGet());

        // Create the Practitioner
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPractitioner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        practitioner.setId(longCount.incrementAndGet());

        // Create the Practitioner
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePractitionerWithPatch() throws Exception {
        // Initialize the database
        insertedPractitioner = practitionerRepository.save(practitioner).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the practitioner using partial update
        Practitioner partialUpdatedPractitioner = new Practitioner();
        partialUpdatedPractitioner.setId(practitioner.getId());

        partialUpdatedPractitioner
            .idDocument(UPDATED_ID_DOCUMENT)
            .idNumber(UPDATED_ID_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .lastName(UPDATED_LAST_NAME)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPractitioner.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPractitioner))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Practitioner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPractitionerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPractitioner, practitioner),
            getPersistedPractitioner(practitioner)
        );
    }

    @Test
    void fullUpdatePractitionerWithPatch() throws Exception {
        // Initialize the database
        insertedPractitioner = practitionerRepository.save(practitioner).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the practitioner using partial update
        Practitioner partialUpdatedPractitioner = new Practitioner();
        partialUpdatedPractitioner.setId(practitioner.getId());

        partialUpdatedPractitioner
            .uuid(UPDATED_UUID)
            .idDocument(UPDATED_ID_DOCUMENT)
            .idNumber(UPDATED_ID_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .verified(UPDATED_VERIFIED)
            .practitionerType(UPDATED_PRACTITIONER_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPractitioner.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPractitioner))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Practitioner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPractitionerUpdatableFieldsEquals(partialUpdatedPractitioner, getPersistedPractitioner(partialUpdatedPractitioner));
    }

    @Test
    void patchNonExistingPractitioner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        practitioner.setId(longCount.incrementAndGet());

        // Create the Practitioner
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, practitionerDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPractitioner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        practitioner.setId(longCount.incrementAndGet());

        // Create the Practitioner
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPractitioner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        practitioner.setId(longCount.incrementAndGet());

        // Create the Practitioner
        PractitionerDTO practitionerDTO = practitionerMapper.toDto(practitioner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(practitionerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Practitioner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePractitioner() {
        // Initialize the database
        insertedPractitioner = practitionerRepository.save(practitioner).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the practitioner
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, practitioner.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return practitionerRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Practitioner getPersistedPractitioner(Practitioner practitioner) {
        return practitionerRepository.findById(practitioner.getId()).block();
    }

    protected void assertPersistedPractitionerToMatchAllProperties(Practitioner expectedPractitioner) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPractitionerAllPropertiesEquals(expectedPractitioner, getPersistedPractitioner(expectedPractitioner));
        assertPractitionerUpdatableFieldsEquals(expectedPractitioner, getPersistedPractitioner(expectedPractitioner));
    }

    protected void assertPersistedPractitionerToMatchUpdatableProperties(Practitioner expectedPractitioner) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPractitionerAllUpdatablePropertiesEquals(expectedPractitioner, getPersistedPractitioner(expectedPractitioner));
        assertPractitionerUpdatableFieldsEquals(expectedPractitioner, getPersistedPractitioner(expectedPractitioner));
    }
}
