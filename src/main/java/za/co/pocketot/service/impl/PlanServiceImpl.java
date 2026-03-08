package za.co.pocketot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.repository.PlanRepository;
import za.co.pocketot.service.PlanService;
import za.co.pocketot.service.dto.PlanDTO;
import za.co.pocketot.service.mapper.PlanMapper;

/**
 * Service Implementation for managing {@link za.co.pocketot.domain.Plan}.
 */
@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanServiceImpl.class);

    private final PlanRepository planRepository;

    private final PlanMapper planMapper;

    public PlanServiceImpl(PlanRepository planRepository, PlanMapper planMapper) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
    }

    @Override
    public Mono<PlanDTO> save(PlanDTO planDTO) {
        LOG.debug("Request to save Plan : {}", planDTO);
        return planRepository.save(planMapper.toEntity(planDTO)).map(planMapper::toDto);
    }

    @Override
    public Mono<PlanDTO> update(PlanDTO planDTO) {
        LOG.debug("Request to update Plan : {}", planDTO);
        return planRepository.save(planMapper.toEntity(planDTO)).map(planMapper::toDto);
    }

    @Override
    public Mono<PlanDTO> partialUpdate(PlanDTO planDTO) {
        LOG.debug("Request to partially update Plan : {}", planDTO);

        return planRepository
            .findById(planDTO.getId())
            .map(existingPlan -> {
                planMapper.partialUpdate(existingPlan, planDTO);

                return existingPlan;
            })
            .flatMap(planRepository::save)
            .map(planMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlanDTO> findAll() {
        LOG.debug("Request to get all Plans");
        return planRepository.findAll().map(planMapper::toDto);
    }

    public Mono<Long> countAll() {
        return planRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlanDTO> findOne(Long id) {
        LOG.debug("Request to get Plan : {}", id);
        return planRepository.findById(id).map(planMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Plan : {}", id);
        return planRepository.deleteById(id);
    }
}
