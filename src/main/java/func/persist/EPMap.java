package func.persist;

import org.pcollections.Empty;
import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

class EPMap<K,V> extends AbstractMap<K,V> implements PMap<K,V> {
    private final PMap<K,V> original;
    private final PMap<K,V> instance;
    private final PVector<MapChange> changes;

    public EPMap(PMap<K,V> instance) {
        this.original = instance;
        this.instance = instance;
        this.changes = Empty.vector();
    }

    public EPMap(PMap<K,V> instance, PVector<MapChange> changes, PMap<K,V> original) {
        this.original = original;
        this.instance = instance;
        this.changes = changes;
    }

    private EPMap<K,V> withChanges(PMap<K,V> instance, PVector<MapChange> changes) {
        return new EPMap<>(instance, changes, original);
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return instance.entrySet();
    }

    @Override
    public EPMap<K,V> plus(K key, V value) {
        return withChanges(instance.plus(key, value), changes.plus(new MPlus<>(key, value)));
    }

    @Override
    public EPMap<K,V> plusAll(Map<? extends K,? extends V> map) {
        return withChanges(instance.plusAll(map), changes.plus(new MPlusAll<>(map)));
    }

    @Override
    public EPMap<K,V> minus(Object key) {
        return withChanges(instance.minus(key), changes.plus(new MMinus<>(key)));
    }

    @Override
    public EPMap<K,V> minusAll(Collection<?> keys) {
        return withChanges(instance.minusAll(keys), changes.plus(new MMinusAll<>(keys)));
    }

    public PVector<MapChange> getChanges() {
        return changes;
    }

    public PMap<K,V> getOriginal() {
        return original;
    }

    public EPMap<K,V> withResetChanges() {
        return new EPMap<>(instance);
    }
}

