package Mail.Classes;

public class Test {
    public static void main(String[] args) {
        Double[] array = new Double[]{1.0, 2.0, 3.0};
        CountingSender s = new CountingSender("/");
        System.out.println(s.prepareMsg(array));
    }
}
