package za.co.pocketot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.repository.ExerciseRepository;
import za.co.pocketot.service.ExerciseService;
import za.co.pocketot.service.dto.ExerciseDTO;
import za.co.pocketot.service.mapper.ExerciseMapper;

/**
 * Service Implementation for managing {@link za.co.pocketot.domain.Exercise}.
 */
@Service
@Transactional
public class ExerciseServiceImpl implements ExerciseService {

    private static final Logger LOG = LoggerFactory.getLogger(ExerciseServiceImpl.class);

    private final ExerciseRepository exerciseRepository;

    private final ExerciseMapper exerciseMapper;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseMapper = exerciseMapper;
    }

    @Override
    public Mono<ExerciseDTO> save(ExerciseDTO exerciseDTO) {
        LOG.debug("Request to save Exercise : {}", exerciseDTO);
        return exerciseRepository.save(exerciseMapper.toEntity(exerciseDTO)).map(exerciseMapper::toDto);
    }

    @Override
    public Mono<ExerciseDTO> update(ExerciseDTO exerciseDTO) {
        LOG.debug("Request to update Exercise : {}", exerciseDTO);
        return exerciseRepository.save(exerciseMapper.toEntity(exerciseDTO)).map(exerciseMapper::toDto);
    }

    @Override
    public Mono<ExerciseDTO> partialUpdate(ExerciseDTO exerciseDTO) {
        LOG.debug("Request to partially update Exercise : {}", exerciseDTO);

        return exerciseRepository
            .findById(exerciseDTO.getId())
            .map(existingExercise -> {
                exerciseMapper.partialUpdate(existingExercise, exerciseDTO);

                return existingExercise;
            })
            .flatMap(exerciseRepository::save)
            .map(exerciseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ExerciseDTO> findAll() {
        LOG.debug("Request to get all Exercises");
        return exerciseRepository.findAll().map(exerciseMapper::toDto);
    }

    public Mono<Long> countAll() {
        return exerciseRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ExerciseDTO> findOne(Long id) {
        LOG.debug("Request to get Exercise : {}", id);
        return exerciseRepository.findById(id).map(exerciseMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Exercise : {}", id);
        return exerciseRepository.deleteById(id);
    }
}
