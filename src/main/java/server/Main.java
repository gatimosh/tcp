package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import config.Gt;
import org.aeonbits.owner.ConfigFactory;
import tcp.TCPEndpoint;
import tcp.TCPServer;

public class Main {

  private static final Logger log = LogManager.getLogger(Main.class);

  public static void main(String[] args) throws Exception {

    Gt cfg = ConfigFactory.create(Gt.class);

    TCPEndpoint endpoint = new TCPEndpoint(cfg.port());

    int queueLength = 200;
    WorkerPool pool = new WorkerPool(2,4, 10, queueLength);

    TCPServer server = new TCPServer(endpoint, pool);

    server.start();
  }

}

