package func.values;

import org.pcollections.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * User: andrea
 * Date: 09/10/12
 * Time: 22:19
 */
public class Values {
    public static final Primitives p1 =
        new Primitives(true,  'f', (byte)0x12, (short)0x1234, 0x1234567a, 0x01234567abcdef12L, 0.123f, 0.623f, 0.123);
    public static final Primitives p2 =
        new Primitives(false, '\u1111', (byte)0x38, (short)0x4245, 0x2654321a, 0x2bcdef1201234567L, 0.321f, 0.821f, 0.321);
    public static final Primitives p3 =
        new Primitives(true,  ',', (byte)0x22, (short)0x2134, 0x1554567a, 0x01111567abcdef12L, 0.191f, 0.591f, 0.181);
    public static final Primitives p4 =
        new Primitives(false, '\u1831', (byte)0x12, (short)0x2233, 0x2231921a, 0x2bcd111101234567L, 0.111f, null, 0.191);

    public static final NonPrimitives n1 =
        new NonPrimitives(
            "hello",
            new BigInteger("1a2d45456b33aaa455c6f7", 16),
            new BigDecimal("54675670.1237234578634563574"));
    public static final NonPrimitives n2 =
        new NonPrimitives(
            "how are you",
            new BigInteger("123934863458934523475345567"),
            new BigDecimal("356735670.4876345786876345321"));
    public static final NonPrimitives n3 =
        new NonPrimitives(
            "hi",
            new BigInteger("1a2d41111113aaa455c6f7", 16),
            new BigDecimal("54675670.1237234111634563574"));
    public static final NonPrimitives n4 =
        new NonPrimitives(
            "one two",
            new BigInteger("123934863458934511175345567"),
            new BigDecimal("356735670.4876341116876345321"));

    public static final Composite c1 =
        new Composite(p1, p2, n1, n2, 134, "one  four");
    public static final Composite c2 =
        new Composite(p3, p4, n3, n4, 93674, "two three");
    public static final Composite c3 =
        new Composite(p2, p1, n1, n2, 134, "1 four");
    public static final Composite c4 =
        new Composite(p3, p2, n2, n4, 974, "2 three");

    public static final PVector<String> vs1 = TreePVector.from(Arrays.asList("vs1", "how", "are", "you"));
    public static final PVector<String> vs2 = TreePVector.from(Arrays.asList("vs2", "two", "three"));
    public static final PVector<String> vs3 = TreePVector.from(Arrays.asList("vs3", "2nd", "3rd"));
    public static final PVector<String> vs4 = TreePVector.from(Arrays.asList("vs4", "22", "333"));

    public static final PVector<Composite> vc1 = TreePVector.from(Arrays.asList(c1, c2, c3, c4));
    public static final PVector<Composite> vc2 = TreePVector.from(Arrays.asList(c3, c4, c3));
    public static final PVector<Composite> vc3 = TreePVector.from(Arrays.asList(c3, c4, c1));

    public static final PSet<Integer> si1 = HashTreePSet.from(Arrays.asList(11, 534, -213, 4));
    public static final POrderedSet<Integer> si2 = OrderedPSet.from(Arrays.asList(22, -34, 0, 233));
    public static final PSet<Integer> si3 = HashTreePSet.from(Arrays.asList(33, -9011, 100, -45));

    public static final WithPCollections wpc1 = new WithPCollections(12, vs1, si1);
    public static final WithPCollections wpc2 = new WithPCollections(67, vs2, si2);
    public static final WithPCollections wpc3 = new WithPCollections(-4, vs2, si3);
    public static final WithPCollections wpc4 = new WithPCollections(0, vs3, si1);
    public static final WithPCollections wpe = new WithPCollections(0, Empty.<String>vector(), Empty.<Integer>set());

    public static final PMap<String,Integer> pm1 = Empty.<String,Integer>map().plus("pm1", 321).plus("aaa", -88).plus("bb", 4);
    public static final PMap<Long,String> pm2 = Empty.<Long,String>map().plus(33L, "pm2").plus(-999L, "aaa");
    public static final PMap<Long,String> pm4 = Empty.<Long,String>map().plus(222L, "pm4").plus(-1111L, "eee");
    public static final PMap<String,Integer> pme = Empty.map();

    public static final WithPMap wpm1 = new WithPMap(12, pm1, pm2);
    public static final WithPMap wpm2 = new WithPMap(67, pme, pm4);

    public static final PSet<Primitives> sp1 = HashTreePSet.from(Arrays.asList(p1, p2));
    public static final POrderedSet<Primitives> sp2 = OrderedPSet.from(Arrays.asList(p3, p4));
    public static final PSet<Primitives> sp3 = HashTreePSet.from(Arrays.asList(p3, p1));
    public static final PVector<WithPCollections> vwp1 = TreePVector.from(Arrays.asList(wpc1, wpe, wpc2));
    public static final PVector<WithPCollections> vwp2 = TreePVector.from(Arrays.asList(wpc3, wpc4));

    public static final PVector<Long> vi1 = TreePVector.from(Arrays.asList(34L, 10101L, -9999L));
    public static final PVector<Long> vi2 = TreePVector.from(Arrays.asList(1134L, -7171L, 8888L));
    public static final PVector<Long> vi3 = TreePVector.from(Arrays.asList(3L, 345L, -90L));
    public static final PVector<Long> vi4 = TreePVector.from(Arrays.asList(4L, 222L, 290L));

    public static final PMap<Composite,PVector<Long>> msc1 =
        Empty.<Composite,PVector<Long>>map().plus(c1, TreePVector.from(vi1)).plus(c2, TreePVector.from(vi2));

    public static final PMap<String,PVector<Long>> msv1 =
        Empty.<String,PVector<Long>>map().plus("msv1", TreePVector.from(vi1)).plus("aa", TreePVector.from(vi2));
    public static final PMap<String,PVector<Long>> msv2 =
        Empty.<String,PVector<Long>>map().plus("msv2", TreePVector.from(vi3)).plus("bb", TreePVector.from(vi4));
    public static final PMap<String,PVector<Long>> msv3 =
        Empty.<String,PVector<Long>>map().plus("msv3", TreePVector.from(vi2)).plus("cc", TreePVector.from(vi3));

    public static final Mixed m1 = new Mixed(465, "132", vs1, sp1, vwp1, msv1, null, p1, c1);
    public static final Mixed m2 = new Mixed(465, "water", vs2, sp2, vwp2, msv2, n2, p2, c2);
}
