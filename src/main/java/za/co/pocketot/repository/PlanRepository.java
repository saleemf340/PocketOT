package za.co.pocketot.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.domain.Plan;

/**
 * Spring Data R2DBC repository for the Plan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanRepository extends ReactiveCrudRepository<Plan, Long>, PlanRepositoryInternal {
    @Query("SELECT * FROM plan entity WHERE entity.patient_id = :id")
    Flux<Plan> findByPatient(Long id);

    @Query("SELECT * FROM plan entity WHERE entity.patient_id IS NULL")
    Flux<Plan> findAllWherePatientIsNull();

    @Query("SELECT * FROM plan entity WHERE entity.practitioner_id = :id")
    Flux<Plan> findByPractitioner(Long id);

    @Query("SELECT * FROM plan entity WHERE entity.practitioner_id IS NULL")
    Flux<Plan> findAllWherePractitionerIsNull();

    @Query("SELECT * FROM plan entity WHERE entity.exercise_id = :id")
    Flux<Plan> findByExercise(Long id);

    @Query("SELECT * FROM plan entity WHERE entity.exercise_id IS NULL")
    Flux<Plan> findAllWhereExerciseIsNull();

    @Override
    <S extends Plan> Mono<S> save(S entity);

    @Override
    Flux<Plan> findAll();

    @Override
    Mono<Plan> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlanRepositoryInternal {
    <S extends Plan> Mono<S> save(S entity);

    Flux<Plan> findAllBy(Pageable pageable);

    Flux<Plan> findAll();

    Mono<Plan> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Plan> findAllBy(Pageable pageable, Criteria criteria);
}
