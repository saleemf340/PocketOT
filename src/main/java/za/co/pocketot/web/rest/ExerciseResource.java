package za.co.pocketot.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;
import za.co.pocketot.repository.ExerciseRepository;
import za.co.pocketot.service.ExerciseService;
import za.co.pocketot.service.dto.ExerciseDTO;
import za.co.pocketot.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link za.co.pocketot.domain.Exercise}.
 */
@RestController
@RequestMapping("/api/exercises")
public class ExerciseResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExerciseResource.class);

    private static final String ENTITY_NAME = "exercise";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExerciseService exerciseService;

    private final ExerciseRepository exerciseRepository;

    public ExerciseResource(ExerciseService exerciseService, ExerciseRepository exerciseRepository) {
        this.exerciseService = exerciseService;
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * {@code POST  /exercises} : Create a new exercise.
     *
     * @param exerciseDTO the exerciseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exerciseDTO, or with status {@code 400 (Bad Request)} if the exercise has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ExerciseDTO>> createExercise(@RequestBody ExerciseDTO exerciseDTO) throws URISyntaxException {
        LOG.debug("REST request to save Exercise : {}", exerciseDTO);
        if (exerciseDTO.getId() != null) {
            throw new BadRequestAlertException("A new exercise cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return exerciseService
            .save(exerciseDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/exercises/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /exercises/:id} : Updates an existing exercise.
     *
     * @param id the id of the exerciseDTO to save.
     * @param exerciseDTO the exerciseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exerciseDTO,
     * or with status {@code 400 (Bad Request)} if the exerciseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exerciseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ExerciseDTO>> updateExercise(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExerciseDTO exerciseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Exercise : {}, {}", id, exerciseDTO);
        if (exerciseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exerciseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return exerciseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return exerciseService
                    .update(exerciseDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /exercises/:id} : Partial updates given fields of an existing exercise, field will ignore if it is null
     *
     * @param id the id of the exerciseDTO to save.
     * @param exerciseDTO the exerciseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exerciseDTO,
     * or with status {@code 400 (Bad Request)} if the exerciseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the exerciseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the exerciseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ExerciseDTO>> partialUpdateExercise(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExerciseDTO exerciseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Exercise partially : {}, {}", id, exerciseDTO);
        if (exerciseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exerciseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return exerciseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ExerciseDTO> result = exerciseService.partialUpdate(exerciseDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /exercises} : get all the exercises.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exercises in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ExerciseDTO>> getAllExercises() {
        LOG.debug("REST request to get all Exercises");
        return exerciseService.findAll().collectList();
    }

    /**
     * {@code GET  /exercises} : get all the exercises as a stream.
     * @return the {@link Flux} of exercises.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ExerciseDTO> getAllExercisesAsStream() {
        LOG.debug("REST request to get all Exercises as a stream");
        return exerciseService.findAll();
    }

    /**
     * {@code GET  /exercises/:id} : get the "id" exercise.
     *
     * @param id the id of the exerciseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exerciseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ExerciseDTO>> getExercise(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Exercise : {}", id);
        Mono<ExerciseDTO> exerciseDTO = exerciseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(exerciseDTO);
    }

    /**
     * {@code DELETE  /exercises/:id} : delete the "id" exercise.
     *
     * @param id the id of the exerciseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteExercise(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Exercise : {}", id);
        return exerciseService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
