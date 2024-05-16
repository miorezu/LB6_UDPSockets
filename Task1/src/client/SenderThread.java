package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SenderThread extends Thread {
    private InetAddress server;
    private int port;
    private DatagramSocket socket;
    private volatile boolean stopped = false;

    SenderThread(DatagramSocket socket, InetAddress address, int port) {
        this.server = address;
        this.port = port;
        this.socket = socket;
        this.socket.connect(server, port);
    }

    public void halt() {
        this.stopped = true;
    }

    @Override
    public void run() {

        try {
            BufferedReader userInput = new BufferedReader(
                    new InputStreamReader(System.in));
            if (stopped)
                return;
            while (true) {
                String theLine = userInput.readLine();
                if (theLine.equals("."))
                    break;
                byte[] data = theLine.getBytes("UTF-8");
                DatagramPacket output = new DatagramPacket(data, data.length, server,
                        port);
                socket.send(output);
                Thread.yield();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}