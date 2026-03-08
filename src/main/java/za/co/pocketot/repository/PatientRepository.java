package za.co.pocketot.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.domain.Patient;

/**
 * Spring Data R2DBC repository for the Patient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatientRepository extends ReactiveCrudRepository<Patient, Long>, PatientRepositoryInternal {
    @Override
    <S extends Patient> Mono<S> save(S entity);

    @Override
    Flux<Patient> findAll();

    @Override
    Mono<Patient> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PatientRepositoryInternal {
    <S extends Patient> Mono<S> save(S entity);

    Flux<Patient> findAllBy(Pageable pageable);

    Flux<Patient> findAll();

    Mono<Patient> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Patient> findAllBy(Pageable pageable, Criteria criteria);
}
