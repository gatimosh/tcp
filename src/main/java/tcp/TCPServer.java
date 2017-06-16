package tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.MsgProcessor;
import server.WorkerPool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {

    private static final Logger log = LogManager.getLogger(TCPServer.class);

    private TCPEndpoint endpoint;
    private WorkerPool workerPool;
    private ServerSocket serverSocket;

    private boolean started = false;

    public TCPServer(TCPEndpoint endpoint, WorkerPool workerPool) {
        this.endpoint = endpoint;
        this.workerPool = workerPool;
    }

    public void run() {
        while (started) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();
            } catch (java.io.InterruptedIOException ignored) {

            } catch (Exception e) {
                log.error(e);
                break;
            }

            if (socket != null) {
                workerPool.execute(new MsgProcessor(socket));
            }
        }
    }

    public void start() throws IOException {
        if (serverSocket == null) {
            if (endpoint.getHost() != null) {
                InetAddress address = InetAddress.getByName(endpoint.getHost());
                serverSocket = new ServerSocket(endpoint.getPort(), endpoint.getBacklog(), address);
            } else {
                serverSocket = new ServerSocket(endpoint.getPort(), endpoint.getBacklog());
            }
        }
        started = true;

        log.info("starting TCP server on port : " + endpoint.getPort());

        run();
    }

}
