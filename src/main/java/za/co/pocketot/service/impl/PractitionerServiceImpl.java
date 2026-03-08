package za.co.pocketot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.repository.PractitionerRepository;
import za.co.pocketot.service.PractitionerService;
import za.co.pocketot.service.dto.PractitionerDTO;
import za.co.pocketot.service.mapper.PractitionerMapper;

/**
 * Service Implementation for managing {@link za.co.pocketot.domain.Practitioner}.
 */
@Service
@Transactional
public class PractitionerServiceImpl implements PractitionerService {

    private static final Logger LOG = LoggerFactory.getLogger(PractitionerServiceImpl.class);

    private final PractitionerRepository practitionerRepository;

    private final PractitionerMapper practitionerMapper;

    public PractitionerServiceImpl(PractitionerRepository practitionerRepository, PractitionerMapper practitionerMapper) {
        this.practitionerRepository = practitionerRepository;
        this.practitionerMapper = practitionerMapper;
    }

    @Override
    public Mono<PractitionerDTO> save(PractitionerDTO practitionerDTO) {
        LOG.debug("Request to save Practitioner : {}", practitionerDTO);
        return practitionerRepository.save(practitionerMapper.toEntity(practitionerDTO)).map(practitionerMapper::toDto);
    }

    @Override
    public Mono<PractitionerDTO> update(PractitionerDTO practitionerDTO) {
        LOG.debug("Request to update Practitioner : {}", practitionerDTO);
        return practitionerRepository.save(practitionerMapper.toEntity(practitionerDTO)).map(practitionerMapper::toDto);
    }

    @Override
    public Mono<PractitionerDTO> partialUpdate(PractitionerDTO practitionerDTO) {
        LOG.debug("Request to partially update Practitioner : {}", practitionerDTO);

        return practitionerRepository
            .findById(practitionerDTO.getId())
            .map(existingPractitioner -> {
                practitionerMapper.partialUpdate(existingPractitioner, practitionerDTO);

                return existingPractitioner;
            })
            .flatMap(practitionerRepository::save)
            .map(practitionerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PractitionerDTO> findAll() {
        LOG.debug("Request to get all Practitioners");
        return practitionerRepository.findAll().map(practitionerMapper::toDto);
    }

    public Mono<Long> countAll() {
        return practitionerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PractitionerDTO> findOne(Long id) {
        LOG.debug("Request to get Practitioner : {}", id);
        return practitionerRepository.findById(id).map(practitionerMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Practitioner : {}", id);
        return practitionerRepository.deleteById(id);
    }
}
