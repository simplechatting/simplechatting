package deprecated;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 19.
 */
public class GUI {
    private JTextField message;
    private JButton send;
    private JTextField username;
    private JButton Login;
    private JButton Attach;
    private JButton Logout;
    private JTextPane messageTotal;
    public JPanel panel;

    // support datum
    public List<String> messages = new LinkedList<>();
    public String messagehtml = "";

    // controller 연결
    public SCClient client;

    // 업데이트
    public void addMsg(SCPacket msg){
        String newMsg = "[" + msg.getUser() + "] " + msg.getMessage();
        messages.add(newMsg);
        messagehtml += "<br>" + newMsg;

        messageTotal.setText("<html>" + messagehtml + "</html>");
    }

    // 리스너
    public GUI(SCClient client) {
        this.client = client;
        messageTotal.setContentType("text/html");

        Login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    client.startServer();
                    client.login(username.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                client.logout();
            }
        });
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                client.sendMsg(message.getText());
                message.setText("");
            }
        });
        message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()==KeyEvent.VK_ENTER){
                    client.sendMsg(message.getText());
                    message.setText("");
                }
        }});

    }
}
