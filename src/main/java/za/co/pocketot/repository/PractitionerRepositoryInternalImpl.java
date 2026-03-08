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
import za.co.pocketot.domain.Practitioner;
import za.co.pocketot.repository.rowmapper.OrganizationRowMapper;
import za.co.pocketot.repository.rowmapper.PractitionerRowMapper;

/**
 * Spring Data R2DBC custom repository implementation for the Practitioner entity.
 */
@SuppressWarnings("unused")
class PractitionerRepositoryInternalImpl extends SimpleR2dbcRepository<Practitioner, Long> implements PractitionerRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrganizationRowMapper organizationMapper;
    private final PractitionerRowMapper practitionerMapper;

    private static final Table entityTable = Table.aliased("practitioner", EntityManager.ENTITY_ALIAS);
    private static final Table organizationTable = Table.aliased("organization", "e_organization");

    public PractitionerRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrganizationRowMapper organizationMapper,
        PractitionerRowMapper practitionerMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Practitioner.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.organizationMapper = organizationMapper;
        this.practitionerMapper = practitionerMapper;
    }

    @Override
    public Flux<Practitioner> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Practitioner> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PractitionerSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrganizationSqlHelper.getColumns(organizationTable, "organization"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(organizationTable)
            .on(Column.create("organization_id", entityTable))
            .equals(Column.create("id", organizationTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Practitioner.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Practitioner> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Practitioner> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Practitioner process(Row row, RowMetadata metadata) {
        Practitioner entity = practitionerMapper.apply(row, "e");
        entity.setOrganization(organizationMapper.apply(row, "organization"));
        return entity;
    }

    @Override
    public <S extends Practitioner> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
