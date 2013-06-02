package func.basic;

import org.pcollections.Empty;
import org.testng.Assert;
import org.testng.annotations.Test;

import static func.values.Values.m1;
import static func.values.Values.m2;
import static org.testng.Assert.*;

public class TestOb {

    @Test
    public void testHashCode() throws Exception {
        assertFalse(new C(23, new B(234, "ab", 77)).hashCode() == new C(23, new B(234, "ab", 727)).hashCode());
        Assert.assertEquals(new C(23, new B(234, "ab", 77)).hashCode(), new C(23, new B(234, "ab", 77)).hashCode());

        Assert.assertEquals(new A(123, "a").hashCode(), new A(123, "a").hashCode());
        Assert.assertEquals(new A(123, null).hashCode(), new A(123, null).hashCode());

        assertTrue(new A(123, "a").hashCode() != new A(234, "a").hashCode());
        assertTrue(new A(123, "a").hashCode() != new A(123, "b").hashCode());
        assertTrue(new A(234, "a").hashCode() != new A(123, "b").hashCode());

        assertTrue(new B(234, "aa", 77).hashCode() == new B(234, "aa", 77).hashCode());
        assertFalse(new B(234, "aa", 77).hashCode() == new B(234, "aa", 78).hashCode());
        assertFalse(new B(234, "aa", 77).hashCode() == new B(234, "ab", 77).hashCode());

        assertNotEquals(m1.hashCode(), m2.hashCode());
        assertEquals(m1.hashCode(), m1.withI(1).withI(465).hashCode());
        assertNotEquals(m1.hashCode(), m1.withI(1).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new A(123, "a"), new A(123, "a"));
        assertEquals(new A(123, "a"), new A(123, "a"));
        assertFalse(new A(123, "a").equals(new A(123, null)));
        assertFalse(new A(123, null).equals(new A(123, "a")));

        assertFalse(new A(123, "a").equals(new A(234, "a")));
        assertFalse(new A(234, "a").equals(new A(123, "a")));
        assertFalse(new A(123, "a").equals(new A(123, "b")));
        assertFalse(new A(123, "b").equals(new A(123, "a")));
        assertFalse(new A(234, "a").equals(new A(123, "b")));
        assertFalse(new A(123, "b").equals(new A(234, "a")));

        assertEquals(new B(234, "ab", 77.1), new B(234, "ab", 77.1));
        assertEquals(new B(234, "ab", 77.1), new B(234, "ab", 77.1));
        assertFalse(new B(234, "ab", 77.1).equals(new B(236, "ab", 77.1)));
        assertFalse(new B(236, "ab", 77.1).equals(new B(234, "ab", 77.1)));
        assertFalse(new B(234, "ab", 77.1).equals(new B(236, "ab", 78)));
        assertFalse(new B(236, "ab", 78).equals(new B(234, "ab", 77.1)));

        assertFalse(new A(123, "b").equals(new B(234, "ab", 77.1)));
        assertFalse(new A(234, "ab").equals(new B(234, "ab", 77.1)));

        assertEquals(new C(23, new B(234, "ab", 77.1)), new C(23, new B(234, "ab", 77.1)));
        assertFalse(new C(23, new B(234, "ab", 77.1)).equals(new C(23, new B(234, "ab", 727))));

        assertNotEquals(m1, m2);
        assertEquals(m1, m1.withI(1).withI(465));

        assertEquals(
            Empty.set().plus(1).plus(2).plus(3),
            Empty.set().plus(3).plus(1).plus(2));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("A[i=123, s=a]", new A(123, "a").toString());
        Assert.assertEquals("A[i=234, s=a]", new A(234, "a").toString());
        Assert.assertEquals("B[i=234, s=ab, d=77.1]", new B(234, "ab", 77.1).toString());
        Assert.assertEquals("C[i=23, b=B[i=234, s=ab, d=77.1]]", new C(23, new B(234, "ab", 77.1)).toString());
    }

