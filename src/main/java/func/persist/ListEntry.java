package func.persist;

/**
 * User: andrea
 * Date: 18/08/12
 * Time: 17:47
 */
class ListEntry {
    private final long first;
    private final int size;
    private final long next;

    public ListEntry(long first, int size, long next) {
        this.first = first;
        this.size = size;
        this.next = next;
    }

    public long getFirst() {
        return first;
    }

    public int getSize() {
        return size;
    }

    public long getNext() {
        return next;
    }
}
