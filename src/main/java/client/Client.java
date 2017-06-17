package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.TaskMsg;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import static tcp.TCPTransport.receive;
import static tcp.TCPTransport.send;

public class Client {

    private static Logger log = LogManager.getLogger(Client.class);

    private final AtomicInteger counter = new AtomicInteger(0);
    private final SocketAddress address;

    public Client(String host, int port) {
        address = new InetSocketAddress(host, port);
    }

    public Object remoteCall(String serviceName, String methodName, Object[] params) {
        Socket socket = openSocket();

        TaskMsg msg = new TaskMsg(counter.getAndIncrement(), serviceName, methodName, params);
        log.debug(String.format("call(%d) %s",socket.getLocalPort(), msg));

        Object resp = null;
        try {
            send(msg, socket);
            resp = receive(socket);
        } catch (IOException | ClassNotFoundException e) {
            log.error(String.format("Exception while calling %s: %s", msg, e.getMessage()));
            return null;
        }

        log.debug(String.format(" get(%d) %s", socket.getLocalPort(), resp));

        return resp;
    }

    private Socket openSocket() {
        try {
            Socket socket = new Socket();
            socket.connect(address);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("Error while opening TCP connection to " + address, e);
        }
    }
}
