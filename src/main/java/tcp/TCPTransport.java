package tcp;

import java.io.*;
import java.net.Socket;

public class TCPTransport {

    public static void send(Object obj, Socket socket) throws IOException {

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(obj);
        out.flush();

    }

    public static Object receive(Socket socket) throws IOException, ClassNotFoundException {
        Object obj = new ObjectInputStream(socket.getInputStream()).readObject();
        return obj;
    }
}
