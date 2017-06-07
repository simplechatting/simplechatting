import java.net.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 6.
 */
public class SCServer{

    static int port = 4444;

    public static void main(String args[]){
        SCServer server = new SCServer();

        Runnable runnable = new SCServerRunnable();
        Thread thread = new Thread(runnable);
        thread.start();

        /* GUI for system management and log */
        SCServerGUI gui = new SCServerGUI(server);
    }

    public void SendMessage(String str){

    }
}
