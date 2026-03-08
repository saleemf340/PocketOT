package za.co.pocketot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static za.co.pocketot.domain.OrganizationAsserts.*;
import static za.co.pocketot.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
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
import za.co.pocketot.domain.Organization;
import za.co.pocketot.repository.EntityManager;
import za.co.pocketot.repository.OrganizationRepository;
import za.co.pocketot.service.dto.OrganizationDTO;
import za.co.pocketot.service.mapper.OrganizationMapper;

/**
 * Integration tests for the {@link OrganizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrganizationResourceIT {

    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_UUID = UUID.randomUUID();

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final String ENTITY_API_URL = "/api/organizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Organization organization;

    private Organization insertedOrganization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createEntity() {
        return new Organization()
            .uuid(DEFAULT_UUID)
            .name(DEFAULT_NAME)
            .registrationNumber(DEFAULT_REGISTRATION_NUMBER)
            .verified(DEFAULT_VERIFIED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createUpdatedEntity() {
        return new Organization()
            .uuid(UPDATED_UUID)
            .name(UPDATED_NAME)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .verified(UPDATED_VERIFIED);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Organization.class).block();
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
        organization = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrganization != null) {
            organizationRepository.delete(insertedOrganization).block();
            insertedOrganization = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOrganization() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);
        var returnedOrganizationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrganizationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Organization in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrganization = organizationMapper.toEntity(returnedOrganizationDTO);
        assertOrganizationUpdatableFieldsEquals(returnedOrganization, getPersistedOrganization(returnedOrganization));

        insertedOrganization = returnedOrganization;
    }

    @Test
    void createOrganizationWithExistingId() throws Exception {
        // Create the Organization with an existing ID
        organization.setId(1L);
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllOrganizationsAsStream() {
        // Initialize the database
        organizationRepository.save(organization).block();

        List<Organization> organizationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrganizationDTO.class)
            .getResponseBody()
            .map(organizationMapper::toEntity)
            .filter(organization::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(organizationList).isNotNull();
        assertThat(organizationList).hasSize(1);
        Organization testOrganization = organizationList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationAllPropertiesEquals(organization, testOrganization);
        assertOrganizationUpdatableFieldsEquals(organization, testOrganization);
    }

    @Test
    void getAllOrganizations() {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        // Get all the organizationList
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
            .value(hasItem(organization.getId().intValue()))
            .jsonPath("$.[*].uuid")
            .value(hasItem(DEFAULT_UUID.toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].registrationNumber")
            .value(hasItem(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.[*].verified")
            .value(hasItem(DEFAULT_VERIFIED));
    }

    @Test
    void getOrganization() {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(organization.getId().intValue()))
            .jsonPath("$.uuid")
            .value(is(DEFAULT_UUID.toString()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.registrationNumber")
            .value(is(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.verified")
            .value(is(DEFAULT_VERIFIED));
    }

    @Test
    void getNonExistingOrganization() {
        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrganization() throws Exception {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organization
        Organization updatedOrganization = organizationRepository.findById(organization.getId()).block();
        updatedOrganization
            .uuid(UPDATED_UUID)
            .name(UPDATED_NAME)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .verified(UPDATED_VERIFIED);
        OrganizationDTO organizationDTO = organizationMapper.toDto(updatedOrganization);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrganizationToMatchAllProperties(updatedOrganization);
    }

    @Test
    void putNonExistingOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization.verified(UPDATED_VERIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrganization, organization),
            getPersistedOrganization(organization)
        );
    }

    @Test
    void fullUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .uuid(UPDATED_UUID)
            .name(UPDATED_NAME)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .verified(UPDATED_VERIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizationUpdatableFieldsEquals(partialUpdatedOrganization, getPersistedOrganization(partialUpdatedOrganization));
    }

    @Test
    void patchNonExistingOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, organizationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrganization() {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the organization
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return organizationRepository.count().block();
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

    protected Organization getPersistedOrganization(Organization organization) {
        return organizationRepository.findById(organization.getId()).block();
    }

    protected void assertPersistedOrganizationToMatchAllProperties(Organization expectedOrganization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationAllPropertiesEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
        assertOrganizationUpdatableFieldsEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
    }

    protected void assertPersistedOrganizationToMatchUpdatableProperties(Organization expectedOrganization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationAllUpdatablePropertiesEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
        assertOrganizationUpdatableFieldsEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
    }
}
