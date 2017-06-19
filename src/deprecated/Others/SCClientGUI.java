package deprecated.Others;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by JoH on 2017-06-06.
 */
public class SCClientGUI extends JFrame {
    SCClient client;

    // GUI
    JTextField username;
    JTextField input;

    public SCClientGUI(SCClient client) {
        this.client = client;
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Simple Chatting Client");

        // 서버 연결
        JPanel pWest = new JPanel();
        pWest.setLayout(new BoxLayout(pWest, BoxLayout.Y_AXIS));
        add(pWest, BorderLayout.LINE_START);
        // 서버 연결 - 설명
        JLabel label = new JLabel("사용자 이름을 입력하고 start로 시작하세요");
        pWest.add(label);
        // 서버 연결 - 유저 이름란
        username = new JTextField(5);
        pWest.add(username);
        // 서버 연결 - 서버 연결 버튼
        JButton btn1 = new JButton("start");
        pWest.add(btn1);
        btn1.addActionListener(new sendMessageListener());

        // 서버 연결 - 현재 사용자 목록
        JList<String> friends = new JList<>();
        pWest.add(friends);



        // 채팅
        JTextArea msg = new JTextArea();
        add(msg, BorderLayout.CENTER);

        JPanel pSouth = new JPanel();
        add(pSouth, BorderLayout.SOUTH);

        input = new JTextField(20);
        pSouth.add(input, BorderLayout.CENTER);

        JButton btn2 = new JButton("enter");
        pSouth.add(btn2, BorderLayout.LINE_END);
        btn2.addActionListener(new sendMessageListener());


        setVisible(true);
    }

    private class sendMessageListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            switch (e.toString()){
                case "enter":
                    client.sendMessage(input.getText());
                    break;
                case "start":

            }
        }
    }
}
