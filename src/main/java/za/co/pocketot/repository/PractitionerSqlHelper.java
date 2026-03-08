package za.co.pocketot.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PractitionerSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("uuid", table, columnPrefix + "_uuid"));
        columns.add(Column.aliased("id_document", table, columnPrefix + "_id_document"));
        columns.add(Column.aliased("id_number", table, columnPrefix + "_id_number"));
        columns.add(Column.aliased("date_of_birth", table, columnPrefix + "_date_of_birth"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("registration_number", table, columnPrefix + "_registration_number"));
        columns.add(Column.aliased("verified", table, columnPrefix + "_verified"));
        columns.add(Column.aliased("practitioner_type", table, columnPrefix + "_practitioner_type"));

        columns.add(Column.aliased("organization_id", table, columnPrefix + "_organization_id"));
        return columns;
    }
}
