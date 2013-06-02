package func.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static func.persist.DB.classToTableName;
import static func.persist.Refl.hasReferences;

class LinkedLists {
    private static final String LISTS_TABLE = "\"lists_\"";
    private static final String UNQUOTED_LISTS_TABLE = "lists_";

    private static final String ID = "id_";
    private static final int ID_INDEX = 1;
    private static final String LISTS_FIRST = "first_";
    private static final int LISTS_FIRST_INDEX = 2;

    private static final String LISTS_SIZE = "size_";
    private static final int LISTS_SIZE_INDEX = 3;

    private static final String LISTS_NEXT = "next_";
    private static final int LISTS_NEXT_INDEX = 4;

    private final DB db;
    private final Connection conn;
    private long nextId;

    LinkedLists(DB db) throws SQLException {
        this.db = db;
        this.conn = db.getConnection();
        if (!db.tableExists(UNQUOTED_LISTS_TABLE)) {
            String idType = db.classToSQLType(Long.class);
            conn.createStatement().executeUpdate(
                "CREATE TABLE " + LISTS_TABLE + "(" +
                    ID + " " + idType + ", " +
                    LISTS_FIRST + " " + idType + ", " +
                    LISTS_SIZE + " " + db.classToSQLType(Integer.class) + ", " +
                    LISTS_NEXT + " " + idType + ", " +
                    "PRIMARY KEY(" + ID + "))");
        }
    }

    /** Returns the id of the newly created row */
    public long addList(RowId firstRowId, int size, long next) throws SQLException {
        PreparedStatement lps = conn.prepareStatement("INSERT INTO " + LISTS_TABLE + " VALUES(?, ?, ?, ?)");
        lps.setLong(ID_INDEX, nextId);
        lps.setLong(LISTS_FIRST_INDEX, firstRowId.id);
        lps.setLong(LISTS_SIZE_INDEX, size);
        lps.setLong(LISTS_NEXT_INDEX, next);
        lps.executeUpdate();
        return nextId++;
    }

    public void updateListFirst(long id, long first) throws SQLException {
        PreparedStatement lps = conn.prepareStatement("UPDATE " + LISTS_TABLE + " SET " +
            LISTS_FIRST + "=? WHERE " + ID + "=" + id);
        lps.setLong(1, first);
        lps.executeUpdate();
    }

    public void updateListSize(long id, int size) throws SQLException {
        PreparedStatement lps = conn.prepareStatement("UPDATE " + LISTS_TABLE + " SET " +
            LISTS_SIZE + "=? WHERE " + ID + "=" + id);
        lps.setInt(1, size);
        lps.executeUpdate();
    }

    public void updateListNext(long id, long next) throws SQLException {
        PreparedStatement lps = conn.prepareStatement("UPDATE " + LISTS_TABLE + " SET " +
            LISTS_NEXT + "=? WHERE " + ID + "=" + id);
        lps.setLong(1, next);
        lps.executeUpdate();
    }

    public long removeList(long index, Class<?> cl) throws SQLException {
        ResultSet listsRes = getResultSet(LISTS_TABLE, index);
        long first = listsRes.getLong(LISTS_FIRST_INDEX);
        int size = listsRes.getInt(LISTS_SIZE_INDEX);

        if (hasReferences(cl)) {
            for (int i = 0; i < size; i++) {
                db.remove(new RowId<>(cl, first + i));
            }
        } else {
            conn.createStatement().executeUpdate(
                "DELETE FROM " + classToTableName(cl) +
                    " WHERE " + ID + " BETWEEN " + first + " AND " + (first + size - 1));
        }

        conn.createStatement().executeUpdate(
            "DELETE FROM " + LISTS_TABLE + " WHERE " + ID + "=" + index);
        return listsRes.getLong(LISTS_NEXT);
    }

    public ListEntry retrieveEntry(long id) throws SQLException {
        ResultSet listsRes = getResultSet(LISTS_TABLE, id);
        long first = listsRes.getLong(LISTS_FIRST_INDEX);
        int size = listsRes.getInt(LISTS_SIZE_INDEX);
        long listsIndex = listsRes.getLong(LISTS_NEXT_INDEX);
        return new ListEntry(first, size, listsIndex);
    }

    private ResultSet getResultSet(String tableName,
                           long id) throws SQLException {
        ResultSet res = conn.createStatement().executeQuery(
            "SELECT * from " + tableName + " WHERE " + ID + "=" + id);
        if (!res.next()) {
            throw new RuntimeException("Row " + id + " of table '" + tableName + "' can not be retrieved");
        }
        return res;
    }
}

