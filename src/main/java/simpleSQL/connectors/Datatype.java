package simpleSQL.connectors;

/**
 * @apiNote This is only for common data types more complex data types should be written in string form.
 */
@SuppressWarnings("unused")
public enum Datatype {
    /**
     * VARCHAR and CHAR require a size to be used. <br>
     * If no size is set the default is 255.
     */
    VARCHAR(255),
    CHAR(255),
    INT,
    FLOAT,
    DOUBLE,
    DATE,
    /**
     * {@code Datatype.OBJECT} is used as a placeholder for result sets and may result in errors if used to write to a
     * database
     */
    OBJECT;

    private Integer size;

    Datatype(int size) {this.size = size;}
    Datatype() {this.size = null;}

    public void setSize(int size) {this.size = size;}

    public Integer getSize() {return this.size;}

    public static String toString(Datatype datatype) {
        return switch (datatype) {
            case VARCHAR -> "VARCHAR(" + datatype.getSize() + ")";
            case CHAR -> "CHAR(" + datatype.getSize() + ")";
            case INT -> "INT";
            case FLOAT -> "FLOAT";
            case DOUBLE -> "DOUBLE";
            case DATE -> "DATE";
            case OBJECT -> null;
        };
    }
}
