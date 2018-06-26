/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmserver;

import java.util.ArrayList;

/**
 *
 * @author jake
 */
public class ClientDatabase {

    //Creating user-defined class objects  
    static Account a1 = new Account(000000000, 0000, 0);  //this one for testing
    static Account a2 = new Account(102102102, 1632, 1000);
    static Account a3 = new Account(103103103, 3486, 1234);
    static Account a4 = new Account(104104104, 9864, 420);
    static Account a5 = new Account(105105105, 3485, 69);
    static Account a6 = new Account(106106106, 6932, 1337);
    static Account a7 = new Account(107107107, 2449, 1000000);
    static Account a8 = new Account(108108108, 9402, 0);
    static Account a9 = new Account(109109109, 4023, 500);
    static Account a10 = new Account(110110110, 4435, 666);
    //creating arraylist  
    static ArrayList<Account> accountList = new ArrayList<>();

    static {
        accountList.add(a1); 
        accountList.add(a2);
        accountList.add(a3);
        accountList.add(a4);
        accountList.add(a5);
        accountList.add(a6);
        accountList.add(a7);
        accountList.add(a8);
        accountList.add(a9);
        accountList.add(a10);
    }

}
