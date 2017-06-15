package Settings;

import java.nio.charset.*;

/**
 * Created by penguin on 17. 6. 13.
 */
public class SCSettings {
    public static String host = "localhost";
    public static int port = 4546;
    public static int datagramSize = 512;
    public static int datagramInterval = 300;
    public static Charset charset = Charset.forName("UTF-8");
}
