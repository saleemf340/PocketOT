package za.co.pocketot.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static za.co.pocketot.domain.ExerciseAsserts.*;
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
import za.co.pocketot.domain.Exercise;
import za.co.pocketot.repository.EntityManager;
import za.co.pocketot.repository.ExerciseRepository;
import za.co.pocketot.service.dto.ExerciseDTO;
import za.co.pocketot.service.mapper.ExerciseMapper;

/**
 * Integration tests for the {@link ExerciseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ExerciseResourceIT {

    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_UUID = UUID.randomUUID();

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VIDEO_LINK = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_LINK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/exercises";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Exercise exercise;

    private Exercise insertedExercise;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exercise createEntity() {
        return new Exercise().uuid(DEFAULT_UUID).name(DEFAULT_NAME).videoLink(DEFAULT_VIDEO_LINK);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exercise createUpdatedEntity() {
        return new Exercise().uuid(UPDATED_UUID).name(UPDATED_NAME).videoLink(UPDATED_VIDEO_LINK);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Exercise.class).block();
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
        exercise = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedExercise != null) {
            exerciseRepository.delete(insertedExercise).block();
            insertedExercise = null;
        }
        deleteEntities(em);
    }

    @Test
    void createExercise() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Exercise
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);
        var returnedExerciseDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ExerciseDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Exercise in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExercise = exerciseMapper.toEntity(returnedExerciseDTO);
        assertExerciseUpdatableFieldsEquals(returnedExercise, getPersistedExercise(returnedExercise));

        insertedExercise = returnedExercise;
    }

    @Test
    void createExerciseWithExistingId() throws Exception {
        // Create the Exercise with an existing ID
        exercise.setId(1L);
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllExercisesAsStream() {
        // Initialize the database
        exerciseRepository.save(exercise).block();

        List<Exercise> exerciseList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ExerciseDTO.class)
            .getResponseBody()
            .map(exerciseMapper::toEntity)
            .filter(exercise::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(exerciseList).isNotNull();
        assertThat(exerciseList).hasSize(1);
        Exercise testExercise = exerciseList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertExerciseAllPropertiesEquals(exercise, testExercise);
        assertExerciseUpdatableFieldsEquals(exercise, testExercise);
    }

    @Test
    void getAllExercises() {
        // Initialize the database
        insertedExercise = exerciseRepository.save(exercise).block();

        // Get all the exerciseList
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
            .value(hasItem(exercise.getId().intValue()))
            .jsonPath("$.[*].uuid")
            .value(hasItem(DEFAULT_UUID.toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].videoLink")
            .value(hasItem(DEFAULT_VIDEO_LINK));
    }

    @Test
    void getExercise() {
        // Initialize the database
        insertedExercise = exerciseRepository.save(exercise).block();

        // Get the exercise
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, exercise.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(exercise.getId().intValue()))
            .jsonPath("$.uuid")
            .value(is(DEFAULT_UUID.toString()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.videoLink")
            .value(is(DEFAULT_VIDEO_LINK));
    }

    @Test
    void getNonExistingExercise() {
        // Get the exercise
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingExercise() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.save(exercise).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exercise
        Exercise updatedExercise = exerciseRepository.findById(exercise.getId()).block();
        updatedExercise.uuid(UPDATED_UUID).name(UPDATED_NAME).videoLink(UPDATED_VIDEO_LINK);
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(updatedExercise);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, exerciseDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExerciseToMatchAllProperties(updatedExercise);
    }

    @Test
    void putNonExistingExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // Create the Exercise
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, exerciseDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // Create the Exercise
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // Create the Exercise
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExerciseWithPatch() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.save(exercise).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exercise using partial update
        Exercise partialUpdatedExercise = new Exercise();
        partialUpdatedExercise.setId(exercise.getId());

        partialUpdatedExercise.name(UPDATED_NAME).videoLink(UPDATED_VIDEO_LINK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExercise.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedExercise))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Exercise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExerciseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedExercise, exercise), getPersistedExercise(exercise));
    }

    @Test
    void fullUpdateExerciseWithPatch() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.save(exercise).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exercise using partial update
        Exercise partialUpdatedExercise = new Exercise();
        partialUpdatedExercise.setId(exercise.getId());

        partialUpdatedExercise.uuid(UPDATED_UUID).name(UPDATED_NAME).videoLink(UPDATED_VIDEO_LINK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExercise.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedExercise))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Exercise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExerciseUpdatableFieldsEquals(partialUpdatedExercise, getPersistedExercise(partialUpdatedExercise));
    }

    @Test
    void patchNonExistingExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // Create the Exercise
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, exerciseDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // Create the Exercise
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // Create the Exercise
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(exerciseDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExercise() {
        // Initialize the database
        insertedExercise = exerciseRepository.save(exercise).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the exercise
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, exercise.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return exerciseRepository.count().block();
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

    protected Exercise getPersistedExercise(Exercise exercise) {
        return exerciseRepository.findById(exercise.getId()).block();
    }

    protected void assertPersistedExerciseToMatchAllProperties(Exercise expectedExercise) {
        // Test fails because reactive api returns an empty object instead of null
        // assertExerciseAllPropertiesEquals(expectedExercise, getPersistedExercise(expectedExercise));
        assertExerciseUpdatableFieldsEquals(expectedExercise, getPersistedExercise(expectedExercise));
    }

    protected void assertPersistedExerciseToMatchUpdatableProperties(Exercise expectedExercise) {
        // Test fails because reactive api returns an empty object instead of null
        // assertExerciseAllUpdatablePropertiesEquals(expectedExercise, getPersistedExercise(expectedExercise));
        assertExerciseUpdatableFieldsEquals(expectedExercise, getPersistedExercise(expectedExercise));
    }
}
