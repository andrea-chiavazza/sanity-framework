package func.utility.swing;

import func.persist.Refl;
import func.persist.Utility;
import func.persist.XMLWrite;
import org.pcollections.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static func.utility.General.invoke;

//todo: add/remove elements from collections/maps
//todo: make entries collapsable
//todo: optional property file that specify field names aliases + class used by coll. fields so that new elements can be added
//todo: add options to make the ValueEditor non-editable/non-nullable, and make map keys non-editable

/** Works like a JTextField, but any composite value can be used rather than just String */
public class ValueEditor<T> extends JPanel {
    private static final String NULL = "null"; // string used to represent a null value
    private final Class<T> cl;
    private final JCheckBox checkBox = new JCheckBox(NULL);
    private Class<?> elCl;
    private Class<?> keyCl;
    private final Map<Object,Object> m;

    public ValueEditor(final Class<T> cl) {
        this(cl, null);
    }

    public ValueEditor(final Class<T> cl,
                       final Class<?> elCl) {
        this(cl, elCl, Collections.EMPTY_MAP);
    }

    public ValueEditor(final Class<T> cl,
                       final Class<?> elCl,
                       final Map<Object,Object> m) {
        this(cl, elCl, null, m);
    }

    public ValueEditor(final Class<T> cl,
                       final Class<?> elCl,
                       final Class<?> keyCl,
                       final Map<Object,Object> m) {
        this.cl = cl;
        this.elCl = elCl;
        this.keyCl = keyCl;
        this.m = m;

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 0, 3);
        c.gridy = 0;

