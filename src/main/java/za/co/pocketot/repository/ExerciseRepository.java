package za.co.pocketot.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.domain.Exercise;

/**
 * Spring Data R2DBC repository for the Exercise entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExerciseRepository extends ReactiveCrudRepository<Exercise, Long>, ExerciseRepositoryInternal {
    @Override
    <S extends Exercise> Mono<S> save(S entity);

    @Override
    Flux<Exercise> findAll();

    @Override
    Mono<Exercise> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ExerciseRepositoryInternal {
    <S extends Exercise> Mono<S> save(S entity);

    Flux<Exercise> findAllBy(Pageable pageable);

    Flux<Exercise> findAll();

    Mono<Exercise> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Exercise> findAllBy(Pageable pageable, Criteria criteria);
}
