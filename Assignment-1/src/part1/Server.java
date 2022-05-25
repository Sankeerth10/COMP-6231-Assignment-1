package part1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * Server using UDP socket to send echo response to a client.
 *
 *
 * @author
 */
public class Server {
    private DatagramSocket socket;
    private List<String> listQuotes = new ArrayList<String>();
    private HashMap<String, Integer> clientList = new HashMap<String, Integer>();
    private HashMap<String, List<Integer>> repoDict = new HashMap<>();

    public Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java EchoServer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);

        try {
            Server server = new Server(port);
            server.protocol();
        }
        catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        }
        catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private void protocol() throws IOException {
        while(true){
            byte[] buffer = new byte[1024];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);


            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();

            if (clientList.containsKey(clientPort + ""))
            {

                int firstNum = clientList.get(clientPort + "");

                String firstNumber = (firstNum + "").replaceAll("[^0-9]", "");
                int firstNumberInteger = Integer.parseInt(firstNumber);

                String secNum = new String(buffer, StandardCharsets.UTF_8);
                String secNumber = secNum.replaceAll("[^0-9]", "");
                int secNumberInteger = Integer.parseInt(secNumber);

                int sum = firstNumberInteger + secNumberInteger;

                buffer = (sum + "").getBytes(StandardCharsets.UTF_8);

                clientPort = request.getPort();
                DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(response);

                clientList.clear();
            }
            else
            {
                String command = new String(buffer, StandardCharsets.UTF_8);
                String[] lst = command.split(" ");

                String lastitem = lst[lst.length -1].replaceAll("[^0-9][^A-Z][^a-z]", "");
                lst[lst.length -1] = lastitem;

                for (String i:lst)
                System.out.println(i);

                String key = "";
                int value = 0;
                String msg = "";
                String number = "";

                switch(lst[0])
                {
                    case "ADD":
                        key = lst[1];
                        value = Integer.parseInt(lst[2]);
                        msg = add(key, value);
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "UPDATE":
                        key = lst[1];
                        value = Integer.parseInt(lst[2]);
                        msg = update(key, value);
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "DELETE":
                        key = lst[1];
                        msg = delete(key);
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "LIST_KEYS":
                        msg = listKeys();
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "GET_VALUE":
                        key = lst[1];
                        msg = getValue(key);
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "GET_VALUES":
                        key = lst[1];
                        msg = getValues(key);
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "SUM":
                        key = lst[1];
                        msg = sum(key);
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "AVG":
                        key = lst[1];
                        msg = avg(key);
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    case "RESET":
                        msg = reset();
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                        break;
                    default:
                        msg = "Command not found";
                        buffer = msg.getBytes(StandardCharsets.UTF_8);
                }

                clientPort = request.getPort();
                DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(response);
            }


        }
    }

    private String add(String k, Integer v)
    {
        List<Integer> lst;
        if(repoDict.containsKey(k)) {
            lst = repoDict.get(k);
            lst.add(v);
        }
        else {
            lst = new ArrayList<>();
            lst.add(v);
        }

        repoDict.put(k, lst);

        return "Successfully added";
    }

    private String update(String k, Integer v)
    {
        List<Integer> lst;
        String msg = "";
        if(repoDict.containsKey(k)) {
            lst = new ArrayList<>();
            lst.add(v);
            repoDict.put(k, lst);
            msg = "Successfully added";
        }
        else {
            msg = "Key does not exist";
        }

        return msg;
    }

    private String delete(String k)
    {
        String msg = "";
        if(repoDict.containsKey(k)) {
            repoDict.remove(k);
            msg = "Successfully removed";
        }
        else {
            msg = "Key does not exist";
        }

        return msg;
    }

    private String listKeys()
    {
        List<String> lst;
        String msg = "Keys:";

        if(!repoDict.isEmpty()) {
            lst = (List<String>) repoDict.keySet();

            for(String i:lst)
            {
                msg = msg + " " + i;
            }
        }
        else {
            msg = "Key does not exist";
        }

        return msg;
    }

    private String getValue(String k)
    {
        List<Integer> lst;
        int value;
        String msg = "";
        if(repoDict.containsKey(k)) {
            lst = repoDict.get(k);
            value = lst.get(0);
            msg = "" + value;
        }
        else {
            msg = "Key does not exist";
        }

        return msg;
    }

    private String getValues(String k) {
        List<Integer> lst;
        String msg = "Values:";
        if(repoDict.containsKey(k)) {
            lst = repoDict.get(k);

            for (int i:lst) {
                msg = msg + " " + i;
            }
        }
        else {
            msg = "Key does not exist";
        }

        return msg;
    }

    private String sum(String k) {
        List<Integer> lst;
        int sum = 0;
        String msg = "";
        if (repoDict.containsKey(k)) {
            lst = repoDict.get(k);
            for(int i:lst)
                sum += i;
            msg = "" + sum;
        } else {
            msg = "Key does not exist";
        }
        return msg;
    }

    private String avg(String k) {
        List<Integer> lst;
        int sum = 0;
        double avg = 0.0;
        String msg = "";
        if (repoDict.containsKey(k)) {
            lst = repoDict.get(k);

            for(int i:lst)
            {
                sum += i;
            }

            avg = sum / lst.size();
            msg = "" + avg;
        } else {
            msg = "Key does not exist";
        }
        return msg;
    }

    private String reset() {
        String msg;
        repoDict.clear();
        msg = "Reset successfully";
        return msg;
    }

}