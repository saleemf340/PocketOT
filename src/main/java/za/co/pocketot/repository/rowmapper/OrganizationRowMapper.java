package za.co.pocketot.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import za.co.pocketot.domain.Organization;

/**
 * Converter between {@link Row} to {@link Organization}, with proper type conversions.
 */
@Service
public class OrganizationRowMapper implements BiFunction<Row, String, Organization> {

    private final ColumnConverter converter;

    public OrganizationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Organization} stored in the database.
     */
    @Override
    public Organization apply(Row row, String prefix) {
        Organization entity = new Organization();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUuid(converter.fromRow(row, prefix + "_uuid", UUID.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setRegistrationNumber(converter.fromRow(row, prefix + "_registration_number", String.class));
        entity.setVerified(converter.fromRow(row, prefix + "_verified", Boolean.class));
        return entity;
    }
}
