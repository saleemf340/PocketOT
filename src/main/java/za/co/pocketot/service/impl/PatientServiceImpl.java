package za.co.pocketot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.repository.PatientRepository;
import za.co.pocketot.service.PatientService;
import za.co.pocketot.service.dto.PatientDTO;
import za.co.pocketot.service.mapper.PatientMapper;

/**
 * Service Implementation for managing {@link za.co.pocketot.domain.Patient}.
 */
@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private static final Logger LOG = LoggerFactory.getLogger(PatientServiceImpl.class);

    private final PatientRepository patientRepository;

    private final PatientMapper patientMapper;

    public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public Mono<PatientDTO> save(PatientDTO patientDTO) {
        LOG.debug("Request to save Patient : {}", patientDTO);
        return patientRepository.save(patientMapper.toEntity(patientDTO)).map(patientMapper::toDto);
    }

    @Override
    public Mono<PatientDTO> update(PatientDTO patientDTO) {
        LOG.debug("Request to update Patient : {}", patientDTO);
        return patientRepository.save(patientMapper.toEntity(patientDTO)).map(patientMapper::toDto);
    }

    @Override
    public Mono<PatientDTO> partialUpdate(PatientDTO patientDTO) {
        LOG.debug("Request to partially update Patient : {}", patientDTO);

        return patientRepository
            .findById(patientDTO.getId())
            .map(existingPatient -> {
                patientMapper.partialUpdate(existingPatient, patientDTO);

                return existingPatient;
            })
            .flatMap(patientRepository::save)
            .map(patientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PatientDTO> findAll() {
        LOG.debug("Request to get all Patients");
        return patientRepository.findAll().map(patientMapper::toDto);
    }

    public Mono<Long> countAll() {
        return patientRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PatientDTO> findOne(Long id) {
        LOG.debug("Request to get Patient : {}", id);
        return patientRepository.findById(id).map(patientMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Patient : {}", id);
        return patientRepository.deleteById(id);
    }
}
