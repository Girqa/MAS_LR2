package Mail.Classes;

import Mail.Interfaces.Sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CountingSender implements Sender {
    private String separator;

    public CountingSender(String separator) {
        this.separator = separator;
    }

    @Override
    public String prepareMsg(Double[] values) {
        List<Double> list = Arrays.asList(values);
        return Arrays.asList(values)
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(separator));
    }
}
