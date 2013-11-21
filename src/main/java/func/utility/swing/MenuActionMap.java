package func.utility.swing;

import javax.swing.*;
import java.util.HashMap;

/**
 * User: andrea
 * Date: 20/11/13
 * Time: 09:59
 */
public class MenuActionMap extends HashMap<String,Action> {
    public Action put(String key,
                      Action value) {
        value.putValue(Action.NAME, key);
        return super.put(key, value);
    }
}
