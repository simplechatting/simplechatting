package deprecated.Others;

import Settings.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 6.
 */

public class SCServer_UDP implements Runnable{
    Selector selector;

    public static void main(String args[]) throws IOException {
        Thread t = new Thread(new SCServer_UDP());
        t.run();
        DummyMsgSender.STARTMAIN();
        System.out.println("success to run");
    }

    public SCServer_UDP() throws IOException{
        // non block io 생성
        DatagramChannel channel = DatagramChannel.open();
        SocketAddress addr = new InetSocketAddress(Settings.SCSettings.port);
        channel.bind(addr);
        channel.configureBlocking(false);

        // 셀렉터 생성
        selector = Selector.open();

        // selector에 등록
        int socketOPs = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        selector.wakeup();
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

    }
    @Override
    public void run() {
        System.out.println("success to run");
        ByteBuffer buf = ByteBuffer.allocateDirect(Settings.SCSettings.datagramSize);
        /*DEBUG*/ int debugi=0, tmpi = 0;
        try{
            while(true) {
                if(debugi != tmpi) {
                    System.out.println("waiting...");
                    tmpi = debugi;
                }
                if (selector.select() > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        debugi++;
                        try {
                            SelectionKey key = (SelectionKey) iterator.next();
                            iterator.remove();

                            if (!key.isValid())
                                continue;

                            if (key.isReadable()) {
                                processRequest(key);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("run end");
    }

    private void processRequest(SelectionKey key) throws IOException{
        DatagramChannel channel = (DatagramChannel) key.channel();

        ByteBuffer buf = ByteBuffer.allocateDirect(Settings.SCSettings.datagramSize);
        SocketAddress socketAddress = channel.receive(buf);
        byte bytes[] = new byte[buf.position()];
        buf.flip();
        buf.get(bytes);
        String data = new String(bytes);
    }
}
