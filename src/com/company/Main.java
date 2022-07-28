package com.company;
//import java.io.Console;
import java.util.Scanner;
//import java.io.*;
import java.sql.*;


class Db<con> {
    //Creating a connection

    String url="jdbc:mysql://localhost:3000/atm";
    String username="puffy";
    String password="puffy";
    public Connection con;

    {
        try {
            con = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean userPresent(String id){
        try{
            String query="select * from atm.users where userid='"+id+"';";
            Statement stmt= con.createStatement();
            ResultSet set= stmt.executeQuery(query);
            int i=0;
            while (set.next()){
                i++;
            }
            if(i==1){
                System.out.println("ID EXIST");
                return true;
            }else{
                System.out.println("ID DOES NOT EXIST");

            }
            con.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean userPresent(String id,String pin){
        try{
            String query="select * from atm.users where userid='"+id+"' and pin='"+pin+"';";
            Statement stmt= con.createStatement();
            ResultSet set= stmt.executeQuery(query);
            int i=0;
            while (set.next()){
                i++;
            }
            if(i==1){
                System.out.println("User identified");
                return true;

            }else{
                System.out.println("Access denied");

            }
            con.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void showTransactionHistory(String id)  {
        String query="select transactionDate,transactionType,amount " +
                "from atm.transactionhistory  where userid = '"+id+"'limit 10;";
        try {
            Statement stmt =con.createStatement();
            ResultSet set =stmt.executeQuery(query);
            while(set.next()){
                Timestamp dateAndTime=set.getTimestamp(1);
                String transactionType=set.getString(2);
                int amount=set.getInt(3);
                System.out.println(dateAndTime+"  "+amount+"  "+transactionType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int userBalance(String id) {
        String query="select totalbalance from atm.users u " +
                "where userid = '"+id+"';";
        int amount = 0;
        try {
            Statement stmt= con.createStatement();
            ResultSet set= stmt.executeQuery(query);
            while (set.next()){
               amount=set.getInt(1); 
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return amount;
    }

    public void insertIntoAtmTransactionHistory(String id, String transactionType, int i) {
        String query ="insert into atm.transactionhistory values" +
                "('"+id+"',now(),'"+transactionType+"',"+i+");";
        try {
           Statement stmt= con.createStatement();

            stmt.executeUpdate(query);


//            System.out.println("inserted in transaction history");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTotalBalance(String id, int i) {
        String query="update atm.users set totalbalance ="+i+" where userid = '"+id+"';";
        try {
            Statement stmt= con.createStatement();

            stmt.executeUpdate(query);
//            System.out.println("total balance of the user has been updated");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Withdraw{
    public String id;
    Withdraw(String id){
        System.out.println("How much money you want to withdraw?");
        this.id=id;
        int i= new Scanner(System.in).nextInt();
        if(check(i)){
            Db d4= new Db();
            d4.updateTotalBalance(id,(getUserBalance()-i));
            Db d5= new Db();
            d5.insertIntoAtmTransactionHistory(id,"withdraw",i);
            System.out.println("Your money has been withdrawled");

            System.out.println("Your current balance is--> "+getUserBalance());
        }else{
            System.out.println("Your given amount is more than your account balance!!!");

            System.out.println("You cannot withdraw money");
        }
    }
    public boolean check(int i){
        return i<=getUserBalance();
    }
    public int getUserBalance(){
        Db d3= new Db();
        return d3.userBalance(this.id);
    }
}

class  Deposit{
    public String id;
    Deposit(String id){
        this.id=id;
        System.out.println("Enter the amount you want to deposit");
        int i= new Scanner(System.in).nextInt();

        Db d4= new Db();
        d4.updateTotalBalance(id,(getUserBalance()+i));
        Db d5= new Db();
        d5.insertIntoAtmTransactionHistory(id,"deposit",i);
        System.out.println("Your money has been deposited");

        System.out.println("Your current balance is-->"+getUserBalance());
    }
    public int getUserBalance(){
        Db d3= new Db();
        return d3.userBalance(this.id);
    }
}

class Transfer {
    public String id;
     Transfer(String id){
        this.id=id;
         System.out.println("To which userID you want to transfer your money --");
         String toUserId=new Scanner(System.in).next();
         Db d8= new Db();
         if(d8.userPresent(toUserId)){
             System.out.println("How much money you want to transfer?");
             int i=new Scanner(System.in).nextInt();

             if(check(i)){
                 Db d4= new Db();
                 d4.updateTotalBalance(id,(getUserBalance()-i));
                 Db d5= new Db();
                 d5.insertIntoAtmTransactionHistory(id,("transferTo-"+toUserId),i);

                 Db d6= new Db();
                 d6.updateTotalBalance(toUserId,(getUserBalance()+i));
                 Db d7= new Db();
                 d7.insertIntoAtmTransactionHistory(toUserId,("transferFrom-"+id),i);

                 System.out.println("Your money has been Transfered");
                 System.out.println("Your current balance is-->"+getUserBalance());
             }else{
                 System.out.println("Your given amount is more than your account balance!!!");

                 System.out.println("You cannot transfer money");
             }
         }

    }
    public boolean check(int i){
        return i<=getUserBalance();
    }
    public int getUserBalance(){
        Db d3= new Db();
        return d3.userBalance(this.id);
    }
}


class  User{
    private String id;
    private String pass;
    public  void menu(){
        int i;
        System.out.println("WELCOME "+ this.id);

        do{
            System.out.println("ENTER 1: TRANSACTION HISTORY");
            System.out.println("ENTER 2: WITHDRAW");
            System.out.println("ENTER 3: DEPOSIT");
            System.out.println("ENTER 4: TRANSFER");
            System.out.println("ENTER 5: QUIT");
            switch (i=new Scanner(System.in).nextInt()){
                case 1->{
                    Db t1= new Db();
                    System.out.println("transaction history for our user");
                    t1.showTransactionHistory(this.id);
                    System.out.println("Your current balance id-->  "+t1.userBalance(this.id));
                }
                case 2->{
                    Withdraw w1= new Withdraw(this.id);
                }
                case 3->{
                    Deposit d1=new Deposit(this.id);
                }
                case 4->{
                    Transfer t1=new Transfer(this.id);
                }

            }
        }while(i!=5);


    }
    User(){
        Scanner sc = new Scanner(System.in);
//        Console cn=System.console();
//        if(cn==null){
//            System.out.println("No console available");
//            return;
//        }
//        readline
//        String str= cn.readLine("Enter UserID: ");
//        ReadPassword into characater array
//        char[] ch=cn.readPassword("Enter Password: ");

        //orignal code
        System.out.println("enter UserID: ");
        this.id=sc.next();
//        this.id=str;

        System.out.println("enter pass: ");
        this.pass=sc.next();
//        this.pass=new String(ch);

        Db d1=new Db();
        if(d1.userPresent(this.id,this.pass)){
                menu();
        }else{
            System.out.println("either your userID or pin is wrong please try again ");
        }
     }

}

public class Main  {

    public static void main(String[] args) {
	// write your code here
//        Console cn=System.console();
//        if(cn==null){
//            System.out.println("No console available");
//            return;
//        }
//        //readline
//        String str= cn.readLine("Enter UserID: ");
//        //ReadPassword into characater array
//        char[] ch=cn.readPassword("Enter Password: ");

            User u = new User();

    }
}
