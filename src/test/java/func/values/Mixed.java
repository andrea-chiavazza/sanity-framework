package func.values;

import func.basic.Ob;
import org.pcollections.PMap;
import org.pcollections.PSet;
import org.pcollections.PVector;

public class Mixed extends Ob {
    private final int i;
    private final String s;
    private final NonPrimitives n;
    private final Primitives p;
    private final PVector<String> vs;
    private final PSet<Primitives> sp;
    private final Composite c;
    private final PVector<WithPCollections> vwp;
    private final PMap<String,PVector<Long>> msv;

    public Mixed(int i,
                 String s,
                 PVector<String> vs,
                 PSet<Primitives> sp,
                 PVector<WithPCollections> vwp,
                 PMap<String,PVector<Long>> msv,
                 NonPrimitives n,
                 Primitives p,
                 Composite c) {
        this.i = i;
        this.s = s;
        this.vs = vs;
        this.sp = sp;
        this.vwp = vwp;
        this.msv = msv;
        this.n = n;
        this.p = p;
        this.c = c;
    }

    public PMap<String,PVector<Long>> getMsv() {
        return msv;
    }

    public Composite getC() {
        return c;
    }

    public PVector<WithPCollections> getVwp() {
        return vwp;
    }

    public int getI() {
        return i;
    }

    public Mixed withI(int i) {
        return new Mixed(i, s, vs, sp, vwp, msv, n, p, c);
    }

    public String getS() {
        return s;
    }

    public PVector<String> getVs() {
        return vs;
    }

    public Mixed withVs(PVector<String> vs) {
        return new Mixed(i, s, vs, sp, vwp, msv, n, p, c);
    }

    public PSet<Primitives> getSp() {
        return sp;
    }

    public NonPrimitives getN() {
        return n;
    }

    public Primitives getP() {
        return p;
    }

}
