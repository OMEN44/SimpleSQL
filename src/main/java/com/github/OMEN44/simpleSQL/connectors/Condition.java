package com.github.OMEN44.simpleSQL.connectors;

public class Condition {

    public final Type TYPE;
    public String column;
    public Object value;
    public boolean bool;

    public Condition(Type type, String column, Object hasValue) {
        this.TYPE = type;
        this.column = column;
        this.value = hasValue;
    }

    public Condition(Type type, boolean bool) {
        this.TYPE = type;
        this.bool = bool;
    }

    public enum Type {
        PLAIN,
        AND,
        OR,
        AND_NOT,
        OR_NOT,
        LIKE,
        ORDER,
        DISTINCT,
        FOR_UPDATE;

        @Override
        public String toString() {
            return switch (this) {
                case PLAIN, FOR_UPDATE -> "";
                case OR -> "OR";
                case AND -> "AND";
                case LIKE -> "LIKE";
                case AND_NOT -> "AND NOT";
                case OR_NOT -> "OR NOT";
                case DISTINCT -> "DISTINCT";
                case ORDER -> "ORDER BY";
            };
        }
    }
}
