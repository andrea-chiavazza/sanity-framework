package func.persist;

import org.pcollections.PCollection;
import org.pcollections.PVector;

/**
 * User: andrea
 * Date: 15/08/12
 * Time: 18:44
 */
interface EPCollection<E> {
    PVector<CollChange> getChanges();
    PCollection<E> getOriginal();
    EPCollection<E> withResetChanges();
}
