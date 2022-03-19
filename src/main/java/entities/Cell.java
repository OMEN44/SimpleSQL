package entities;

@SuppressWarnings("unused")
public interface Cell extends BasicCell {
    /**
     * @return Gets the column that this cell belongs to.
     */
    Column getColumn();

    /**
     * @return {@code true} if the cell is in a unique column and {@code false} otherwise.
     */
    boolean isUnique();

    /**
     * @return {@code true} if the cell is in a primary column and {@code false} otherwise.
     */
    boolean isPrimary();
}
