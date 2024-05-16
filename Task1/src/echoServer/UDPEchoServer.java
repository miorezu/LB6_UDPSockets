package echoServer;

import java.io.*;
import java.net.*;

public class UDPEchoServer extends UDPServer {

    public final static int DEFAULT_PORT = 1200;

    public UDPEchoServer() {
        super(DEFAULT_PORT);
    }

    @Override
    public void respond(DatagramSocket socket, DatagramPacket request)
            throws IOException {
        DatagramPacket reply = new DatagramPacket(request.getData(),
                request.getLength(), request.getAddress(), request.getPort());

        try {
            socket.send(reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        UDPServer server = new UDPEchoServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
        System.out.println("Start echo-server...");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.shutDown();
        System.out.println("Finish echo-server...");
    }
}