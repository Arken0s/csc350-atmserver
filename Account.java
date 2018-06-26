/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmserver;

/**
 *
 * @author jake
 */
public class Account {
    
    private int accountnum;
    private int pin;
    private int balance;

    public Account(int accountnum, int pin, int balance) {
        this.accountnum = accountnum;
        this.pin = pin;
        this.balance = balance;
    }

    public int getAccountnum() {
        return accountnum;
    }

    public int getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setAccountnum(int accountnum) {
        this.accountnum = accountnum;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    
}
