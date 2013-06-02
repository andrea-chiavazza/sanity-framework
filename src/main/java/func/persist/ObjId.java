package func.persist;

public class ObjId<T> {
    final RowId<? extends T> rowId;
    T object; // MUTABLE

    ObjId(RowId<? extends T> rowId, T object) {
        this.rowId = rowId;
        this.object = object;
    }

    public RowId<? extends T> getRowId() {
        return rowId;
    }

    public T getObject() {
        return object;
    }
}
