package func.values;

import func.basic.Ob;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NonPrimitives extends Ob {
    private final BigInteger bi;
    private final String str;
    private final BigDecimal bd;

    public NonPrimitives(String str, BigInteger bi, BigDecimal bd) {
        this.str = str;
        this.bi = bi;
        this.bd = bd.stripTrailingZeros();
    }

    public String getStr() {
        return str;
    }

    public BigDecimal getBd() {
        return bd;
    }

    public BigInteger getBi() {
        return bi;
    }

    public NonPrimitives withStr(String str) {
        return new NonPrimitives(str, bi, bd);
    }

    public NonPrimitives withBd(BigDecimal bd) {
        return new NonPrimitives(str, bi, bd);
    }

    public NonPrimitives withBi(BigInteger bi) {
        return new NonPrimitives(str, bi, bd);
    }

}
