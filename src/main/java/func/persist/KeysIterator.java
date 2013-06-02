package func.persist;

import java.sql.SQLException;

class KeysIterator<E> extends BaseIterator<E> {
    private static final int K_LISTS_ID_INDEX = 4;
    private static final int K_TYPE_INDEX = 5;

    public KeysIterator(RowId rowId,
                        DB db) throws SQLException {
        super(rowId, db, K_LISTS_ID_INDEX, K_TYPE_INDEX);
    }
}
