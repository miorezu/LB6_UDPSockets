package bulletinBoardService;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MessengerImpl implements Messenger {
    private UITasks ui = null;
    private MulticastSocket group = null;
    private InetAddress address = null;
    private int port;
    private String name;
    private boolean canceled = false;

    public MessengerImpl(InetAddress address, int port, String name, UITasks ui) {
        this.name = name;
        this.ui = ui;
        this.address = address;
        this.port = port;
        try {
            group = new MulticastSocket(port);
            group.setTimeToLive(2);
            group.joinGroup(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        Thread t = new Receiver();
        t.start();
    }

    @Override
    public void stop() {
        cancel();
        try {
            group.leaveGroup(address);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Disconnect error\n" +
                    e.getMessage());
        } finally {
            group.close();
        }
    }

    @Override
    public void send() {
        new Sender().start();
    }

    private class Sender extends Thread {
        public void run() {
            try {
                String msg = name + ": " + ui.getMessage();
                byte[] out = msg.getBytes();
                DatagramPacket pkt = new DatagramPacket(out, out.length, address,
                        port);
                group.send(pkt);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Send error\n" +
                        e.getMessage());
            }
        }
    }

    private class Receiver extends Thread {
        public void run() {
            try {
                byte[] in = new byte[512];
                DatagramPacket pkt = new DatagramPacket(in, in.length);
                while (!isCanceled()) {
                    group.receive(pkt);
                    ui.setText(new String(pkt.getData(), 0, pkt.getLength()));
                }
            } catch (Exception e) {
                if (isCanceled()) {
                    JOptionPane.showMessageDialog(null, "You are disconnected");
                } else {
                    JOptionPane.showMessageDialog(null, "Receiving error\n" +
                            e.getMessage());
                }
            }
        }
    }

    private synchronized boolean isCanceled() {
        return canceled;
    }

    public synchronized void cancel() {
        canceled = true;
    }
}