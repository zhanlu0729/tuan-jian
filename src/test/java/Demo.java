import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        System.set
        System.err.println("envList--------------------");
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            System.err.println(entry.getKey() + "=" + entry.getValue());
        }
    }
}
