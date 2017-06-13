/**
 * Created by kuman514 on 2017-06-06.
 */

import java.net.*;
import java.io.*;

public class SCRoom implements Runnable {

    private SCRoomRunnable clients[] = new SCRoomRunnable[5];
    public int clientCount = 0;

    private int ePort = -1;

    public SCRoom(int port) {
        this.ePort = port;
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(ePort);
            System.out.println ("Server started: socket created on " + ePort);

            while (true) {
                addClient(serverSocket);
            }
        } catch (BindException b) {
            System.out.println("Can't bind on: "+ePort);
        } catch (IOException i) {
            System.out.println(i);
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    public int whoClient(int clientID) {
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getClientID() == clientID)
                return i;
        return -1;
    }

    public void putClient(int clientID, String inputLine) {
        System.out.println("Sender - Message : " + inputLine);
        String sTo = "To : ";

        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getClientID() == clientID) {
                continue;
            } else {
                sTo = sTo + clients[i].getClientID() + " ";
                clients[i].out.println(inputLine);
            }
        }

        System.out.println(sTo + "\n");
    }

    public void addClient(ServerSocket serverSocket) {
        Socket clientSocket = null;

        if (clientCount < clients.length) {
            try {
                clientSocket = serverSocket.accept();
                //clientSocket.setSoTimeout(40000);         // 1000/sec
            } catch (IOException i) {
                System.out.println ("Accept() fail: "+i);
            }
            clients[clientCount] = new SCRoomRunnable(this, clientSocket);
            new Thread(clients[clientCount]).start();
            clientCount++;
            System.out.println ("Client connected: " + clientSocket.getPort()
                    +", CurrentClient: " + clientCount);
        } else {
            try {
                Socket dummySocket = serverSocket.accept();
                SCRoomRunnable dummyRunnable = new SCRoomRunnable(this, dummySocket);
                new Thread(dummyRunnable);
                dummyRunnable.out.println(dummySocket.getPort() + " < Sorry maximum user connected now");
                System.out.println("Client refused: maximum connection " + clients.length + " reached.");
                dummyRunnable.close();
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    public synchronized void delClient(int clientID) {
        int pos = whoClient(clientID);
        SCRoomRunnable endClient = null;
        if (pos >= 0) {
            endClient = clients[pos];
            if (pos < clientCount-1)
                for (int i = pos+1; i < clientCount; i++)
                    clients[i-1] = clients[i];
            clientCount--;
            System.out.println("Client removed: " + clientID + " at clients[" + pos +"], CurrentClient: " + clientCount);
            endClient.close();
        }
    }

    public SCRoom getRoom() {return this;}

    /*
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: Classname ServerPort");
            System.exit(1);
        }
        int ePort = Integer.parseInt(args[0]);

        new Thread(new SCRoom(ePort)).start();
    }
    */
}
