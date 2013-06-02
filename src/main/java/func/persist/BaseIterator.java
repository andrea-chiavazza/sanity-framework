package func.persist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static func.persist.DB.classToTableName;

abstract class BaseIterator<E> implements Iterator<E> {
    public static final long LISTS_INDEX_NONE = -1;
    private static final String ID = "id_";

    private final DB db;
    private final Class<E> cl;

    private long next;
    private ResultSet partial;
    private boolean hasAdvanced;
    private boolean hasMore;

    public BaseIterator(RowId rowId,
                        DB db,
                        int idIndex,
                        int typeIndex) throws SQLException {
        try {
            this.db = db;
            ResultSet res = db.getResultSet(rowId);
            long listsIndex = res.getLong(idIndex);
            if (listsIndex != LISTS_INDEX_NONE) {
                cl = (Class<E>) Class.forName(res.getString(typeIndex));
                initNextList(listsIndex);
                hasAdvanced = false;
            } else {
                cl = null;
                hasAdvanced = true;
                hasMore = false;
                next = LISTS_INDEX_NONE;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            if (!hasAdvanced) {
                hasMore = partial.next();
                hasAdvanced = true;
            }
            if (!hasMore) {
                if (next == LISTS_INDEX_NONE) {
                    return false;
                } else {
                    initNextList(next);
                    hasMore = partial.next();
                    hasAdvanced = true;
                }
            }
            return hasMore;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E next() {
        try {
            if (hasNext()) {
                hasAdvanced = false;
                return db.retrieve(partial, cl);
            } else {
                throw new NoSuchElementException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        // if the next method has not yet been called,
        // or the remove method has already been called after the last call to the next method
        if (false) {
            throw new IllegalStateException();
        } else {

        }

        throw new UnsupportedOperationException();
        //todo
    }

    private void initNextList(long id) throws SQLException {
        ListEntry entry = db.getLists().retrieveEntry(id);
        long first = entry.getFirst();
        int size = entry.getSize();
        next = entry.getNext();
        partial = db.getConnection().createStatement().executeQuery("SELECT * FROM " + classToTableName(cl) +
            " WHERE " + ID + " BETWEEN " + first + " AND " + (first + size - 1));
    }
}
