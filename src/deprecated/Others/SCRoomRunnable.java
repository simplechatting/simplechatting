package deprecated.Others;

import java.net.*;
import java.io.*;

class SCRoomRunnable implements Runnable {
    protected SCRoom chatRoom = null;
    protected Socket clientSocket = null;
    protected PrintWriter out = null;
    protected BufferedReader in = null;
    public int clientID = -1;

    public SCRoomRunnable(SCRoom room, Socket socket){
        this.chatRoom = room;
        this.clientSocket = socket;
        clientID = clientSocket.getPort();
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch(IOException i){
        }
    }

    @Override
    public void run(){
        try{
            String inputLine;
            while((inputLine = in.readLine()) != null){
                chatRoom.putClient(getClientID(), getClientID() + " : " + inputLine);
                if(inputLine.equalsIgnoreCase("Bye.")) break;
            }
            chatRoom.delClient(getClientID());
        }catch(SocketTimeoutException ste){
            System.out.println("Socket timeout Occured, force close() : " + getClientID());
            chatRoom.delClient(getClientID());
        }catch(IOException e){
            chatRoom.delClient(getClientID());
        }
    }

    public int getClientID(){
        return clientID;
    }

    public void close(){
        try{
            if(in != null) in.close();
            if(out != null) out.close();
            if(clientSocket != null) clientSocket.close();
        }catch(IOException i){
        }
    }
}