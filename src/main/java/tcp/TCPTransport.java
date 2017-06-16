package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.stream.Collectors;

public class TCPTransport {

    public static void send(String msg, Socket socket) throws IOException {

        OutputStream out = socket.getOutputStream();
        out.write((msg + "\r\n").getBytes("UTF-8"));
        out.flush();

    }

    public static String receive(Socket socket) throws IOException {
        // FIXME read bytes not lines
        String resp = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
        return resp;
    }

    public static Socket openConnection(String host, int port, int timeout) {
        try {
            SocketAddress address = new InetSocketAddress(host, port);
            Socket socket = new Socket();
            if (timeout != -1) {
                socket.setSoTimeout(timeout);
            }
            socket.connect(address);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error while opening TCP connection to %s:%d ", host, port), e);
        }
    }
}
