package atmserver;

import java.io.IOException;
import java.io.Serializable;

public class Client implements Serializable {

    private final static long serialVersionUID = 1;

    private int AccountNumber; // 9 numbers
    private int pin; // 4 numbers
    private int Request;
    private int amount;

    public Client() {
    }

    public Client(int AccountNumber, int pin) {
        this(0, AccountNumber, pin, -1);
    }

    public Client(int Request, int AccountNumber, int pin, int amount) {
        this.AccountNumber = AccountNumber;
        this.pin = pin;
        this.Request = Request;
        this.amount = amount;
    }

    public void setAccountNumber(int AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getAccountNumber() {
        return AccountNumber;
    }

    public int getPin() {
        return pin;
    }

    public int getRequest() {
        return Request;
    }

    public int getAmount() {
        return amount;
    }

    public void setRequest(int Request) {
        this.Request = Request;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Client{" + "AccountNumber=" + AccountNumber + ", pin=" + pin + ", Request=" + Request + ", amount=" + amount;
    }

//0 = login,  1 = balance , 2 = withdrawl, 3 = deposit , 4 exit
//test account : 000000000, pin 0000
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        UDPSocketClient udp = new UDPSocketClient();
        udp.createAndListenSocket();

    }

}