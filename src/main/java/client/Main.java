package client;

public class Main {

    public static void main(String[] args) {

        sendMsg();
    }

    private static void sendMsg() {

        Client client = new Client("localhost", 1111);

        client.remoteCall("service1", "method1", new Object[]{"arg1"});
        client.remoteCall("service1", "method1", new Object[]{"arg1", new Integer(777)});
        client.remoteCall("service1", "method1", new Object[]{"arg1", new Double(3.14), "arg3"});

        System.out.println("Client.main is completed");
    }

}
