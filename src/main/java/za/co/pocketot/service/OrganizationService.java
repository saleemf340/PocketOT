package za.co.pocketot.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.service.dto.OrganizationDTO;

/**
 * Service Interface for managing {@link za.co.pocketot.domain.Organization}.
 */
public interface OrganizationService {
    /**
     * Save a organization.
     *
     * @param organizationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrganizationDTO> save(OrganizationDTO organizationDTO);

    /**
     * Updates a organization.
     *
     * @param organizationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrganizationDTO> update(OrganizationDTO organizationDTO);

    /**
     * Partially updates a organization.
     *
     * @param organizationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrganizationDTO> partialUpdate(OrganizationDTO organizationDTO);

    /**
     * Get all the organizations.
     *
     * @return the list of entities.
     */
    Flux<OrganizationDTO> findAll();

    /**
     * Returns the number of organizations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" organization.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrganizationDTO> findOne(Long id);

    /**
     * Delete the "id" organization.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
