package func.persist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class IdCounter {
    private final Map<String,Long> tableNameToNextId = new HashMap<>();
    private final DB db;

    public IdCounter(DB db) {
        this.db = db;
    }

    /** The table must exist */
    public long getNextAndIncrementId(String tableName) throws SQLException {
        if (tableNameToNextId.containsKey(tableName)) {
            long id = tableNameToNextId.get(tableName);
            tableNameToNextId.put(tableName, id + 1);
            return id;
        } else {
            ResultSet res = db.getConnection().createStatement().executeQuery("SELECT MAX(" + DB.ID + ") FROM " + tableName);
            if (res.next()) {
                long id = res.getLong(1);
                tableNameToNextId.put(tableName, id + 1);
                return id;
            } else {
                tableNameToNextId.put(tableName, 1L);
                return 0;
            }
        }
    }

    public long getNextId(String tableName) {
        return tableNameToNextId.get(tableName);
    }

    public void remove(String tableName) {
        tableNameToNextId.remove(tableName);
    }
    public void reset(String tableName) {
        tableNameToNextId.put(tableName, 0L);
    }
}
