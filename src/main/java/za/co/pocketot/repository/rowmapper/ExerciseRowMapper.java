package za.co.pocketot.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import za.co.pocketot.domain.Exercise;

/**
 * Converter between {@link Row} to {@link Exercise}, with proper type conversions.
 */
@Service
public class ExerciseRowMapper implements BiFunction<Row, String, Exercise> {

    private final ColumnConverter converter;

    public ExerciseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Exercise} stored in the database.
     */
    @Override
    public Exercise apply(Row row, String prefix) {
        Exercise entity = new Exercise();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUuid(converter.fromRow(row, prefix + "_uuid", UUID.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setVideoLink(converter.fromRow(row, prefix + "_video_link", String.class));
        return entity;
    }
}
