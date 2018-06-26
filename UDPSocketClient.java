package atmserver;

import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;


public class UDPSocketClient  extends Client {


    DatagramSocket Socket;

    public UDPSocketClient() {
    }

    public void createAndListenSocket() throws IOException, ClassNotFoundException {

        try {
            Scanner input = new Scanner(System.in);
            Socket = new DatagramSocket();

            InetAddress IPAddress = InetAddress.getByName("localhost");
            // IP address of server destination. 

            System.out.println("welcome! what would you like to do? (Please enter in integer units only)"
                    + "\n 0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit");
            while (true) {

                Request = input.nextInt();
                //0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit

                Client client = new Client(); // initializes client

                switch (Request) {
                    case 0: // login command
                        client.setS_Request("Login"); 
                        System.out.println("enter account number");
                        AccountNumber = input.nextInt();
                        System.out.println("enter account pin");
                        pin = input.nextInt();
                        client = new Client(AccountNumber, pin);
                        System.out.println(client.toString());
                        break;

                    case 1: // 1 = balance
                        client.setS_Request("Balance"); 
                        System.out.println("asking server for balance");
                        client = new Client(1, -1, -1, -1);
                        System.out.println(client.toString());
                        break;

                    case 2: // 2 = withdrawl
                        client.setS_Request("Withdrawl"); 
                        System.out.println("How much would you like to withdrawl?");
                        amount = input.nextInt();
                        System.out.println("Withdrawing $" + amount + " dollars.");
                        client = new Client(2, -1, -1, amount);
                        System.out.println(client.toString());
                        break;

                    case 3: // 3 = deposit
                        client.setS_Request("Deposit"); 
                        System.out.println("How much would you like to deposit?");
                        amount = input.nextInt();
                        System.out.println("Requesting  $" + amount + "dollars to be deposited into your account.");
                        client = new Client(3, -1, -1, amount);
                        System.out.println(client.toString());
                        break;

                    case 4: // 4 = exit
                        client.setS_Request("Exit"); 
                        client = new Client(4, -1, -1, -1);
                        System.out.println("Goodbye!");
                        System.out.println(client.toString());
                        break;

                    default: // edge cases
                        System.out.println("invalid selection. Please try again.");
                        break;
                }
                
                /* below is to send the client object*/
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
               
                os.writeObject(client);
            

                byte[] b = outputStream.toByteArray();
             

                DatagramPacket msg = new DatagramPacket(b, b.length, IPAddress, 4445);
       
                Socket.send(msg);

                Socket.receive(msg);
                System.err.println("message from <"
                        + msg.getAddress().getHostAddress() + "," + msg.getPort() + ">");

                byte[] data = msg.getData();
               
                ByteArrayInputStream in = new ByteArrayInputStream(data);
   

                ObjectInputStream is = new ObjectInputStream(in);
             

                
                Client atmMessage = (Client) is.readObject(); // reads message
                System.out.println("Message received "); // prints message
                switch (atmMessage.getRequest()) {
                    case 5:
                        if(Request == 0){ 
                            System.out.println("Sucessfully logged in. " + " \n0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit ");
                        }
                        else if (Request == 1){ // balance check
                            System.out.println("Sucessful: Account balance is " + atmMessage.getAmount() + ".");
                            atmMessage.setBalance(atmMessage.getAmount());
                        }else if ( Request == 2){ // withdrawl
                            System.out.println("Sucessful: withdrawing " + amount + ". Account balance is " + atmMessage.getAmount() + ".");
                            atmMessage.setBalance(atmMessage.getAmount());
                        } else if (Request == 3) { // deposit
                            System.out.println("Sucessful: depositing " + amount + ". Account balance is " + atmMessage.getAmount() + ".");
                            atmMessage.setBalance(atmMessage.getAmount());
                        }else{ System.out.println("Sucessfully.... broke the server? [PARADOX] ( Line144) ");  }
                        break;
                        
                    case 6:
                        System.out.println("There was an error with your Request. Please try again!");
                        break;
//                    case 4: // unknown if direction is to recieve response from server. 
//                        System.out.println("Disconnected from Server");
//                        System.exit(4); 
                    default:
                        System.out.println("How did you manage to get here?? [Line154]");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
