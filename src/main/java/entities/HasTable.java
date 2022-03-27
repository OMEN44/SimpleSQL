package entities;

import connectors.Connector;
import logger.TableUnassignedException;

/**
 * @apiNote This interface is to group all the entities that build up tables. It also defines the getter and setter for
 * tables.
 */
@SuppressWarnings("unused")
public interface HasTable extends Entity {

    /**
     * @return Gets a {@link Table} object that represents the table this entity is in.
     * @throws TableUnassignedException If the entity has been created through methods and not gotten from a database
     * the table that it belongs to might be unassigned.
     */
    Table getParentTable() throws TableUnassignedException;

    /**
     * @param table {@link Table} that is being set.
     * @return The entity that the table is being set for.
     */
    HasTable setParentTable(Table table);

    void write(Connector conn) throws TableUnassignedException;
}
