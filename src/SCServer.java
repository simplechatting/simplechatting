import com.sun.corba.se.spi.activation.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 6.
 */

public class SCServer implements Runnable{
    Selector selector;

    public static void main(String args[]) throws IOException {
        Thread t = new Thread(new SCServer());
        t.run();
        DummyMsgSender.STARTMAIN();
        System.out.println("success to run");
    }

    public SCServer() throws IOException{
        // non block io 생성
        DatagramChannel channel = DatagramChannel.open();
        SocketAddress addr = new InetSocketAddress(SCSettings.port);
        channel.bind(addr);
        channel.configureBlocking(false);


        // 셀렉터 생성
        selector = Selector.open();


        // selector에 등록
        int socketOPs = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        selector.wakeup();
        SelectionKey key = channel.register(selector, SelectionKey.OP_ACCEPT);

    }
    @Override
    public void run() {
        System.out.println("success to run");
        ByteBuffer buf = ByteBuffer.allocateDirect(SCSettings.datagramSize);
        try{
            while(true) {
                System.out.println("waiting...");
                if (selector.select() > 0) {
                    System.out.println("has selector");
                    System.out.println(selector.toString());
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        try {
                            SelectionKey key = (SelectionKey) iterator.next();
                            iterator.remove();

                            if (!key.isValid())
                                continue;

                            if (key.isReadable()) {
                                DatagramChannel channel = (DatagramChannel) key.channel();
                                SocketAddress socketAddress = channel.receive(buf);

                                System.out.println(buf.toString());

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
}