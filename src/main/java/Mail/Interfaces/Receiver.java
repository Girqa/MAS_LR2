package Mail.Interfaces;

import java.util.HashMap;

public interface Receiver {
    HashMap<String, Double> parse(String msg);
}
