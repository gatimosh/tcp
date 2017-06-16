package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

import static tcp.TCPTransport.openConnection;
import static tcp.TCPTransport.receive;
import static tcp.TCPTransport.send;

public class Main {

    private static Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        sendMsg("test");
    }

    private static void sendMsg(String msg) throws IOException {

        Socket socket = openConnection("localhost", 1111, -1);

        log.debug("put " + msg);
        send(msg, socket);

        String resp = receive(socket);
        log.debug("get " + resp);
    }

}
