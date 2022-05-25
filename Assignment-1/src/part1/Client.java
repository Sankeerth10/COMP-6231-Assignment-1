package part1;
import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Simple UDP socket client to send request and wait for response from server.
 * @author
 */
public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usage: java Client <hostname> <port>");
            return;
        }

        String hostname = args[0];
        String message = "";
        int port = Integer.parseInt(args[1]);

        try {
            InetAddress address = InetAddress.getByName(hostname);
            DatagramSocket ds = new DatagramSocket();
            Scanner in = new Scanner(System.in);
            System.out.print("Command set:\n" +
                    "ADD <Key> <Value>\n" +
                    "UPDATE <Key> <Value>\n" +
                    "DELETE <Key>\n" +
                    "LIST_KEYS\n"+
                    "GET_VALUE <Key>\n" +
                    "GET_VALUES <key>\n" +
                    "SUM <Key>\n" +
                    "AVG <Key>\n" +
                    "RESET\n" +
                    "EXIT\n"
                     );
            while (true) {
                System.out.print("Enter the command: ");
                message = in.nextLine();
                System.out.println();

                if(message.compareTo("EXIT") == 0)
                    break;

                byte[] buffer = message.getBytes();
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
                ds.send(request);
                buffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                ds.receive(response);
                String echo = new String(buffer, 0, response.getLength());
                System.out.println("Server response: " + echo);
                System.out.println();
            }
        }
        catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
