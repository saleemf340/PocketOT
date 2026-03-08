package za.co.pocketot.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.domain.Exercise;
import za.co.pocketot.repository.rowmapper.ExerciseRowMapper;

/**
 * Spring Data R2DBC custom repository implementation for the Exercise entity.
 */
@SuppressWarnings("unused")
class ExerciseRepositoryInternalImpl extends SimpleR2dbcRepository<Exercise, Long> implements ExerciseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ExerciseRowMapper exerciseMapper;

    private static final Table entityTable = Table.aliased("exercise", EntityManager.ENTITY_ALIAS);

    public ExerciseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ExerciseRowMapper exerciseMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Exercise.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.exerciseMapper = exerciseMapper;
    }

    @Override
    public Flux<Exercise> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Exercise> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ExerciseSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Exercise.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Exercise> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Exercise> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Exercise process(Row row, RowMetadata metadata) {
        Exercise entity = exerciseMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Exercise> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
