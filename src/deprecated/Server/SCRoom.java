package deprecated.Server;

import java.net.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 18.
 */
public class SCRoom {
    public String roomName;
    public HashMap<String, Integer> userReads = new HashMap<>();
    public HashMap<String, InetSocketAddress> members = new HashMap<>();
    int currentMin = 0;
}
