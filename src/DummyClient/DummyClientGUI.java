package DummyClient;

import Settings.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;

/**
 * Created by penguin on 17. 6. 15.
 */
public class DummyClientGUI {
    public JPanel GUI;
    private JTextField userName;
    private JButton connectServer;
    private JPanel Login;
    private JList RoomList;
    private JTextField newRoomName;
    private JButton addNewRoom;
    private JPanel Rooms;
    private JPanel Room;
    private JLabel roomName;
    private JPanel chatting;
    private JTextField textField1;
    private JButton sendMessage;
    private JButton backToRoomList;
    private JButton leaveRoom;
    private JButton Logout;
    private JLabel loginMsg;

    public DummyClient controler;
    public DummyClientDB model;

    public DummyClientGUI() {
        ////////////////// 로그인 화면 //////////////////
        connectServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    // TODO: Server와 Connect후, userid 받아옴
                    String username = userName.getText();
                    controler.startClient(username);
                    model.startDB(username);

                    // 룸 목록 이동
                    loginMsg.setText("서버 연결됨");
                    Thread.sleep(500);
                    Login.setVisible(false);
                    Rooms.setVisible(true);

                } catch (IOException | InterruptedException e) {
                    loginMsg.setText(e.getMessage());
                }
            }
        });

        ////////////////// 룸 목록 화면 //////////////////
        addNewRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // TODO: 서버에 새로운 방 요청
                String roomName =  newRoomName.getText();
                SCPacket packet = new SCPacket(SCPacketType.CREATE_ROOM, roomName);
                controler.send(packet);
                // TODO: 서버에서 방 목록 불러오기

                // TODO: RoomList에 새로운 방 추가

            }
        });
        Logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // TODO: Server와 연결 종료
                controler.stopClient();
                // 로그인 화면으로 돌아가기
                Rooms.setVisible(false);
                Login.setVisible(true);
            }
        });
        RoomList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    JList source = (JList)event.getSource();
                    String selected = source.getSelectedValue().toString();
                    // TODO: selected 채팅 룸을 연결하도록 서버에 요청

                    // 룸으로 이동
                    Login.setVisible(false);
                    Room.setVisible(true);

                    // TODO: 이미 들어갔던 적이 있던 룸: 모델에서 룸 정보를 언팩

                    // TODO: 들어간 적이 없는 룸: 서버에서 룸 정보를 언팩
                }
            }
        });

        ////////////////// 룸 화면  //////////////////
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // TODO: 서버에 메시지 전송
            }
        });
        backToRoomList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // 룸 리스트로 이동
                Room.setVisible(false);
                Rooms.setVisible(true);
            }
        });
        leaveRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //  TODO: 서버에 룸에서 나감을 요청

                // TODO: db에서 룸 정보 삭제

                // 룸 리스트로 이동
                Room.setVisible(false);
                Rooms.setVisible(true);
            }
        });
    }
}
