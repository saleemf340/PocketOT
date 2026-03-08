package za.co.pocketot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static za.co.pocketot.domain.PatientAsserts.*;
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
import za.co.pocketot.domain.Patient;
import za.co.pocketot.domain.enumeration.IdDocument;
import za.co.pocketot.repository.EntityManager;
import za.co.pocketot.repository.PatientRepository;
import za.co.pocketot.service.dto.PatientDTO;
import za.co.pocketot.service.mapper.PatientMapper;

/**
 * Integration tests for the {@link PatientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PatientResourceIT {

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

    private static final String ENTITY_API_URL = "/api/patients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Patient patient;

    private Patient insertedPatient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Patient createEntity() {
        return new Patient()
            .uuid(DEFAULT_UUID)
            .idDocument(DEFAULT_ID_DOCUMENT)
            .idNumber(DEFAULT_ID_NUMBER)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Patient createUpdatedEntity() {
        return new Patient()
            .uuid(UPDATED_UUID)
            .idDocument(UPDATED_ID_DOCUMENT)
            .idNumber(UPDATED_ID_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Patient.class).block();
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
        patient = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPatient != null) {
            patientRepository.delete(insertedPatient).block();
            insertedPatient = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPatient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);
        var returnedPatientDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PatientDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Patient in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPatient = patientMapper.toEntity(returnedPatientDTO);
        assertPatientUpdatableFieldsEquals(returnedPatient, getPersistedPatient(returnedPatient));

        insertedPatient = returnedPatient;
    }

    @Test
    void createPatientWithExistingId() throws Exception {
        // Create the Patient with an existing ID
        patient.setId(1L);
        PatientDTO patientDTO = patientMapper.toDto(patient);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPatientsAsStream() {
        // Initialize the database
        patientRepository.save(patient).block();

        List<Patient> patientList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PatientDTO.class)
            .getResponseBody()
            .map(patientMapper::toEntity)
            .filter(patient::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(patientList).isNotNull();
        assertThat(patientList).hasSize(1);
        Patient testPatient = patientList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertPatientAllPropertiesEquals(patient, testPatient);
        assertPatientUpdatableFieldsEquals(patient, testPatient);
    }

    @Test
    void getAllPatients() {
        // Initialize the database
        insertedPatient = patientRepository.save(patient).block();

        // Get all the patientList
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
            .value(hasItem(patient.getId().intValue()))
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
            .value(hasItem(DEFAULT_LAST_NAME));
    }

    @Test
    void getPatient() {
        // Initialize the database
        insertedPatient = patientRepository.save(patient).block();

        // Get the patient
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, patient.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(patient.getId().intValue()))
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
            .value(is(DEFAULT_LAST_NAME));
    }

    @Test
    void getNonExistingPatient() {
        // Get the patient
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPatient() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.save(patient).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient
        Patient updatedPatient = patientRepository.findById(patient.getId()).block();
        updatedPatient
            .uuid(UPDATED_UUID)
            .idDocument(UPDATED_ID_DOCUMENT)
            .idNumber(UPDATED_ID_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME);
        PatientDTO patientDTO = patientMapper.toDto(updatedPatient);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, patientDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPatientToMatchAllProperties(updatedPatient);
    }

    @Test
    void putNonExistingPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, patientDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePatientWithPatch() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.save(patient).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient using partial update
        Patient partialUpdatedPatient = new Patient();
        partialUpdatedPatient.setId(patient.getId());

        partialUpdatedPatient.uuid(UPDATED_UUID).idNumber(UPDATED_ID_NUMBER).firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPatient.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPatient))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Patient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPatient, patient), getPersistedPatient(patient));
    }

    @Test
    void fullUpdatePatientWithPatch() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.save(patient).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient using partial update
        Patient partialUpdatedPatient = new Patient();
        partialUpdatedPatient.setId(patient.getId());

        partialUpdatedPatient
            .uuid(UPDATED_UUID)
            .idDocument(UPDATED_ID_DOCUMENT)
            .idNumber(UPDATED_ID_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPatient.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPatient))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Patient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatientUpdatableFieldsEquals(partialUpdatedPatient, getPersistedPatient(partialUpdatedPatient));
    }

    @Test
    void patchNonExistingPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, patientDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(patientDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePatient() {
        // Initialize the database
        insertedPatient = patientRepository.save(patient).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the patient
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, patient.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return patientRepository.count().block();
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

    protected Patient getPersistedPatient(Patient patient) {
        return patientRepository.findById(patient.getId()).block();
    }

    protected void assertPersistedPatientToMatchAllProperties(Patient expectedPatient) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPatientAllPropertiesEquals(expectedPatient, getPersistedPatient(expectedPatient));
        assertPatientUpdatableFieldsEquals(expectedPatient, getPersistedPatient(expectedPatient));
    }

    protected void assertPersistedPatientToMatchUpdatableProperties(Patient expectedPatient) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPatientAllUpdatablePropertiesEquals(expectedPatient, getPersistedPatient(expectedPatient));
        assertPatientUpdatableFieldsEquals(expectedPatient, getPersistedPatient(expectedPatient));
    }
}
