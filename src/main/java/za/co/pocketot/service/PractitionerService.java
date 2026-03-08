package za.co.pocketot.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.service.dto.PractitionerDTO;

/**
 * Service Interface for managing {@link za.co.pocketot.domain.Practitioner}.
 */
public interface PractitionerService {
    /**
     * Save a practitioner.
     *
     * @param practitionerDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PractitionerDTO> save(PractitionerDTO practitionerDTO);

    /**
     * Updates a practitioner.
     *
     * @param practitionerDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PractitionerDTO> update(PractitionerDTO practitionerDTO);

    /**
     * Partially updates a practitioner.
     *
     * @param practitionerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PractitionerDTO> partialUpdate(PractitionerDTO practitionerDTO);

    /**
     * Get all the practitioners.
     *
     * @return the list of entities.
     */
    Flux<PractitionerDTO> findAll();

    /**
     * Returns the number of practitioners available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" practitioner.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PractitionerDTO> findOne(Long id);

    /**
     * Delete the "id" practitioner.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
