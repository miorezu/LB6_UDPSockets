package bulletinBoardService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Frame extends JFrame {
    private JTextField textFieldMsg;
    private JTextField GROUPTextField;
    private JTextField PORTTextField;
    private JTextField NAMETextField;

    private JButton shutdownButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton clearButton;
    private JButton sendButton;
    private JPanel Main;
    private JTextArea textArea;
    private Messenger messenger = null;


    private InetAddress address;
    private int port;
    private String name;

    public Frame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        setLayout(new BorderLayout());

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UITasks ui = (UITasks) Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class[]{UITasks.class},
                        new EDTInvocationHandler(new UITasksImpl()));
                try {
                    address = InetAddress.getByName(GROUPTextField.getText());
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
                port = Integer.parseInt(PORTTextField.getText());
                name = NAMETextField.getText();
                messenger = new MessengerImpl(address, port, name, ui);
                messenger.start();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messenger.send();
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messenger.stop();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText(" ");
            }
        });

        shutdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PROGRAM");
        frame.setContentPane(new Frame().Main);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private class UITasksImpl implements UITasks {
        @Override
        public String getMessage() {
            String res = textFieldMsg.getText();
            textFieldMsg.setText("");
            return res;
        }

        @Override
        public void setText(String txt) {
            textArea.append(txt + "\n");
        }
    }
}