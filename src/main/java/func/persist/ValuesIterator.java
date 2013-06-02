package func.persist;

import java.sql.SQLException;

class ValuesIterator<E> extends BaseIterator<E> {
    private static final int V_LISTS_ID_INDEX = 2;
    private static final int V_TYPE_INDEX = 3;

    public ValuesIterator(RowId rowId,
                          DB db) throws SQLException {
        super(rowId, db, V_LISTS_ID_INDEX, V_TYPE_INDEX);
    }
}
