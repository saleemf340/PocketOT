package za.co.pocketot.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.service.dto.ExerciseDTO;

/**
 * Service Interface for managing {@link za.co.pocketot.domain.Exercise}.
 */
public interface ExerciseService {
    /**
     * Save a exercise.
     *
     * @param exerciseDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ExerciseDTO> save(ExerciseDTO exerciseDTO);

    /**
     * Updates a exercise.
     *
     * @param exerciseDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ExerciseDTO> update(ExerciseDTO exerciseDTO);

    /**
     * Partially updates a exercise.
     *
     * @param exerciseDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ExerciseDTO> partialUpdate(ExerciseDTO exerciseDTO);

    /**
     * Get all the exercises.
     *
     * @return the list of entities.
     */
    Flux<ExerciseDTO> findAll();

    /**
     * Returns the number of exercises available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" exercise.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ExerciseDTO> findOne(Long id);

    /**
     * Delete the "id" exercise.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
