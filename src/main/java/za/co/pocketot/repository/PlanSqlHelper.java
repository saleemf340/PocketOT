package za.co.pocketot.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PlanSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("uuid", table, columnPrefix + "_uuid"));
        columns.add(Column.aliased("exercise_repitition", table, columnPrefix + "_exercise_repitition"));
        columns.add(Column.aliased("plan_repitition", table, columnPrefix + "_plan_repitition"));
        columns.add(Column.aliased("effective_from", table, columnPrefix + "_effective_from"));
        columns.add(Column.aliased("effective_to", table, columnPrefix + "_effective_to"));

        columns.add(Column.aliased("patient_id", table, columnPrefix + "_patient_id"));
        columns.add(Column.aliased("practitioner_id", table, columnPrefix + "_practitioner_id"));
        columns.add(Column.aliased("exercise_id", table, columnPrefix + "_exercise_id"));
        return columns;
    }
}
