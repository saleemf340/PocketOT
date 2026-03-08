package za.co.pocketot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static za.co.pocketot.domain.PlanAsserts.*;
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
import za.co.pocketot.domain.Plan;
import za.co.pocketot.repository.EntityManager;
import za.co.pocketot.repository.PlanRepository;
import za.co.pocketot.service.dto.PlanDTO;
import za.co.pocketot.service.mapper.PlanMapper;

/**
 * Integration tests for the {@link PlanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlanResourceIT {

    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_UUID = UUID.randomUUID();

    private static final Integer DEFAULT_EXERCISE_REPITITION = 1;
    private static final Integer UPDATED_EXERCISE_REPITITION = 2;

    private static final Integer DEFAULT_PLAN_REPITITION = 1;
    private static final Integer UPDATED_PLAN_REPITITION = 2;

    private static final LocalDate DEFAULT_EFFECTIVE_FROM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_FROM = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_EFFECTIVE_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_TO = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Plan plan;

    private Plan insertedPlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plan createEntity() {
        return new Plan()
            .uuid(DEFAULT_UUID)
            .exerciseRepitition(DEFAULT_EXERCISE_REPITITION)
            .planRepitition(DEFAULT_PLAN_REPITITION)
            .effectiveFrom(DEFAULT_EFFECTIVE_FROM)
            .effectiveTo(DEFAULT_EFFECTIVE_TO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plan createUpdatedEntity() {
        return new Plan()
            .uuid(UPDATED_UUID)
            .exerciseRepitition(UPDATED_EXERCISE_REPITITION)
            .planRepitition(UPDATED_PLAN_REPITITION)
            .effectiveFrom(UPDATED_EFFECTIVE_FROM)
            .effectiveTo(UPDATED_EFFECTIVE_TO);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Plan.class).block();
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
        plan = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPlan != null) {
            planRepository.delete(insertedPlan).block();
            insertedPlan = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPlan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);
        var returnedPlanDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlanDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Plan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlan = planMapper.toEntity(returnedPlanDTO);
        assertPlanUpdatableFieldsEquals(returnedPlan, getPersistedPlan(returnedPlan));

        insertedPlan = returnedPlan;
    }

    @Test
    void createPlanWithExistingId() throws Exception {
        // Create the Plan with an existing ID
        plan.setId(1L);
        PlanDTO planDTO = planMapper.toDto(plan);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPlansAsStream() {
        // Initialize the database
        planRepository.save(plan).block();

        List<Plan> planList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PlanDTO.class)
            .getResponseBody()
            .map(planMapper::toEntity)
            .filter(plan::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(planList).isNotNull();
        assertThat(planList).hasSize(1);
        Plan testPlan = planList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertPlanAllPropertiesEquals(plan, testPlan);
        assertPlanUpdatableFieldsEquals(plan, testPlan);
    }

    @Test
    void getAllPlans() {
        // Initialize the database
        insertedPlan = planRepository.save(plan).block();

        // Get all the planList
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
            .value(hasItem(plan.getId().intValue()))
            .jsonPath("$.[*].uuid")
            .value(hasItem(DEFAULT_UUID.toString()))
            .jsonPath("$.[*].exerciseRepitition")
            .value(hasItem(DEFAULT_EXERCISE_REPITITION))
            .jsonPath("$.[*].planRepitition")
            .value(hasItem(DEFAULT_PLAN_REPITITION))
            .jsonPath("$.[*].effectiveFrom")
            .value(hasItem(DEFAULT_EFFECTIVE_FROM.toString()))
            .jsonPath("$.[*].effectiveTo")
            .value(hasItem(DEFAULT_EFFECTIVE_TO.toString()));
    }

    @Test
    void getPlan() {
        // Initialize the database
        insertedPlan = planRepository.save(plan).block();

        // Get the plan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, plan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(plan.getId().intValue()))
            .jsonPath("$.uuid")
            .value(is(DEFAULT_UUID.toString()))
            .jsonPath("$.exerciseRepitition")
            .value(is(DEFAULT_EXERCISE_REPITITION))
            .jsonPath("$.planRepitition")
            .value(is(DEFAULT_PLAN_REPITITION))
            .jsonPath("$.effectiveFrom")
            .value(is(DEFAULT_EFFECTIVE_FROM.toString()))
            .jsonPath("$.effectiveTo")
            .value(is(DEFAULT_EFFECTIVE_TO.toString()));
    }

    @Test
    void getNonExistingPlan() {
        // Get the plan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlan() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.save(plan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plan
        Plan updatedPlan = planRepository.findById(plan.getId()).block();
        updatedPlan
            .uuid(UPDATED_UUID)
            .exerciseRepitition(UPDATED_EXERCISE_REPITITION)
            .planRepitition(UPDATED_PLAN_REPITITION)
            .effectiveFrom(UPDATED_EFFECTIVE_FROM)
            .effectiveTo(UPDATED_EFFECTIVE_TO);
        PlanDTO planDTO = planMapper.toDto(updatedPlan);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanToMatchAllProperties(updatedPlan);
    }

    @Test
    void putNonExistingPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlanWithPatch() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.save(plan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plan using partial update
        Plan partialUpdatedPlan = new Plan();
        partialUpdatedPlan.setId(plan.getId());

        partialUpdatedPlan
            .exerciseRepitition(UPDATED_EXERCISE_REPITITION)
            .planRepitition(UPDATED_PLAN_REPITITION)
            .effectiveFrom(UPDATED_EFFECTIVE_FROM)
            .effectiveTo(UPDATED_EFFECTIVE_TO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPlan, plan), getPersistedPlan(plan));
    }

    @Test
    void fullUpdatePlanWithPatch() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.save(plan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plan using partial update
        Plan partialUpdatedPlan = new Plan();
        partialUpdatedPlan.setId(plan.getId());

        partialUpdatedPlan
            .uuid(UPDATED_UUID)
            .exerciseRepitition(UPDATED_EXERCISE_REPITITION)
            .planRepitition(UPDATED_PLAN_REPITITION)
            .effectiveFrom(UPDATED_EFFECTIVE_FROM)
            .effectiveTo(UPDATED_EFFECTIVE_TO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Plan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanUpdatableFieldsEquals(partialUpdatedPlan, getPersistedPlan(partialUpdatedPlan));
    }

    @Test
    void patchNonExistingPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, planDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlan() {
        // Initialize the database
        insertedPlan = planRepository.save(plan).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the plan
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, plan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planRepository.count().block();
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

    protected Plan getPersistedPlan(Plan plan) {
        return planRepository.findById(plan.getId()).block();
    }

    protected void assertPersistedPlanToMatchAllProperties(Plan expectedPlan) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanAllPropertiesEquals(expectedPlan, getPersistedPlan(expectedPlan));
        assertPlanUpdatableFieldsEquals(expectedPlan, getPersistedPlan(expectedPlan));
    }

    protected void assertPersistedPlanToMatchUpdatableProperties(Plan expectedPlan) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanAllUpdatablePropertiesEquals(expectedPlan, getPersistedPlan(expectedPlan));
        assertPlanUpdatableFieldsEquals(expectedPlan, getPersistedPlan(expectedPlan));
    }
}
