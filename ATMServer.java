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
                Client atmMessage = (Client) is.readObject();
                System.out.println("Message received.");

                int requestType = atmMessage.getRequest();
                Integer value = openSessions.get(msg.getAddress());

                switch (requestType) {

                    case 0: //Login
                        if (value != null) {
                            requestResponse(6, -1, msg.getAddress(), msg.getPort());
                            System.out.println("This address already has an account signed in!");
                            break;
                        }
                        else {
                            int count = ClientDatabase.accountList.size();
                            Boolean foundAccount = false;
                            for (int i = 0; i <= count; i++) {
                                if (ClientDatabase.accountList.get(i).getAccountnum() == atmMessage.getAccountNumber()) {
                                    if (ClientDatabase.accountList.get(i).getPin() == atmMessage.getPin()) {
                                        openSessions.put(msg.getAddress(), atmMessage.getAccountNumber());
                                        requestResponse(5, -1, msg.getAddress(), msg.getPort());
                                        System.out.println(msg.getAddress() + " has successfully signed in with account #" + atmMessage.getAccountNumber() + ".");
                                        System.out.println(msg.getPort());
                                        foundAccount = true;
                                        break;
                                    } else {
                                        requestResponse(6, -1, msg.getAddress(), msg.getPort());
                                        System.out.println("Wrong PIN for account #" + atmMessage.getAccountNumber() + ".");
                                        break;
                                    }
                                }
                            }
                            if (foundAccount == false) {
                                requestResponse(6, -1, msg.getAddress(), msg.getPort());
                                System.out.println("The account number #" + atmMessage.getAccountNumber() + " does not exist!");
                                break;
                            }
                            break;
                        }

                    case 1: //Balance
                        if (value != null) {
                            for (Account account : ClientDatabase.accountList) {
                                if (account.getAccountnum() == value) {
                                    requestResponse(5, account.getBalance(), msg.getAddress(), msg.getPort());
                                    System.out.println("Account #" + value + "'s balance is " + account.getBalance());
                                    break;
                                }
                            }
                         
                        } else {
                            requestResponse(6, -1, msg.getAddress(), msg.getPort());
                            System.out.println("Not Logged In!");
                            
                        }
                        break;
                    case 2: //Withdrawl
                        if (value != null) {
                            for (Account account : ClientDatabase.accountList) {
                                if (account.getAccountnum() == value) {
                                    if (account.getBalance() > atmMessage.getAmount()) {
                                        account.setBalance((account.getBalance() - atmMessage.getAmount()));
                                        requestResponse(5, account.getBalance(), msg.getAddress(), msg.getPort());
                                        System.out.println("After the withdrawal, account #" + value + "'s balance is " + account.getBalance());
                                        break;
                                    }
                                    else {
                                        requestResponse(6, -1, msg.getAddress(), msg.getPort());
                                        System.out.println("Not enough funds for this withdrawal!");
                                        break;
                                    }
                                }
                                else {
                                    System.out.println("Account not found!");
                                }
                            }
                            break;
                        } else {
                            requestResponse(6, -1, msg.getAddress(), msg.getPort());
                            System.out.println("Not Logged In!");
                            break;
                        }
                    case 3: //Deposit
                        if (value != null) {
                            for (Account account : ClientDatabase.accountList) {
                                if (account.getAccountnum() == value) {
                                    account.setBalance((account.getBalance() + atmMessage.getAmount()));
                                    requestResponse(5, account.getBalance(), msg.getAddress(), msg.getPort());
                                    System.out.println("After the deposit, account #" + value + "'s balance is " + account.getBalance());
                                    break;
                                }
                                else {
                                    requestResponse(6, -1, msg.getAddress(), msg.getPort());
                                    System.out.println("Account not found!");
                                    break;
                                }
                            }
                            break;
                        } 
                        else {
                            requestResponse(6, -1, msg.getAddress(), msg.getPort());
                            System.out.println("Not Logged In!");
                            break;
                        }

                    case 4: //Logout
                        if (value != null) {
                            openSessions.remove(msg.getAddress());
                            requestResponse(5, -1, msg.getAddress(), msg.getPort());
                            System.out.println("Account #" + value + "(" + msg.getAddress() + ") has logged out of their session!");
                            break;
                        } else {
                            requestResponse(6, -1, msg.getAddress(), msg.getPort());
                            System.out.println("Not Logged In!");
                            break;
                        }

                    default:
                        requestResponse(6, -1, msg.getAddress(), msg.getPort());
                        System.out.println("Error. Request not valid or packet is not recognized.");
                        break;
                }
                
                value = null;
                
            } catch (SocketTimeoutException ste) {    // receive() timed out
                System.err.println("Response timed out!");
            } catch (Exception ioe) {                // should never happen!
                System.err.println("Bad receive");
                ioe.printStackTrace();
            }

            // newline must be part of str
        }
        // s.close();
    } // end of main

    static public void requestResponse(int responseType, int responseAmount, InetAddress clientAddress, int clientPort) throws IOException, ClassNotFoundException {
        DatagramSocket Socket;
        Socket = new DatagramSocket();
        Client response = new Client(responseType, -1, -1, responseAmount);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // creates new byte output stream
        ObjectOutputStream os = new ObjectOutputStream(outputStream);          // creates new out stream
        os.writeObject(response); // writes new client to send message
        byte[] b = outputStream.toByteArray(); // writes bytes to array
        DatagramPacket msg = new DatagramPacket(b, b.length, clientAddress, clientPort); // creates new datagram to send with coordinates
        Socket.send(msg); // sends message

    }
}
