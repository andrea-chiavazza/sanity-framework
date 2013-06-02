package func.persist;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

class KeysIterable<E> implements Iterable<E> {
    private final RowId<? extends Collection<E>> rowId;
    private final DB db;

    public KeysIterable(RowId<? extends Collection<E>> rowId, DB db) {
        this.rowId = rowId;
        this.db = db;
    }

    @Override
    public Iterator<E> iterator() {
        try {
            return new KeysIterator<>(rowId, db);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