    @Test
    public void testMixed1ToString() {
        assertEquals(
            m1.toString(),
            "Mixed[" +
                "i=465, " +
                "s=132, " +
                "n=null, " +
                "p=Primitives[c=f, bool=true, s=4660, f=0.123, wf=0.623, i=305419898, l=81985529789148946, b=18, d=0.123], " +
                "vs=[vs1, how, are, you], " +
                "sp=[" +
                    "Primitives[c=ᄑ, bool=false, s=16965, f=0.321, wf=0.821, i=643052058, l=3156441774464124263, b=56, d=0.321], " +
                    "Primitives[c=f, bool=true, s=4660, f=0.123, wf=0.623, i=305419898, l=81985529789148946, b=18, d=0.123]], " +
                "c=Composite[" +
                    "primitives1=Primitives[c=f, bool=true, s=4660, f=0.123, wf=0.623, i=305419898, l=81985529789148946, b=18, d=0.123], " +
                    "nonPrimitives1=NonPrimitives[bi=31645855629199197655647991, str=hello, bd=54675670.1237234578634563574], " +
                    "primitives2=Primitives[c=ᄑ, bool=false, s=16965, f=0.321, wf=0.821, i=643052058, l=3156441774464124263, b=56, d=0.321], " +
                    "nonPrimitives2=NonPrimitives[bi=123934863458934523475345567, str=how are you, bd=356735670.4876345786876345321], i2=134, s2=one  four], " +
                "vwp=[" +
                    "WithPCollections[no=12, names=[vs1, how, are, you], numbers=[-213, 4, 11, 534]], " +
                    "WithPCollections[no=0, names=[], numbers=[]], WithPCollections[no=67, names=[vs2, two, three], numbers=[22, -34, 0, 233]]], " +
                "msv={aa=[1134, -7171, 8888], msv1=[34, 10101, -9999]}]");
    }

    @Test
    public void testMixed2ToString() {
        assertEquals(
            m2.toString(),
            "Mixed[" +
                "i=465, " +
                "s=water, " +
                "n=NonPrimitives[bi=123934863458934523475345567, str=how are you, bd=356735670.4876345786876345321], " +
                "p=Primitives[c=ᄑ, bool=false, s=16965, f=0.321, wf=0.821, i=643052058, l=3156441774464124263, b=56, d=0.321], " +
                "vs=[vs2, two, three], " +
                "sp=[" +
                    "Primitives[c=,, bool=true, s=8500, f=0.191, wf=0.591, i=357848698, l=76866203650223890, b=34, d=0.181], " +
                    "Primitives[c=ᠱ, bool=false, s=8755, f=0.111, wf=null, i=573674010, l=3156197678587790695, b=18, d=0.191]], " +
                "c=Composite[" +
                    "primitives1=Primitives[c=,, bool=true, s=8500, f=0.191, wf=0.591, i=357848698, l=76866203650223890, b=34, d=0.181], " +
                    "nonPrimitives1=NonPrimitives[bi=31645778069860080569140983, str=hi, bd=54675670.1237234111634563574], " +
                    "primitives2=Primitives[c=ᠱ, bool=false, s=8755, f=0.111, wf=null, i=573674010, l=3156197678587790695, b=18, d=0.191], " +
                    "nonPrimitives2=NonPrimitives[bi=123934863458934511175345567, str=one two, bd=356735670.4876341116876345321], i2=93674, s2=two three], " +
                "vwp=[" +
                    "WithPCollections[no=-4, names=[vs2, two, three], numbers=[-9011, -45, 33, 100]], " +
                    "WithPCollections[no=0, names=[vs3, 2nd, 3rd], numbers=[-213, 4, 11, 534]]], " +
                "msv={bb=[4, 222, 290], msv2=[3, 345, -90]}]");
    }

    class A extends Ob {
        private final int i;
        private final String s;

        A(int i, String s) {
            this.i = i;
            this.s = s;
        }
    }

    class B extends A {
        private final double d;

        B(int i, String s, double d) {
            super(i, s);
            this.d = d;
        }
    }

    class C extends Ob {
        private final int i;
        private final B b;

        C(int i, B b) {
            this.i = i;
            this.b = b;
        }
    }
}

