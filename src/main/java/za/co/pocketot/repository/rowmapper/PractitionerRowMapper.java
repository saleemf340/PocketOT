package za.co.pocketot.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import za.co.pocketot.domain.Practitioner;
import za.co.pocketot.domain.enumeration.IdDocument;
import za.co.pocketot.domain.enumeration.PractitionerType;

/**
 * Converter between {@link Row} to {@link Practitioner}, with proper type conversions.
 */
@Service
public class PractitionerRowMapper implements BiFunction<Row, String, Practitioner> {

    private final ColumnConverter converter;

    public PractitionerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Practitioner} stored in the database.
     */
    @Override
    public Practitioner apply(Row row, String prefix) {
        Practitioner entity = new Practitioner();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUuid(converter.fromRow(row, prefix + "_uuid", UUID.class));
        entity.setIdDocument(converter.fromRow(row, prefix + "_id_document", IdDocument.class));
        entity.setIdNumber(converter.fromRow(row, prefix + "_id_number", String.class));
        entity.setDateOfBirth(converter.fromRow(row, prefix + "_date_of_birth", LocalDate.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setRegistrationNumber(converter.fromRow(row, prefix + "_registration_number", String.class));
        entity.setVerified(converter.fromRow(row, prefix + "_verified", Boolean.class));
        entity.setPractitionerType(converter.fromRow(row, prefix + "_practitioner_type", PractitionerType.class));
        entity.setOrganizationId(converter.fromRow(row, prefix + "_organization_id", Long.class));
        return entity;
    }
}