        if (Utility.isBasicType(cl)) {
            JTextField textField = new JTextField();
            textField.setMinimumSize(textField.getPreferredSize());
            textField.setColumns(12);
            textField.setToolTipText(cl.getSimpleName());
            c.gridx = 0;
            add(textField, c);
            if (! cl.isPrimitive()) {
                c.gridx = 1;
                add(checkBox, c);
            }
        } else if (Collection.class.isAssignableFrom(cl)) {
            setBorder();
            add(checkBox, c);
            addCollAddButton(c);
        } else if (Map.class.isAssignableFrom(cl)) {
            //todo
        } else {
            setBorder();

            c.gridx = 0;
            add(checkBox, c);
            c.gridy++;
            for (Method getter : Refl.findGetters(cl)) {
                Class<?> returnType = getter.getReturnType();
                c.gridx = 0;
                String getterName = getter.getName();
                String key = cl.getName() + "/" + getterName;
                String fieldLabelName;
                if (m.containsKey(key)) {
                    fieldLabelName = m.get(key).toString();
                } else {
                    fieldLabelName = XMLWrite.getterNameToFieldName(getterName);
                }
                JLabel label = new JLabel(fieldLabelName);
                label.setToolTipText(returnType.getName());
                add(label, c);
                c.gridx = 1;
                ValueEditor<?> valueEditor;
                if (Collection.class.isAssignableFrom(returnType)) {
                    String k = key + "/TYPE";
                    if (m.containsKey(k)) {
                        valueEditor = new ValueEditor<>(returnType, Refl.classFromName(m.get(k).toString()), m);
                    } else {
                        valueEditor = new ValueEditor<>(returnType, null, m);
                    }
                } else {
                    valueEditor = new ValueEditor<>(returnType, null, m);
                }
                add(valueEditor, c);
                c.gridy++;
            }
        }
    }

    public Class<T> getCl() {
        return cl;
    }

    public void setValue(final T value) {
        if (value != null) {
            if (!Utility.primitiveTypeToWrapper(cl).isInstance(value)) {
                throw new IllegalArgumentException(
                    "value is of type " + value.getClass().getName() + " should be " + cl.getName());
            }
            checkBox.setSelected(false);
        } else {
            checkBox.setSelected(true);
            return;
        }

        Component[] components = getComponents();
        if (Utility.isBasicType(cl)) {
            ((JTextField) getComponents()[0]).setText(value.toString());
        } else if (value instanceof Collection) {
            //todo: make buttons small
            //todo: add move up/move down buttons
            removeAll();

            // works out the element class from objects in the collection
            if (elCl == null) {
                for (Object el : (Collection) value) {
                    if (el != null) {
                        Class<?> elClass = el.getClass();
                        if (elCl == null || elClass.isAssignableFrom(elCl)) {
                            elCl = elClass;
                        }
                    }
                }
            }

            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.LINE_START;
            c.insets = new Insets(0, 3, 0, 3);
            c.gridy = 0;

            addCollAddButton(c);

            int i = 0;
            for (Object el : (Collection) value) {
                c.gridx = 0;
                JLabel label = new JLabel(Integer.toString(i));
                label.setToolTipText(elCl.getName());
                add(label, c);

                c.gridx = 1;
                if (elCl == null) {
                    add(new JLabel(NULL), c);
                } else {
                    ValueEditor component = new ValueEditor<>(elCl);
                    add(component, c);
                    component.setValue(el);
                }
                c.gridx = 2;
                addRemoveButton(c, i, el);

                c.gridy++;
                i++;
            }
            c.gridx = 0;
        } else if (value instanceof Map) {
            removeAll();

            // works out the value class from values in the map
            if (elCl == null) {
                for (Object el : ((Map) value).values()) {
                    if (el != null) {
                        Class<?> elClass = el.getClass();
                        if (elCl == null || elClass.isAssignableFrom(elCl)) {
                            elCl = elClass;
                        }
                    }
                }
            }

            // works out the key class from keys in the map
            if (keyCl == null) {
                for (Object key : ((Map) value).keySet()) {
                    if (key != null) {
                        Class<?> elClass = key.getClass();
                        if (keyCl == null || elClass.isAssignableFrom(keyCl)) {
                            keyCl = elClass;
                        }
                    }
                }
            }

            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.LINE_START;
            c.insets = new Insets(0, 3, 0, 3);
            c.gridy = 0;

            addMapAddButton(c);

            for (Map.Entry entry : ((Map<?,?>) value).entrySet()) {
                //todo: the key must not be editable !!
                c.gridx = 0;
                //todo: null key ?
                ValueEditor keyComponent = new ValueEditor<>(entry.getKey().getClass(), null, null, m);
                add(keyComponent, c);
                keyComponent.setValue(entry.getKey());

                c.gridx = 1;
                //todo: null value ?
                ValueEditor valueComponent = new ValueEditor<>(entry.getValue().getClass());
                add(valueComponent, c);
                valueComponent.setValue(entry.getValue());

                c.gridy++;
            }
            c.gridx = 0;
        } else {
            PVector<Method> getters = Refl.findGetters(cl);
            int gettersSize = getters.size();
            int componentsLength = components.length;
            int v = 0;
            int i = 1;
            while (v < gettersSize && i < componentsLength) {
                i++; // skips JLabel
                ((ValueEditor) components[i]).setValue(invoke(value, getters.get(v)));
                v++;
                i++;
            }
        }
        revalidate();
    }

    public T getValue() {
        if (checkBox.isSelected()) {
            return null;
        }

        Component[] components = getComponents();

        if (Utility.isBasicType(cl)) {
            return
                parseBasicValue(
                    ((JTextField) components[0]).getText(),
                    cl);
        } else if (Collection.class.isAssignableFrom(cl)) {
            PCollection<Object> coll;
            if (PVector.class.isAssignableFrom(cl)) { //todo: why List.class doesn't work ?
                coll = Empty.vector();
            } else if (POrderedSet.class.isAssignableFrom(cl)) {
                coll = Empty.orderedSet();
            } else if (Set.class.isAssignableFrom(cl)) {
                coll = Empty.set();
            } else {
                throw new RuntimeException("Unsupported collection " + cl);
            }
            for (Component component : components) {
                if (component instanceof ValueEditor) {
                    coll = coll.plus(((ValueEditor) component).getValue());
                }
            }
            return (T) coll;
        } else if (Map.class.isAssignableFrom(cl)) {
            PMap<Object,Object> map = Empty.map();
            for (int i = 0; i < components.length; i++) {
                Component kComp = components[i];
                if (kComp instanceof ValueEditor) {
                    Object key = ((ValueEditor) kComp).getValue();
                    i++;
                    while (i < components.length) {
                        Component vComp = components[i];
                        if (vComp instanceof ValueEditor) {
                            map = map.plus(key, ((ValueEditor) vComp).getValue());
                            break;
                        }
                        i++;
                    }
                }
            }
            return (T) map;
        } else {
            PVector<Method> getters = Refl.findGetters(cl);
            int gettersSize = getters.size();
            Object[] values = new Object[gettersSize];
            int g = 0;
            int c = 0;
            while (g < gettersSize) {
                Component component = components[c];
                Class<?> returnType = getters.get(g).getReturnType();
                if (component instanceof JTextField) {
                    values[g] = parseBasicValue(
                        ((JTextField) component).getText(),
                        returnType);
                    g++;
                } else if (component instanceof ValueEditor) {
                    values[g] = ((ValueEditor) component).getValue();
                    g++;
                }
                c++;
            }
            return Refl.instantiate(Refl.findConstructor(cl), values);
        }
    }

    //__________________________________________________________________________________________________________________

    private void addCollAddButton(GridBagConstraints c) {
        if (elCl != null) {
            JButton addButton = new JButton("+");
            addButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setValue((T) ((PCollection) getValue()).plus(Refl.makeDefaultValue(elCl)));
                    }
                });
            add(addButton, c);
            c.gridy++;
        }
    }

    private void addMapAddButton(GridBagConstraints c) {
        if (elCl != null) {
            JButton addButton = new JButton("+");
            addButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setValue(
                            (T) ((PMap) getValue()).plus(Refl.makeDefaultValue(keyCl), Refl.makeDefaultValue(elCl)));
                    }
                });
            add(addButton, c);
            c.gridy++;
        }
    }

    private void addRemoveButton(final GridBagConstraints c,
                                 final int i,
                                 final Object el) {
        if (elCl != null) {
            JButton addButton = new JButton("-");
            addButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        PCollection value = (PCollection) getValue();
                        if (value instanceof PVector) {
                            setValue((T) ((PVector) value).minus(i));
                        } else if (value instanceof POrderedSet) {
                            setValue((T) ((POrderedSet) value).minus(el));
                        } else if (value instanceof PSet) {
                            setValue((T) ((PSet) value).minus(el));
                        }
                    }
                });
            add(addButton, c);
            c.gridy++;
        }
    }

    private void setBorder() {
        setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5))));
    }

    private static <T> T parseBasicValue(String text,
                                         Class<T> cl) {
        if (cl != String.class && NULL.equals(text)) {
            return null;
        }

        Class<T> unwrapped = Utility.wrapperToPrimitiveType(cl);
        Object result;
        if (unwrapped == Boolean.TYPE) {
            result = Boolean.parseBoolean(text);
        } else if (unwrapped == Character.TYPE) {
            result = text.charAt(0);
        } else if (unwrapped == Byte.TYPE) {
            result = Byte.parseByte(text);
        } else if (unwrapped == Short.TYPE) {
            result = Short.parseShort(text);
        } else if (unwrapped == Integer.TYPE) {
            result = Integer.parseInt(text);
        } else if (unwrapped == Long.TYPE) {
            result = Long.parseLong(text);
        } else if (unwrapped == Float.TYPE) {
            result = Float.parseFloat(text);
        } else if (unwrapped == Double.TYPE) {
            result = Double.parseDouble(text);
        } else if (unwrapped == String.class) {
            result = text;
        } else if (unwrapped == BigInteger.class) {
            result = new BigInteger(text);
        } else if (unwrapped == BigDecimal.class) {
            result = new BigDecimal(text);
        } else {
            throw new RuntimeException("Illegal value \"" + text + "\"");
        }
        return (T) result;
    }

}
