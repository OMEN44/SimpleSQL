package com.github.OMEN44.simpleSQL.entities;

import com.github.OMEN44.simpleSQL.connectors.Condition;
import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.logger.IllegalConditionException;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface FromDatabase {
    @Nonnull
    default ResultTable select(Condition... conditions) {
        return new ResultTable("");
    }

    default void update(Connector connector, String tableName, Condition... conditions)
            throws IllegalConditionException {
        StringBuilder sb = new StringBuilder("UPDATE " + tableName + " SET ");
        List<Condition> forUpdate = new ArrayList<>();
        for (Condition condition : conditions) {
            if (condition.TYPE == Condition.Type.ORDER || condition.TYPE == Condition.Type.DISTINCT)
                throw new IllegalConditionException(
                        "Condition of type '" + condition.TYPE + "' cannot be applied to delete."
                );
            else if (condition.TYPE == Condition.Type.FOR_UPDATE || condition.TYPE == Condition.Type.PLAIN) {
                if (condition.column == null)
                    throw new IllegalConditionException("Column was not assigned to this condition");
                forUpdate.add(condition);
            }
        }
        Object[] args = new Object[conditions.length - forUpdate.size()];
        connector.executeUpdate(sb.toString(), args);
    }

    default void delete(Connector connector, String tableName, Condition... conditions)
            throws IllegalConditionException {
        StringBuilder sb = new StringBuilder("DELETE FROM " + tableName);
        Object[] args = new Object[conditions.length];
        if (conditions.length != 0) {
            sb.append(" WHERE ");
            for (int i = 0; i < conditions.length; i++) {
                Condition.Type type = conditions[i].TYPE;
                if (type == Condition.Type.ORDER || type == Condition.Type.DISTINCT ||
                        type == Condition.Type.FOR_UPDATE)
                    throw new IllegalConditionException(
                            "Condition of type '" + conditions[i].TYPE + "' cannot be applied to delete."
                    );
                args[i] = conditions[i].value;
                if (i == 0 && (type == Condition.Type.AND_NOT || type == Condition.Type.OR_NOT))
                    sb.append("NOT ");
                else sb.append(type.toString()).append(" ");
                sb.append(conditions[i].column).append("=? ");
            }
        }
        connector.executeUpdate(sb.toString(), args);
    }

    default void create(Connector connector, String tableName, String existingTable, String[] columnNames, Condition... conditions) {
        /*
        CREATE TABLE new_table_name AS
        SELECT column1, column2,...
        FROM existing_table_name
        WHERE ....;
        */
    }

    default void drop() throws TableUnassignedException {

    }

    default void alter() {
        /*
        The ALTER TABLE statement is used to add, delete, or modify columns in an existing table.
        The ALTER TABLE...ALTER COLUMN statement is also used to add and drop various constraints on an existing table.
        */
    }
}
