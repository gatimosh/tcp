package client;

public class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 1111;

    public static void main(String[] args) {

        // the only arg is port
        // 2 args are: 'host port'
        String host = args != null && args.length == 2 ? args[0] : HOST;
        int port = PORT;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args.length == 1 ? args[0] : args[1]);
        }

        Client client = new Client("localhost", port);

        client.remoteCall("Service1", "method1", new Object[]{"arg1"});
//        client.remoteCall("service1", "method1", new Object[]{"arg1", new Integer(777)});
//        client.remoteCall("service1", "method1", new Object[]{"arg1", new Double(3.14), "arg3"});

        client.remoteCall("Service1", "getCurrentDate", new Object[0]);

        // multithread client usage

        for(int i = 0; i < 10; i++) {
            new Thread(() -> client.remoteCall("Service1", "getCurrentDate", new Object[0])).start();
        }

//        client.dismiss();
//        System.out.println("Client.main is completed");
    }

}
