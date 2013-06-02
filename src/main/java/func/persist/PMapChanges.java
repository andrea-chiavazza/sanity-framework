package func.persist;

import java.util.Collection;
import java.util.Map;

class MapChange {
}

class MPlus<K,V> extends MapChange {
    public final K key;
    public final V value;

    MPlus(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

class MPlusAll<K,V> extends MapChange {
    public final Map<K,V> map;
    MPlusAll(Map<K,V> map) {
        this.map = map;
    }
}

class MMinus<K,V> extends MapChange {
    public final K key;

    MMinus(K key) {
        this.key = key;
    }
}

class MMinusAll<K,V> extends MapChange {
    public final Collection<K> keys;

    MMinusAll(Collection<K> keys) {
        this.keys = keys;
    }
}

