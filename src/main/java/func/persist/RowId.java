package func.persist;

class RowId<T> {
    public final Class<T> cl;
    public final long id;

    RowId(Class<T> cl, long id) {
        this.cl = cl;
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + cl + ", " + id + ")";
    }

    @Override
    public int hashCode() {
        return cl.hashCode() ^ new Long(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RowId)) {
            return false;
        }
        RowId that = (RowId) obj;
        return cl == that.cl && id == that.id;
    }
}
