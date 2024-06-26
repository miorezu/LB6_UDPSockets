package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class ReceiverThread extends Thread {
    private DatagramSocket socket;
    private volatile boolean stopped = false;

    ReceiverThread(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[65507];
        while (true) {
            if (stopped)
                return;
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                String s = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                System.out.println(s);
                Thread.yield();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

    public void halt() {
        this.stopped = true;
    }
}