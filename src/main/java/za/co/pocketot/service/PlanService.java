package za.co.pocketot.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.service.dto.PlanDTO;

/**
 * Service Interface for managing {@link za.co.pocketot.domain.Plan}.
 */
public interface PlanService {
    /**
     * Save a plan.
     *
     * @param planDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PlanDTO> save(PlanDTO planDTO);

    /**
     * Updates a plan.
     *
     * @param planDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PlanDTO> update(PlanDTO planDTO);

    /**
     * Partially updates a plan.
     *
     * @param planDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PlanDTO> partialUpdate(PlanDTO planDTO);

    /**
     * Get all the plans.
     *
     * @return the list of entities.
     */
    Flux<PlanDTO> findAll();

    /**
     * Returns the number of plans available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" plan.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PlanDTO> findOne(Long id);

    /**
     * Delete the "id" plan.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
