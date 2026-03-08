package za.co.pocketot.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.service.dto.PatientDTO;

/**
 * Service Interface for managing {@link za.co.pocketot.domain.Patient}.
 */
public interface PatientService {
    /**
     * Save a patient.
     *
     * @param patientDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PatientDTO> save(PatientDTO patientDTO);

    /**
     * Updates a patient.
     *
     * @param patientDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PatientDTO> update(PatientDTO patientDTO);

    /**
     * Partially updates a patient.
     *
     * @param patientDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PatientDTO> partialUpdate(PatientDTO patientDTO);

    /**
     * Get all the patients.
     *
     * @return the list of entities.
     */
    Flux<PatientDTO> findAll();

    /**
     * Returns the number of patients available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" patient.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PatientDTO> findOne(Long id);

    /**
     * Delete the "id" patient.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
