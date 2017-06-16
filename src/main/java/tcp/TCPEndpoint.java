package tcp;

public class TCPEndpoint {

    private String host = null;
    private int port = -1;
    private int backlog = -1;

    public TCPEndpoint(int port) {
        this("localhost", port, 50);
    }

    public TCPEndpoint(String host, int port, int backlog) {
        this.host = host;
        this.port = port;
        this.backlog = backlog;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getBacklog() {
        return backlog;
    }
}
