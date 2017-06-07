import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by JoH on 2017-06-06.
 */
public class SCClientGUI extends JFrame {
    SCClient client;
    JTextField input;

    public SCClientGUI(SCClient client) {
        this.client = client;
        setSize(500, 600);
        setTitle("Simple Chatting Client");

        JTextArea msg = new JTextArea();
        add(msg, BorderLayout.CENTER);

        JPanel pSouth = new JPanel();
        add(pSouth, BorderLayout.SOUTH);

        input = new JTextField(20);
        pSouth.add(input, BorderLayout.CENTER);

        JButton btn = new JButton("enter");
        pSouth.add(btn, BorderLayout.LINE_END);
        btn.addActionListener(new sendMessageListener());


        setVisible(true);
    }

    private class sendMessageListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            client.sendMessage(input.getText());
        }
    }
}
