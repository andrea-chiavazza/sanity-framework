package func.persist;

import func.values.Values;
import org.testng.Assert;
import org.testng.annotations.Test;

import static func.persist.XMLWrite.valueToXMLString;
import static org.testng.Assert.assertEquals;

public class TestXMLWrite {

    @Test
    public void testInteger() throws Exception {
        assertEquals(
            valueToXMLString(2),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<java.lang.Integer>2</java.lang.Integer>\n");
    }

    @Test
    public void testString() throws Exception {
        assertEquals(
            valueToXMLString("Hello"),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<java.lang.String>Hello</java.lang.String>\n");
    }

    @Test
    public void testNullValueWithoutSpecifyingTheClass() throws Exception {
        try {
            assertEquals(
                valueToXMLString(null),
                "");
            Assert.fail("It should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testNullValueWithClass() throws Exception {
        assertEquals(
            valueToXMLString(null, String.class),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<java.lang.String>null</java.lang.String>\n");
    }

    @Test
    public void testPrimitivesToXml() throws Exception {
        assertEquals(
            valueToXMLString(Values.p1),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<func.values.Primitives b=\"18\" bool=\"true\" c=\"f\" d=\"0.123\" f=\"0.123\" i=\"305419898\" l=\"81985529789148946\" s=\"4660\" wf=\"0.623\"/>\n");
    }

    @Test
    public void testNonPrimitivesToXml() throws Exception {
        assertEquals(
            valueToXMLString(Values.n1),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<func.values.NonPrimitives bd=\"54675670.1237234578634563574\" bi=\"31645855629199197655647991\" str=\"hello\"/>\n");
    }

    @Test
    public void testComposite1ToXml() throws Exception {
        assertEquals(
            valueToXMLString(Values.c1),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<func.values.Composite i2=\"134\" s2=\"one  four\">\n" +
            "  <func.values.Primitives b=\"18\" bool=\"true\" c=\"f\" d=\"0.123\" f=\"0.123\" i=\"305419898\" l=\"81985529789148946\" s=\"4660\" wf=\"0.623\"/>\n" +
            "  <func.values.Primitives b=\"56\" bool=\"false\" c=\"ᄑ\" d=\"0.321\" f=\"0.321\" i=\"643052058\" l=\"3156441774464124263\" s=\"16965\" wf=\"0.821\"/>\n" +
            "  <func.values.NonPrimitives bd=\"54675670.1237234578634563574\" bi=\"31645855629199197655647991\" str=\"hello\"/>\n" +
            "  <func.values.NonPrimitives bd=\"356735670.4876345786876345321\" bi=\"123934863458934523475345567\" str=\"how are you\"/>\n" +
            "</func.values.Composite>\n");
    }

    @Test
    public void testComposite2ToXml() throws Exception {
        assertEquals(
            valueToXMLString(Values.c2),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<func.values.Composite i2=\"93674\" s2=\"two three\">\n" +
            "  <func.values.Primitives b=\"34\" bool=\"true\" c=\",\" d=\"0.181\" f=\"0.191\" i=\"357848698\" l=\"76866203650223890\" s=\"8500\" wf=\"0.591\"/>\n" +
            "  <func.values.Primitives b=\"18\" bool=\"false\" c=\"ᠱ\" d=\"0.191\" f=\"0.111\" i=\"573674010\" l=\"3156197678587790695\" s=\"8755\" wf=\"null\"/>\n" +
            "  <func.values.NonPrimitives bd=\"54675670.1237234111634563574\" bi=\"31645778069860080569140983\" str=\"hi\"/>\n" +
            "  <func.values.NonPrimitives bd=\"356735670.4876341116876345321\" bi=\"123934863458934511175345567\" str=\"one two\"/>\n" +
            "</func.values.Composite>\n");
    }

    @Test
    public void testWithPVectorToXml() throws Exception {
        assertEquals(
            valueToXMLString(Values.wpc1),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><func.values.WithPCollections no=\"12\">\n" +
                "  <org.pcollections.PVector>\n" +
                "    <java.lang.String>vs1</java.lang.String>\n" +
                "    <java.lang.String>how</java.lang.String>\n" +
                "    <java.lang.String>are</java.lang.String>\n" +
                "    <java.lang.String>you</java.lang.String>\n" +
                "  </org.pcollections.PVector>\n" +
                "  <org.pcollections.PSet>\n" +
                "    <java.lang.Integer>-213</java.lang.Integer>\n" +
                "    <java.lang.Integer>4</java.lang.Integer>\n" +
                "    <java.lang.Integer>11</java.lang.Integer>\n" +
                "    <java.lang.Integer>534</java.lang.Integer>\n" +
                "  </org.pcollections.PSet>\n" +
                "</func.values.WithPCollections>\n");
    }

    @Test
    public void testMixed1ToXml() throws Exception {
        assertEquals(
            valueToXMLString(Values.m1),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><func.values.Mixed i=\"465\" s=\"132\">\n" +
                "  <org.pcollections.PVector>\n" +
                "    <java.lang.String>vs1</java.lang.String>\n" +
                "    <java.lang.String>how</java.lang.String>\n" +
                "    <java.lang.String>are</java.lang.String>\n" +
                "    <java.lang.String>you</java.lang.String>\n" +
                "  </org.pcollections.PVector>\n" +
                "  <org.pcollections.PSet>\n" +
                "    <func.values.Primitives b=\"56\" bool=\"false\" c=\"ᄑ\" d=\"0.321\" f=\"0.321\" i=\"643052058\" l=\"3156441774464124263\" s=\"16965\" wf=\"0.821\"/>\n" +
                "    <func.values.Primitives b=\"18\" bool=\"true\" c=\"f\" d=\"0.123\" f=\"0.123\" i=\"305419898\" l=\"81985529789148946\" s=\"4660\" wf=\"0.623\"/>\n" +
                "  </org.pcollections.PSet>\n" +
                "  <org.pcollections.PVector>\n" +
                "    <func.values.WithPCollections no=\"12\">\n" +
                "      <org.pcollections.PVector>\n" +
                "        <java.lang.String>vs1</java.lang.String>\n" +
                "        <java.lang.String>how</java.lang.String>\n" +
                "        <java.lang.String>are</java.lang.String>\n" +
                "        <java.lang.String>you</java.lang.String>\n" +
                "      </org.pcollections.PVector>\n" +
                "      <org.pcollections.PSet>\n" +
                "        <java.lang.Integer>-213</java.lang.Integer>\n" +
                "        <java.lang.Integer>4</java.lang.Integer>\n" +
                "        <java.lang.Integer>11</java.lang.Integer>\n" +
                "        <java.lang.Integer>534</java.lang.Integer>\n" +
                "      </org.pcollections.PSet>\n" +
                "    </func.values.WithPCollections>\n" +
                "    <func.values.WithPCollections no=\"0\">\n" +
                "      <org.pcollections.PVector/>\n" +
                "      <org.pcollections.PSet/>\n" +
                "    </func.values.WithPCollections>\n" +
                "    <func.values.WithPCollections no=\"67\">\n" +
                "      <org.pcollections.PVector>\n" +
                "        <java.lang.String>vs2</java.lang.String>\n" +
                "        <java.lang.String>two</java.lang.String>\n" +
                "        <java.lang.String>three</java.lang.String>\n" +
                "      </org.pcollections.PVector>\n" +
                "      <org.pcollections.PSet>\n" +
                "        <java.lang.Integer>22</java.lang.Integer>\n" +
                "        <java.lang.Integer>-34</java.lang.Integer>\n" +
                "        <java.lang.Integer>0</java.lang.Integer>\n" +
                "        <java.lang.Integer>233</java.lang.Integer>\n" +
                "      </org.pcollections.PSet>\n" +
                "    </func.values.WithPCollections>\n" +
                "  </org.pcollections.PVector>\n" +
                "  <org.pcollections.PMap>\n" +
                "    <java.lang.String>aa</java.lang.String>\n" +
                "    <org.pcollections.TreePVector>\n" +
                "      <java.lang.Long>1134</java.lang.Long>\n" +
                "      <java.lang.Long>-7171</java.lang.Long>\n" +
                "      <java.lang.Long>8888</java.lang.Long>\n" +
                "    </org.pcollections.TreePVector>\n" +
                "    <java.lang.String>msv1</java.lang.String>\n" +
                "    <org.pcollections.TreePVector>\n" +
                "      <java.lang.Long>34</java.lang.Long>\n" +
                "      <java.lang.Long>10101</java.lang.Long>\n" +
                "      <java.lang.Long>-9999</java.lang.Long>\n" +
                "    </org.pcollections.TreePVector>\n" +
                "  </org.pcollections.PMap>\n" +
                "  <func.values.NonPrimitives>null</func.values.NonPrimitives>\n" +
                "  <func.values.Primitives b=\"18\" bool=\"true\" c=\"f\" d=\"0.123\" f=\"0.123\" i=\"305419898\" l=\"81985529789148946\" s=\"4660\" wf=\"0.623\"/>\n" +
                "  <func.values.Composite i2=\"134\" s2=\"one  four\">\n" +
                "    <func.values.Primitives b=\"18\" bool=\"true\" c=\"f\" d=\"0.123\" f=\"0.123\" i=\"305419898\" l=\"81985529789148946\" s=\"4660\" wf=\"0.623\"/>\n" +
                "    <func.values.Primitives b=\"56\" bool=\"false\" c=\"ᄑ\" d=\"0.321\" f=\"0.321\" i=\"643052058\" l=\"3156441774464124263\" s=\"16965\" wf=\"0.821\"/>\n" +
                "    <func.values.NonPrimitives bd=\"54675670.1237234578634563574\" bi=\"31645855629199197655647991\" str=\"hello\"/>\n" +
                "    <func.values.NonPrimitives bd=\"356735670.4876345786876345321\" bi=\"123934863458934523475345567\" str=\"how are you\"/>\n" +
                "  </func.values.Composite>\n" +
                "</func.values.Mixed>\n");
    }

    @Test
    public void testMixed2ToXml() throws Exception {
        assertEquals(
            valueToXMLString(Values.m2),
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><func.values.Mixed i=\"465\" s=\"water\">\n" +
                "  <org.pcollections.PVector>\n" +
                "    <java.lang.String>vs2</java.lang.String>\n" +
                "    <java.lang.String>two</java.lang.String>\n" +
                "    <java.lang.String>three</java.lang.String>\n" +
                "  </org.pcollections.PVector>\n" +
                "  <org.pcollections.PSet>\n" +
                "    <func.values.Primitives b=\"34\" bool=\"true\" c=\",\" d=\"0.181\" f=\"0.191\" i=\"357848698\" l=\"76866203650223890\" s=\"8500\" wf=\"0.591\"/>\n" +
                "    <func.values.Primitives b=\"18\" bool=\"false\" c=\"ᠱ\" d=\"0.191\" f=\"0.111\" i=\"573674010\" l=\"3156197678587790695\" s=\"8755\" wf=\"null\"/>\n" +
                "  </org.pcollections.PSet>\n" +
                "  <org.pcollections.PVector>\n" +
                "    <func.values.WithPCollections no=\"-4\">\n" +
                "      <org.pcollections.PVector>\n" +
                "        <java.lang.String>vs2</java.lang.String>\n" +
                "        <java.lang.String>two</java.lang.String>\n" +
                "        <java.lang.String>three</java.lang.String>\n" +
                "      </org.pcollections.PVector>\n" +
                "      <org.pcollections.PSet>\n" +
                "        <java.lang.Integer>-9011</java.lang.Integer>\n" +
                "        <java.lang.Integer>-45</java.lang.Integer>\n" +
                "        <java.lang.Integer>33</java.lang.Integer>\n" +
                "        <java.lang.Integer>100</java.lang.Integer>\n" +
                "      </org.pcollections.PSet>\n" +
                "    </func.values.WithPCollections>\n" +
                "    <func.values.WithPCollections no=\"0\">\n" +
                "      <org.pcollections.PVector>\n" +
                "        <java.lang.String>vs3</java.lang.String>\n" +
                "        <java.lang.String>2nd</java.lang.String>\n" +
                "        <java.lang.String>3rd</java.lang.String>\n" +
                "      </org.pcollections.PVector>\n" +
                "      <org.pcollections.PSet>\n" +
                "        <java.lang.Integer>-213</java.lang.Integer>\n" +
                "        <java.lang.Integer>4</java.lang.Integer>\n" +
                "        <java.lang.Integer>11</java.lang.Integer>\n" +
                "        <java.lang.Integer>534</java.lang.Integer>\n" +
                "      </org.pcollections.PSet>\n" +
                "    </func.values.WithPCollections>\n" +
                "  </org.pcollections.PVector>\n" +
                "  <org.pcollections.PMap>\n" +
                "    <java.lang.String>bb</java.lang.String>\n" +
                "    <org.pcollections.TreePVector>\n" +
                "      <java.lang.Long>4</java.lang.Long>\n" +
                "      <java.lang.Long>222</java.lang.Long>\n" +
                "      <java.lang.Long>290</java.lang.Long>\n" +
                "    </org.pcollections.TreePVector>\n" +
                "    <java.lang.String>msv2</java.lang.String>\n" +
                "    <org.pcollections.TreePVector>\n" +
                "      <java.lang.Long>3</java.lang.Long>\n" +
                "      <java.lang.Long>345</java.lang.Long>\n" +
                "      <java.lang.Long>-90</java.lang.Long>\n" +
                "    </org.pcollections.TreePVector>\n" +
                "  </org.pcollections.PMap>\n" +
                "  <func.values.NonPrimitives bd=\"356735670.4876345786876345321\" bi=\"123934863458934523475345567\" str=\"how are you\"/>\n" +
                "  <func.values.Primitives b=\"56\" bool=\"false\" c=\"ᄑ\" d=\"0.321\" f=\"0.321\" i=\"643052058\" l=\"3156441774464124263\" s=\"16965\" wf=\"0.821\"/>\n" +
                "  <func.values.Composite i2=\"93674\" s2=\"two three\">\n" +
                "    <func.values.Primitives b=\"34\" bool=\"true\" c=\",\" d=\"0.181\" f=\"0.191\" i=\"357848698\" l=\"76866203650223890\" s=\"8500\" wf=\"0.591\"/>\n" +
                "    <func.values.Primitives b=\"18\" bool=\"false\" c=\"ᠱ\" d=\"0.191\" f=\"0.111\" i=\"573674010\" l=\"3156197678587790695\" s=\"8755\" wf=\"null\"/>\n" +
                "    <func.values.NonPrimitives bd=\"54675670.1237234111634563574\" bi=\"31645778069860080569140983\" str=\"hi\"/>\n" +
                "    <func.values.NonPrimitives bd=\"356735670.4876341116876345321\" bi=\"123934863458934511175345567\" str=\"one two\"/>\n" +
                "  </func.values.Composite>\n" +
                "</func.values.Mixed>\n");
    }

}
