package func.values;

import func.basic.Ob;
import org.pcollections.PSet;
import org.pcollections.PVector;

public class WithPCollections extends Ob {
    private final int no;
    private final PVector<String> names;
    private final PSet<Integer> numbers;

    public WithPCollections(int no, PVector<String> names, PSet<Integer> numbers) {
        this.no = no;
        this.names = names;
        this.numbers = numbers;
    }

    public int getNo() {
        return no;
    }

    public PVector<String> getNames() {
        return names;
    }

    public PSet<Integer> getNumbers() {
        return numbers;
    }

}
