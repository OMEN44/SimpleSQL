import connectors.Connector;
import entities.Param;
import entities.Table;
import entities.cell.Cell;
import entities.cell.CreateCell;
import entities.column.Column;
import logger.TableUnassignedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unused")
public class SimpleSQL {
    private final Connector CONNECTOR;

    public SimpleSQL(Connector connector) {
        this.CONNECTOR = connector;
    }

    /*
     * Cell methods:
     *
     *
     */

    /**
     * @param column Target column
     * @return Returns a list of all the cells in a row.
     */
    List<Cell> getCellFromColumn(Column column) {
        return null;
    }

    /**
     * @param targetColumn Parent column of the cell that you are getting.
     * @param idColumn     unique column of the table that the cell belongs to.
     * @param cellValue    This basic cell is used to find the correct row of the cell being gotten, it must be in the unique column.
     * @return Returns a single cell given the column and a row.
     * @apiNote This should be used to get a single cell if it's data is the target then use the getData method.
     */
    Cell getCell(Column targetColumn, Column idColumn, Object cellValue) throws TableUnassignedException {
        Table table = idColumn.getParentTable();
        Object data = null;

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = CONNECTOR.getSQLConnection();
            if (conn != null) {
                ps = conn.prepareStatement("SELECT `" + targetColumn.getName() + "` FROM " + table.getName() +
                        " WHERE " + idColumn.getName() + "=?");
                ps.setObject(1, cellValue);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    data = rs.getObject(targetColumn.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connector.disconnector(conn, ps);
        }

        return new CreateCell(
                targetColumn.getDatatype(),
                data,
                targetColumn,
                targetColumn.isUnique(),
                targetColumn.isPrimary()
        );
    }

    /**
     * @return A selection of cells that fit the specified parameters.
     */
    List<Cell> getCells(Param... param) {
        return null;
    }
}