package za.co.pocketot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.repository.OrganizationRepository;
import za.co.pocketot.service.OrganizationService;
import za.co.pocketot.service.dto.OrganizationDTO;
import za.co.pocketot.service.mapper.OrganizationMapper;

/**
 * Service Implementation for managing {@link za.co.pocketot.domain.Organization}.
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository organizationRepository;

    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public Mono<OrganizationDTO> save(OrganizationDTO organizationDTO) {
        LOG.debug("Request to save Organization : {}", organizationDTO);
        return organizationRepository.save(organizationMapper.toEntity(organizationDTO)).map(organizationMapper::toDto);
    }

    @Override
    public Mono<OrganizationDTO> update(OrganizationDTO organizationDTO) {
        LOG.debug("Request to update Organization : {}", organizationDTO);
        return organizationRepository.save(organizationMapper.toEntity(organizationDTO)).map(organizationMapper::toDto);
    }

    @Override
    public Mono<OrganizationDTO> partialUpdate(OrganizationDTO organizationDTO) {
        LOG.debug("Request to partially update Organization : {}", organizationDTO);

        return organizationRepository
            .findById(organizationDTO.getId())
            .map(existingOrganization -> {
                organizationMapper.partialUpdate(existingOrganization, organizationDTO);

                return existingOrganization;
            })
            .flatMap(organizationRepository::save)
            .map(organizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrganizationDTO> findAll() {
        LOG.debug("Request to get all Organizations");
        return organizationRepository.findAll().map(organizationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return organizationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrganizationDTO> findOne(Long id) {
        LOG.debug("Request to get Organization : {}", id);
        return organizationRepository.findById(id).map(organizationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Organization : {}", id);
        return organizationRepository.deleteById(id);
    }
}
