package Mail.Classes;

import Mail.Interfaces.Receiver;

import java.util.Arrays;
import java.util.HashMap;

public class CountingReceiver implements Receiver {
    private String[] params;
    private String splitter;
    public CountingReceiver(String[] params, String splitter) {
        this.params = params;
        this.splitter = splitter;
    }

    @Override
    public HashMap<String, Double> parse(String msg) {
        double[] valuesDbl =
                Arrays.stream(msg.split(splitter))
                .mapToDouble(Double::parseDouble)
                .toArray();
        HashMap<String, Double> parseResult = new HashMap<>();
        for (int i = 0; i < params.length; i++) {
            parseResult.put(params[i], valuesDbl[i]);
        }
        return parseResult;
    }
}
