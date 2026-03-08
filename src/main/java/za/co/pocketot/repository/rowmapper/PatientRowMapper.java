package za.co.pocketot.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import za.co.pocketot.domain.Patient;
import za.co.pocketot.domain.enumeration.IdDocument;

/**
 * Converter between {@link Row} to {@link Patient}, with proper type conversions.
 */
@Service
public class PatientRowMapper implements BiFunction<Row, String, Patient> {

    private final ColumnConverter converter;

    public PatientRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Patient} stored in the database.
     */
    @Override
    public Patient apply(Row row, String prefix) {
        Patient entity = new Patient();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUuid(converter.fromRow(row, prefix + "_uuid", UUID.class));
        entity.setIdDocument(converter.fromRow(row, prefix + "_id_document", IdDocument.class));
        entity.setIdNumber(converter.fromRow(row, prefix + "_id_number", String.class));
        entity.setDateOfBirth(converter.fromRow(row, prefix + "_date_of_birth", LocalDate.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        return entity;
    }
}
