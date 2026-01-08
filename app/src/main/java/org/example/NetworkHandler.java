package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkHandler implements AutoCloseable {
    private final BufferedReader in;
    private final PrintWriter out;
    private final Socket socket;

    private NetworkHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public static NetworkHandler serverHandler(int port) throws IOException {
        try(ServerSocket s = new ServerSocket(port)) {
            System.out.println("WAITING FOR SECOND PLAYER...");
            Socket newSocket = s.accept();
            return new NetworkHandler(newSocket);
        }
    }
    public static NetworkHandler clientHandler(String host, int port) throws IOException {
        Socket s = new Socket(host, port);
        return new NetworkHandler(s);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String readLine() throws IOException {
        return in.readLine();
    }

    @Override
    public void close() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }
}