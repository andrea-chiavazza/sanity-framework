package func.utility.swing;


import func.values.Composite;
import func.values.Mixed;
import org.pcollections.PVector;
import org.testng.annotations.Test;

import static func.values.Values.*;
import static org.testng.Assert.assertEquals;

//todo: shouldn't test that the setValue.equals(getValue), but that X.parse(setValue.toString()).equals(getValue)

public class TestValueEditor {

    @Test
    public void testSetValue() {
        ValueEditor<Composite> valueEditor = new ValueEditor<>(Composite.class);
        for (Composite composite : new Composite[] {c1, c2, c3, c4}) {
            valueEditor.setValue(composite);
            assertEquals(
                valueEditor.getValue(),
                composite);
        }
    }

    @Test
    public void testSetValueVector() {
        ValueEditor<PVector> valueEditor = new ValueEditor<>(PVector.class);
        for (PVector vector : new PVector[] {vc1, vc2, vc3}) {
            valueEditor.setValue(vector);
            assertEquals(
                valueEditor.getValue(),
                vector);
        }
    }

    @Test
    public void testSetValueMixed() {
        ValueEditor<Mixed> valueEditor = new ValueEditor<>(Mixed.class);
        for (Mixed mixed : new Mixed[] {m1, m2}) {
            valueEditor.setValue(mixed);
            assertEquals(
                valueEditor.getValue(),
                mixed);
        }
    }
}
