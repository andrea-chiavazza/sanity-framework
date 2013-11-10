package func.persist;

import org.pcollections.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static func.persist.Refl.findGetters;
import static func.persist.XMLWrite.getterNameToFieldName;

public class XMLRead {
    private static final String NULL = String.valueOf((Object) null); // string used to represent a null value

    public static Object parsePrimitiveValue(String text,
                                             Class<?> cl) throws SAXException {
        if (NULL.equals(text)) {
            throw new RuntimeException("A primitive can't be null");
        }
        return parseBasicValue(text, Utility.primitiveTypeToWrapper(cl));
    }

    public static Object parseBasicValue(String text,
                                         Class<?> cl) throws SAXException {
        if (String.class != cl && NULL.equals(text)) {
            return null;
        }

        if (cl == Boolean.class) {
            return new Boolean(text);
        } else if (cl == Character.class) {
            if (text.length() == 1) {
                return new Character(text.charAt(0));
            } else {
                throw new SAXException("A character must be defined by a string of length 1");
            }
        } else if (cl == Byte.class) {
            return new Byte(text);
        } else if (cl == Short.class) {
            return new Short(text);
        } else if (cl == Integer.class) {
            return new Integer(text);
        } else if (cl == Long.class) {
            return new Long(text);
        } else if (cl == Float.class) {
            return new Float(text);
        } else if (cl == Double.class) {
            return new Double(text);
        } else if (cl == String.class) {
            return text;
        } else if (cl == BigDecimal.class) {
            return new BigDecimal(text);
        } else if (cl == BigInteger.class) {
            return new BigInteger(text);
        } else {
            throw new IllegalArgumentException("Not a basic type: " + cl);
        }
    }

    public static Object nodeToValue(Node node) throws ClassNotFoundException, SAXException {
        Class<?> cl = Class.forName(node.getNodeName());
        if (cl.isPrimitive()) {
            return parseBasicValue(node.getTextContent(), cl);
        } else if (Utility.isBasicType(cl)) {
            String textContent = node.getTextContent();
            //todo: String can't be null
            return parseBasicValue(textContent, cl);
        } else if (Collection.class.isAssignableFrom(cl)) {
            PCollection<Object> pColl;
            if (PVector.class.isAssignableFrom(cl)) {
                pColl = Empty.vector();
            } else if (POrderedSet.class.isAssignableFrom(cl)) {
                pColl = Empty.orderedSet();
            } else if (PSet.class.isAssignableFrom(cl)) {
                pColl = Empty.set();
            } else {
                throw new RuntimeException("unimplemented collection");
            }
            NodeList childNodes = node.getChildNodes();
            int childNodesLength = childNodes.getLength();
            for (int i = 0; i < childNodesLength; i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    pColl = pColl.plus(nodeToValue(child));
                }
            }
            return pColl;
        } else if (Map.class.isAssignableFrom(cl)) {
            PMap<Object,Object> pMap = Empty.map();
            NodeList childNodes = node.getChildNodes();
            int childNodesLength = childNodes.getLength();
            for (int k = 0; k < childNodesLength; k++) {
                Node key = childNodes.item(k);
                if (key.getNodeType() == Node.ELEMENT_NODE) {
                    int v;
                    for (v = k + 1; v < childNodesLength; v++) {
                        Node value = childNodes.item(v);
                        if (value.getNodeType() == Node.ELEMENT_NODE) {
                            pMap = pMap.plus(nodeToValue(key), nodeToValue(value));
                            break;
                        }
                    }
                    k = v;
                }
            }
            return pMap;
        } else {
            String textContent = node.getTextContent();
            if (NULL.equals(textContent)) {
                return null;
            } else {
                NamedNodeMap attributes = node.getAttributes();
                PVector<Method> getters = findGetters(cl);
                List<Object> values = new ArrayList<>(getters.size());
                NodeList childNodes = node.getChildNodes();
                int childCounter = 0;

                for (Method getter : getters) {
                    Class<?> returnType = getter.getReturnType();
                    Node namedItem = attributes.getNamedItem(getterNameToFieldName(getter.getName()));
                    if (namedItem != null) { // the value is one of the attributes
                        if (returnType.isPrimitive()) {
                            returnType = Utility.primitiveTypeToWrapper(returnType);
                        }
                        if (Utility.isBasicType(returnType)) {
                            values.add(parseBasicValue(namedItem.getTextContent(), returnType));
                        } else {
                            values.add(nodeToValue(namedItem));
                        }
                    } else { // the value is not among the attributes
                        //todo: remove repetition
                        while (childNodes.item(childCounter).getNodeType() != Node.ELEMENT_NODE) {
                            childCounter++;
                        }
                        values.add(nodeToValue(childNodes.item(childCounter)));
                        childCounter++;
                    }
                }
                return Refl.instantiate(Refl.findConstructor(cl), values.toArray());
            }
        }
    }

    public static Object documentToValue(Document document) throws ClassNotFoundException, SAXException {
        return nodeToValue(document.getChildNodes().item(0));
    }

    public static Object xmlStreamToValue(InputStream reader) throws IOException, SAXException, ClassNotFoundException {
        try {
            return documentToValue(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(reader));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object xmlStringToValue(String s) throws IOException, SAXException, ClassNotFoundException {
        return xmlStreamToValue(new ByteArrayInputStream(s.getBytes()));
    }

    public static <T> PCollection<T> verifyCollection(Object object,
                                                      Class<T> cl) throws IOException,
                                                                          SAXException,
                                                                          ClassNotFoundException {
                if (object instanceof PCollection) {
                    PCollection coll = (PCollection) object;
                    for (Object o : coll) {
                        if (! (cl.isInstance(o))) {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
                return (PCollection<T>) object;
     }

}
