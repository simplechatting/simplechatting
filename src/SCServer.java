import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 15.
 */
public class SCServer implements Runnable{

    public static void main(String args[]){

    }

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private SocketChannel socketChannel;
    private HashMap<Integer, SCRoom> scRooms = new HashMap<>();

    public SCServer() throws IOException{
        // selector 생성
        selector = Selector.open();

        // channel 바인드
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(SCSettings.port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true){
            try {
                if(selector.select() == 0)
                    continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                while(iter.hasNext()){
                    SelectionKey key = iter.next();

                    if(key.isAcceptable()){
                        accept(key);
                    }else if(key.isReadable()){

                    }else if(key.isWritable()){

                    }
                }
            } catch (IOException e) {
                System.out.println("server stopped");
                if(serverChannel.isOpen())
                    restartServer();
                else
                    System.out.println("IOException occurred.");
            }
        }
    }

    public void restartServer(){
        try{
            System.out.println("restart server");
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(SCSettings.port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            System.out.println("fail to restart : ClosedChannelException");
        } catch (IOException e) {
            System.out.println("fail to restart : IOException");
        }
    }

    public void accept(SelectionKey key) throws IOException{
        serverChannel = (ServerSocketChannel)key.channel();
        socketChannel = serverChannel.accept();

        String msg = ""
    }
}
