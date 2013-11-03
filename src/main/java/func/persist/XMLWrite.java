package func.persist;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.CharArrayWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static func.utility.General.invoke;

public class XMLWrite {
    private static final String NULL = String.valueOf((Object) null); // string used to represent a null value

    public static Node valueToNode(Object obj,
                                   Class<?> cl,
                                   Document document) {
        Node node;
        if (obj == null) {
            node = document.createElement(cl.getName());
            node.appendChild(document.createTextNode(NULL));
        } else if (obj instanceof Collection) {
            if (obj instanceof Set) {
                node = document.createElement("org.pcollections.PSet");
//            } else if (obj instanceof List) {
            } else {
                node = document.createElement("org.pcollections.PVector");
            }
            Collection coll = (Collection) obj;
            for (Object o : coll) {
                node.appendChild(
                    valueToNode(
                        o,
                        o == null ? Object.class : o.getClass(),
                        document));
            }
//                    NamedNodeMap attributes = node.getAttributes();
//                    if (isBasicType(o.getClass())) {
//                        Attr attNode = document.createAttribute(getFieldName(getter.getName()));
//                        attNode.setNodeValue(o.toString());
//                        attributes.setNamedItem(attNode);
//                    } else {
//                    }
        } else if (obj instanceof Map) {
            node = document.createElement("org.pcollections.PMap");
            Map<?,?> map = (Map<?,?>) obj;
            for (Map.Entry entry : map.entrySet()) {
                Object key = entry.getKey();
                node.appendChild(
                    valueToNode(
                        key,
                        key == null ? Object.class : key.getClass(),
                        document));
                Object value = entry.getValue();
                node.appendChild(
                    valueToNode(
                        value,
                        value == null ? Object.class : value.getClass(),
                        document));
            }

        } else if (Utility.isBasicType(cl)) {
            node = document.createElement(cl.getName());
            node.appendChild(document.createTextNode(obj.toString()));
        } else {
            node = document.createElement(cl.getName());
            NamedNodeMap attributes = node.getAttributes();
            for (Method getter : Refl.findGetters(cl)) {
                Object returnedValue = invoke(obj, getter);
                if (Utility.isBasicType(getter.getReturnType())) {
                    Attr attNode = document.createAttribute(getterNameToFieldName(getter.getName()));
                    attNode.setNodeValue(returnedValue == null ? NULL : returnedValue.toString());
                    attributes.setNamedItem(attNode);
                } else {
                    node.appendChild(
                        valueToNode(
                            returnedValue,
                            getter.getReturnType(),
                            document));
                }
            }
        }
        return node;
    }

    public static String getterNameToFieldName(String getterName) {
        String fieldName;
        if ("is".equals(getterName.substring(0, 2))) {
            fieldName = Character.toLowerCase(getterName.charAt(2)) + getterName.substring(3);
        } else if ("get".equals(getterName.substring(0, 3))) {
            fieldName = Character.toLowerCase(getterName.charAt(3)) + getterName.substring(4);
        } else {
            fieldName = getterName;
        }
        return fieldName;
    }

    private static void assertObjectNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Class must be specified for null objects.");
        }
    }

    public static Document valueToDocument(Object obj,
                                           Class<?> cl) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.appendChild(valueToNode(obj, cl, document));
            return document;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document valueToDocument(Object obj) {
        assertObjectNotNull(obj);
        return valueToDocument(obj, obj.getClass());
    }

    public static void valueToXMLWriter(Object obj,
                                        Class<?> cl,
                                        Writer writer,
                                        Transformer transformer) {
        try {
            transformer
                .transform(
                    new DOMSource(
                        valueToNode(
                            obj,
                            cl,
                            DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())),
                    new StreamResult(writer));
        } catch (TransformerException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void valueToXMLWriter(Object obj,
                                        Class<?> cl,
                                        Writer writer) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            valueToXMLWriter(obj, cl, writer, transformer);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void valueToXMLWriter(Object obj,
                                        Writer writer) {
        assertObjectNotNull(obj);
        valueToXMLWriter(obj, obj.getClass(), writer);
    }

    public static String valueToXMLString(Object obj,
                                          Class<?> cl,
                                          Transformer transformer) {
        CharArrayWriter writer = new CharArrayWriter();
        valueToXMLWriter(obj, cl, writer, transformer);
        return writer.toString();
    }

    public static String valueToXMLString(Object obj,
                                          Class<?> cl) {
        CharArrayWriter writer = new CharArrayWriter();
        valueToXMLWriter(obj, cl, writer);
        return writer.toString();
    }

    public static String valueToXMLString(Object obj) {
        assertObjectNotNull(obj);
        return valueToXMLString(obj, obj.getClass());
    }

}
