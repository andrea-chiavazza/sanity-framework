package func.values;

import func.basic.Ob;

public class Composite extends Ob {
    private final Primitives primitives1;
    private final NonPrimitives nonPrimitives1;
    private final Primitives primitives2;
    private final NonPrimitives nonPrimitives2;
    private final int i2;
    private final String s2;

    public Composite(Primitives primitives1,
                     Primitives primitives2,
                     NonPrimitives nonPrimitives1,
                     NonPrimitives nonPrimitives2,
                     int i2, String s2) {
        this.primitives1 = primitives1;
        this.primitives2 = primitives2;
        this.nonPrimitives1 = nonPrimitives1;
        this.nonPrimitives2 = nonPrimitives2;
        this.i2 = i2;
        this.s2 = s2;
    }

    public Primitives getPrimitives1() {
        return primitives1;
    }

    public NonPrimitives getNonPrimitives1() {
        return nonPrimitives1;
    }

    public NonPrimitives getNonPrimitives2() {
        return nonPrimitives2;
    }

    public Primitives getPrimitives2() {
        return primitives2;
    }

    public String getS2() {
        return s2;
    }

    public int getI2() {
        return i2;
    }

}
