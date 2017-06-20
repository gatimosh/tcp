package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.TaskMsg;
import tcp.TaskRes;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static tcp.TCPTransport.receive;
import static tcp.TCPTransport.send;

public class Client {

    private static Logger log = LogManager.getLogger(Client.class);
    private static long DELAY = 1; //seconds

    class Receiver implements Runnable {

        Socket socket;
        ConcurrentMap<Integer, TaskRes> results = new ConcurrentHashMap<>();
        Thread thread;

        public Receiver(@Nonnull Socket socket) {
            this.socket = socket;
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            while (!socket.isClosed() && !socket.isInputShutdown()) {
                Object resp;
                try {
                    resp = receive(socket);
                } catch (IOException | ClassNotFoundException e) {
                    log.error(String.format("Exception while receiving: %s", e.getMessage()));
                    // don't we need to close the socket and terminate the run?
                    continue;
                }
                if (resp instanceof TaskRes) {
                    TaskRes casted = (TaskRes) resp;
                    TaskRes prev = results.put(casted.id, casted);
                    if (null != prev) {
                        log.warn(String.format("Something is wrong: result map already contains the result with id %d: %s", prev.id, prev));
                    }
                }
            }
        }
    }

    private final AtomicInteger counter = new AtomicInteger(0);
    private final SocketAddress address;

    private volatile Socket socket;
    private Receiver receiver;

    public Client(String host, int port) {
        address = new InetSocketAddress(host, port);
    }

    public Object remoteCall(String serviceName, String methodName, Object[] params) {
        Socket socket = getSocket();

        Integer id = counter.getAndIncrement();
        TaskMsg msg = new TaskMsg(id, serviceName, methodName, params);
        log.debug(String.format("call(%d) %s",socket.getLocalPort(), msg));

        Object resp;
        try {
            // this is actually an out-stream lock, as receiver is the only reader of input-stream
            synchronized(socket) {
                send(msg, socket);
            }
            while(!receiver.results.containsKey(id)) {
                try {
                    TimeUnit.SECONDS.sleep(DELAY);
                } catch (InterruptedException e) {
                    log.warn("Remote call interrupted, no response for id=" + id);
                    return null;
                }
            }
            resp = receiver.results.remove(id);
        } catch (IOException e) {
            log.error(String.format("Exception while calling %s: %s", msg, e.getMessage()));
            return null;
        }

        log.debug(String.format(" get(%d) %s", socket.getLocalPort(), resp));

        return resp;
    }

    private Socket getSocket() {
        if (socket == null) {
            initSocket();
        }
        return socket;
    }

    private synchronized void initSocket() {
        if (socket == null) {
            try {
                Socket s = new Socket();
                s.connect(address);
                socket = s;
            } catch (Exception e) {
                throw new RuntimeException("Error while opening TCP connection to " + address, e);
            }

            // start receiver thread
            receiver = new Receiver(socket);
        }
    }

    public synchronized void dismiss() {
        if (socket != null && !socket.isClosed()) {
            receiver.thread.interrupt();
            try {
                getSocket().close();
            } catch (IOException e) {
                log.error("Error while closing socket: ", e);
            }
        }
        receiver = null;
        socket = null;
    }
}
