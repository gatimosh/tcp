package server;

import com.google.common.base.Preconditions;

import tcp.TCPEndpoint;
import tcp.TCPServer;

public class Main {

  private static final int PORT = 1111;
  private static final int CORE = 2;
  private static final int MAX = 64;
  private static final int KEEP = 10;
  private static final int QUEUE = 256;

  public static void main(String[] args) throws Exception {

    int port = args != null && args.length == 1 ? Integer.parseInt(args[0]) : PORT;

    WorkerPool pool = new WorkerPool(CORE,MAX, KEEP, QUEUE);

    TCPServer server = new TCPServer(new TCPEndpoint(port), pool);

    server.start();
  }

}

