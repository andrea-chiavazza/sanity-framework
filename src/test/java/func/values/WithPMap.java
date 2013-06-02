package func.values;

import func.basic.Ob;
import org.pcollections.PMap;

public class WithPMap extends Ob {
    private final int no;
    private final PMap<String,Integer> m1;
    private final PMap<Long,String> m2;

    public WithPMap(int no, PMap<String,Integer> m1, PMap<Long,String> m2) {
        this.no = no;
        this.m1 = m1;
        this.m2 = m2;
    }

    public int getNo() {
        return no;
    }

    public PMap<String,Integer> getM1() {
        return m1;
    }

    public PMap<Long,String> getM2() {
        return m2;
    }

}
