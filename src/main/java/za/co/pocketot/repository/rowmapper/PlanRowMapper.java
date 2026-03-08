package za.co.pocketot.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import za.co.pocketot.domain.Plan;

/**
 * Converter between {@link Row} to {@link Plan}, with proper type conversions.
 */
@Service
public class PlanRowMapper implements BiFunction<Row, String, Plan> {

    private final ColumnConverter converter;

    public PlanRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Plan} stored in the database.
     */
    @Override
    public Plan apply(Row row, String prefix) {
        Plan entity = new Plan();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUuid(converter.fromRow(row, prefix + "_uuid", UUID.class));
        entity.setExerciseRepitition(converter.fromRow(row, prefix + "_exercise_repitition", Integer.class));
        entity.setPlanRepitition(converter.fromRow(row, prefix + "_plan_repitition", Integer.class));
        entity.setEffectiveFrom(converter.fromRow(row, prefix + "_effective_from", LocalDate.class));
        entity.setEffectiveTo(converter.fromRow(row, prefix + "_effective_to", LocalDate.class));
        entity.setPatientId(converter.fromRow(row, prefix + "_patient_id", Long.class));
        entity.setPractitionerId(converter.fromRow(row, prefix + "_practitioner_id", Long.class));
        entity.setExerciseId(converter.fromRow(row, prefix + "_exercise_id", Long.class));
        return entity;
    }
}
