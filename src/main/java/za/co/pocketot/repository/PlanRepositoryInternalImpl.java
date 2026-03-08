package za.co.pocketot.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.pocketot.domain.Plan;
import za.co.pocketot.repository.rowmapper.ExerciseRowMapper;
import za.co.pocketot.repository.rowmapper.PatientRowMapper;
import za.co.pocketot.repository.rowmapper.PlanRowMapper;
import za.co.pocketot.repository.rowmapper.PractitionerRowMapper;

/**
 * Spring Data R2DBC custom repository implementation for the Plan entity.
 */
@SuppressWarnings("unused")
class PlanRepositoryInternalImpl extends SimpleR2dbcRepository<Plan, Long> implements PlanRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PatientRowMapper patientMapper;
    private final PractitionerRowMapper practitionerMapper;
    private final ExerciseRowMapper exerciseMapper;
    private final PlanRowMapper planMapper;

    private static final Table entityTable = Table.aliased("plan", EntityManager.ENTITY_ALIAS);
    private static final Table patientTable = Table.aliased("patient", "patient");
    private static final Table practitionerTable = Table.aliased("practitioner", "practitioner");
    private static final Table exerciseTable = Table.aliased("exercise", "exercise");

    public PlanRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PatientRowMapper patientMapper,
        PractitionerRowMapper practitionerMapper,
        ExerciseRowMapper exerciseMapper,
        PlanRowMapper planMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Plan.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.patientMapper = patientMapper;
        this.practitionerMapper = practitionerMapper;
        this.exerciseMapper = exerciseMapper;
        this.planMapper = planMapper;
    }

    @Override
    public Flux<Plan> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Plan> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PlanSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PatientSqlHelper.getColumns(patientTable, "patient"));
        columns.addAll(PractitionerSqlHelper.getColumns(practitionerTable, "practitioner"));
        columns.addAll(ExerciseSqlHelper.getColumns(exerciseTable, "exercise"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(patientTable)
            .on(Column.create("patient_id", entityTable))
            .equals(Column.create("id", patientTable))
            .leftOuterJoin(practitionerTable)
            .on(Column.create("practitioner_id", entityTable))
            .equals(Column.create("id", practitionerTable))
            .leftOuterJoin(exerciseTable)
            .on(Column.create("exercise_id", entityTable))
            .equals(Column.create("id", exerciseTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Plan.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Plan> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Plan> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Plan process(Row row, RowMetadata metadata) {
        Plan entity = planMapper.apply(row, "e");
        entity.setPatient(patientMapper.apply(row, "patient"));
        entity.setPractitioner(practitionerMapper.apply(row, "practitioner"));
        entity.setExercise(exerciseMapper.apply(row, "exercise"));
        return entity;
    }

    @Override
    public <S extends Plan> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
