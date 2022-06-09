package com.github.OMEN44.simpleSQL.entities;

import com.github.OMEN44.simpleSQL.logger.SimpleSQLException;

import javax.annotation.Nonnull;

/**
 * @apiNote Core for all the objects in this library. {@code Entity} provides basic structure for all the database
 * objects used.
 */
@SuppressWarnings("unused")
public interface Entity {

    /**
     * This enum is a list of identifiers for the different simpleSQL.entities.
     */
    enum InstanceType {
        TABLE,
        COLUMN,
        ROW,
        CELL
    }

    /**
     * @return This method is implemented int each entity and returns the hard coded entity type.
     */
    @Nonnull
    InstanceType getEntityType();

    /**
     * @param entity Entity being checked.
     * @return The entity type of the object being checked.
     * @throws SimpleSQLException This is thrown if the entity being checked is not from the vanilla library. <br>
     * {@link Entity#getEntityType()} should be implemented into your class instead.
     */
    static InstanceType getInstanceType(Entity entity) throws SimpleSQLException {
        if (entity instanceof BasicCell) {
            return InstanceType.CELL;
        } else {
            throw new SimpleSQLException("Entity input is not recognised.");
        }
    }
}
