package func.values;

import func.basic.Ob;

public class Primitives extends Ob {
    private final char c;
    private final boolean bool;
    private final short s;
    private final float f;
    private final Float wf;
    private final int i;
    private final long l;
    private final byte b;
    private final double d;

    public Primitives(boolean bool, char c, byte b, short s, int i, long l, float f, Float wf, double d) {
        this.bool = bool;
        this.c = c;
        this.b = b;
        this.s = s;
        this.i = i;
        this.l = l;
        this.f = f;
        this.wf = wf;
        this.d = d;
    }

    public char getC() {
        return c;
    }

    public boolean isBool() {
        return bool;
    }

    public short getS() {
        return s;
    }

    public Primitives withS(short s) {
        return new Primitives(bool, c, b, s, i, l, f, wf, d);
    }

    public Float getWf() {
        return wf;
    }

    public Primitives withWF(Float wf) {
        return new Primitives(bool, c, b, s, i, l, f, wf, d);
    }

    public int getI() {
        return i;
    }

    public byte getB() {
        return b;
    }

    public float getF() {
        return f;
    }

    public long getL() {
        return l;
    }

    public Primitives withL(long l) {
        return new Primitives(bool, c, b, s, i, l, f, wf, d);
    }

    public double getD() {
        return d;
    }

}
