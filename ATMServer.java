package atmserver;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ATMServer {

    static public int destport = 4445;
    static public int bufsize = 512;
    static public final int timeout = 15000; // time in milliseconds

    static public void main(String args[]) {

        DatagramSocket s;               // UDP uses DatagramSockets

        try {
            s = new DatagramSocket(destport);
        } catch (SocketException se) {
            System.err.println("cannot create socket with port " + destport);
            return;
        }
        try {
            s.setSoTimeout(timeout);       // set timeout in milliseconds
        } catch (SocketException se) {
            System.err.println("socket exception: timeout not set!");
        }

        // create DatagramPacket object for receiving data:
        DatagramPacket msg = new DatagramPacket(new byte[bufsize], bufsize);

        Map<InetAddress, Integer> openSessions = new HashMap<>();

        while (true) { // read loop
            try {
                msg.setLength(bufsize);  // max received packet size
                s.receive(msg);          // the actual receive operation
                System.err.println("message from <"
                        + msg.getAddress().getHostAddress() + "," + msg.getPort() + ">");
                byte[] data = msg.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
//                InetAddress IPAddress = InetAddress.getByName("10.100.2.1");
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                Client atmMessage = (Client) is.readObject();
                System.out.println("Message received.");

                int requestType = atmMessage.getRequest();
                Integer value = openSessions.get(msg.getAddress());

                switch (requestType) {

                    case 0:
                        //Login
                        int count = ClientDatabase.accountList.size();
                        Boolean foundAccount = false;
                        for (int i = 0; i <= count; i++) {
                            if (ClientDatabase.accountList.get(i).getAccountnum() == atmMessage.getAccountNumber()) {
                                if (ClientDatabase.accountList.get(i).getPin() == atmMessage.getPin()) {
                                    openSessions.put(msg.getAddress(), atmMessage.getAccountNumber());
                                    requestResponse(5, -1, msg.getAddress());
                                    System.out.println("Login Successful");
                                    foundAccount = true;
                                    break;
                                } else {
                                    requestResponse(6, -1, msg.getAddress());
                                    System.out.println("Wrong PIN");
                                   
                                }
                            } 
                        }
                        if (foundAccount == false) {
                            System.out.println("Account Doesn't Exist");
                        }
                        break;

                    case 1:
                        //Balance
                        if (value != null) {
                            for (Account account : ClientDatabase.accountList) {
                                if (account.getAccountnum() == value) {
                                    System.out.println(account.getBalance());
                                }
                            }
                        } 
                        else {
                            System.out.println("Not Logged In!");
                        }
                            System.out.println("Balance");

                        
                        case 2:
                        if (value != null) {

                        } else {
                            System.out.println("Not Logged In!");
                        }
                        System.out.println("Withdrawl");

                    case 3:
                        if (value != null) {

                        } else {
                            System.out.println("Not Logged In!");
                        }
                        System.out.println("Deposit");

                    case 4:
                        if (value != null) {

                        } else {
                            System.out.println("Not Logged In!");
                        }
                        System.out.println("Logout");

                    default:
                        System.out.println("Error");
                }

                String str = new String(msg.getData(), 0, msg.getLength());
                        System.out.print(str);

                }catch (SocketTimeoutException ste) {    // receive() timed out
                System.err.println("Response timed out!");
            }catch (Exception ioe) {                // should never happen!
                System.err.println("Bad receive");
                ioe.printStackTrace();
            }

                // newline must be part of str
            }
            // s.close();
        } // end of main
    
        static public void requestResponse(int responseType, int responseAmount, InetAddress clientAddress) throws IOException, ClassNotFoundException {
            DatagramSocket Socket;
            Socket = new DatagramSocket();
            Client response = new Client(responseType, -1, -1, responseAmount);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // creates new byte output stream
            ObjectOutputStream os = new ObjectOutputStream(outputStream);          // creates new out stream
            os.writeObject(response); // writes new client to send message
            byte[] b = outputStream.toByteArray(); // writes bytes to array
            DatagramPacket msg = new DatagramPacket(b, b.length, clientAddress, 4445); // creates new datagram to send with coordinates
            Socket.send(msg); // sends message
            
        }
    /*
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // creates new byte output stream
            ObjectOutputStream os = new ObjectOutputStream(outputStream);          // creates new out stream
            os.writeObject(client); // writes new client to send message
                byte[] b = outputStream.toByteArray(); // writes bytes to array
                DatagramPacket msg = new DatagramPacket(b, b.length, IPAddress, 4445); // creates new datagram to send with coordinates
                Socket.send(msg); // sends message
    */
    }
