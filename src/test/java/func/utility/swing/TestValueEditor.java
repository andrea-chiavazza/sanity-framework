package func.utility.swing;


import func.values.Composite;
import func.values.Mixed;
import org.pcollections.PVector;
import org.testng.Assert;
import org.testng.annotations.Test;

import static func.values.Values.*;

//todo: shouldn't test that the setValue.equals(getValue), but that X.parse(setValue.toString()).equals(getValue)

public class TestValueEditor {

    @Test
    public void testSetValue() {
        ValueEditor<Composite> valueEditor = new ValueEditor<>(Composite.class);
        for (Composite composite : new Composite[] {c1, c2, c3, c4}) {
            valueEditor.setValue(composite);
            Assert.assertEquals(
                valueEditor.getValue(),
                composite);
        }
    }

    @Test
    public void testSetValueVector() {
        ValueEditor<PVector> valueEditor = new ValueEditor<>(PVector.class);
        for (PVector vector : new PVector[] {vi1, vi2, vi3, vi4, vc1, vc2, vc3, vs1, vs2, vs3}) {
            valueEditor.setValue(vector);
            Assert.assertEquals(
                valueEditor.getValue(),
                vector);
        }
    }

    @Test
    public void testSetValueMixed() {
        ValueEditor<Mixed> valueEditor = new ValueEditor<>(Mixed.class);
        for (Mixed mixed : new Mixed[] {m1, m2}) {
            valueEditor.setValue(mixed);
            Assert.assertEquals(
                valueEditor.getValue(),
                mixed);
        }
    }
}
