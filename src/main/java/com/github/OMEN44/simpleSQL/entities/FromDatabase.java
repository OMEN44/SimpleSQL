package com.github.OMEN44.simpleSQL.entities;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface FromDatabase {
    @Nonnull
    default ResultTable select(Where... conditions) {
        return null;
    }

    default void update() {

    }

    default void delete(Connector connector, String tableName, Where... conditions) {
        StringBuilder sb = new StringBuilder("DELETE FROM " + tableName);
        Object[] args = new Object[conditions.length];
        for (int i = 0; i < conditions.length; i++) {
            args[i] = conditions[i].data;
            if (i != 1)
                sb.append(" AND");
            sb.append(" WHERE `").append(conditions[i].column).append("`='").append(conditions[i].data).append("'");
        }
        connector.executeUpdate(sb.toString(), args);
    }

    default void create() {

    }

    default void drop() throws TableUnassignedException {

    }

    default void alter() {

    }

    class Where {
        public String column;
        public Object data;

        public Where(String column, Object hasValue) {
            this.column = column;
            this.data = hasValue;
        }
    }
}
