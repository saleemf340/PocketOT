package za.co.pocketot.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.domain.Practitioner;

/**
 * Spring Data R2DBC repository for the Practitioner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PractitionerRepository extends ReactiveCrudRepository<Practitioner, Long>, PractitionerRepositoryInternal {
    @Query("SELECT * FROM practitioner entity WHERE entity.organization_id = :id")
    Flux<Practitioner> findByOrganization(Long id);

    @Query("SELECT * FROM practitioner entity WHERE entity.organization_id IS NULL")
    Flux<Practitioner> findAllWhereOrganizationIsNull();

    @Override
    <S extends Practitioner> Mono<S> save(S entity);

    @Override
    Flux<Practitioner> findAll();

    @Override
    Mono<Practitioner> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PractitionerRepositoryInternal {
    <S extends Practitioner> Mono<S> save(S entity);

    Flux<Practitioner> findAllBy(Pageable pageable);

    Flux<Practitioner> findAll();

    Mono<Practitioner> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Practitioner> findAllBy(Pageable pageable, Criteria criteria);
}
