package func.persist;

import java.sql.SQLException;
import java.util.Iterator;

class ValuesIterable<E> implements Iterable<E> {
    private final RowId rowId;
    private final DB db;

    public ValuesIterable(RowId rowId, DB db) {
        this.rowId = rowId;
        this.db = db;
    }

    @Override
    public Iterator<E> iterator() {
        try {
            return new ValuesIterator<>(rowId, db);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
