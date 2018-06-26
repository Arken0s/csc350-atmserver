package atmserver;

import static atmserver.ATMServer.bufsize;
import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;

/* simplex-talk server, UDP version */
public class UDPSocketClient {

    DatagramSocket Socket;

    public UDPSocketClient() {
    }

    public void createAndListenSocket() throws IOException, ClassNotFoundException {

        try {
            Scanner input = new Scanner(System.in);
            int AccountNumber;
            int pin; // 4 numbers
            int Request;
            int amount;

            Socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");

            System.out.println("welcome! what would you like to do? (Please enter in integer units only)"
                    + "\n 0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit");
            while (true) {

                Request = input.nextInt();
                //0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit

                Client client = null; // initializes client

                switch (Request) {
                    case 0: // login command
                        System.out.println("enter account number");
                        AccountNumber = input.nextInt();
                        System.out.println("enter account pin");
                        pin = input.nextInt();
                        client = new Client(AccountNumber, pin);
                        break;

                    case 1: // 1 = balance
                        System.out.println("asking server for balance");
                        client = new Client(1, -1, -1, -1);
                        break;

                    case 2: // 2 = withdrawl
                        System.out.println("How much would you like to withdrawl?");
                        amount = input.nextInt();
                        System.out.println("Withdrawing $" + amount + " dollars.");
                        client = new Client(2, -1, -1, amount);
                        break;

                    case 3: // 3 = deposit
                        System.out.println("How much would you like to deposit?");
                        amount = input.nextInt();
                        System.out.println("Depositing $" + amount + "dollars into your account.");
                        client = new Client(3, -1, -1, amount);
                        break;

                    case 4: // 4 = exit
                        System.out.println("would you like to exit? (Y/N)");
                        if (input.next().toLowerCase() == "y") {
                            client = new Client(4, -1, -1, -1);
                            System.out.println("Goodbye!");
                            System.exit(4);
                        } else {
                            System.out.println("What would you like to do?"
                                    + "\n 0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit");
                        }
                        break;
                    default: // edge cases
                        System.out.println("invalid selection. Please try again.");
                        break;
                }
                //  send the client object
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // creates new byte output stream
                ObjectOutputStream os = new ObjectOutputStream(outputStream);          // creates new out stream
                os.writeObject(client); // writes new client to send message
                byte[] b = outputStream.toByteArray(); // writes bytes to array
                DatagramPacket msg = new DatagramPacket(b, b.length, IPAddress, 4445); // creates new datagram to send with coordinates
                Socket.send(msg); // sends message

                Socket.receive(msg);          // the actual receive operation
                System.err.println("message from <"
                        + msg.getAddress().getHostAddress() + "," + msg.getPort() + ">");
                byte[] data = msg.getData(); // puts bytes in array
                ByteArrayInputStream in = new ByteArrayInputStream(data); // places bytes into datastream to read
                ObjectInputStream is = new ObjectInputStream(in); // converts bytes into inputstream
                Client atmMessage = (Client) is.readObject(); // reads message
                System.out.println("Message received :: " + atmMessage); // prints message

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
