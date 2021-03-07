package textUI;

import banking.Account;
import database.accountRepository;

import java.util.Scanner;

public class textUI {
    private final accountRepository accRepo;
    private boolean running;
    private boolean loggedIn;

    public textUI(accountRepository accRepo) {
        this.accRepo = accRepo;
        this.running = true;
        this.loggedIn = false;
    }

    public void start() {
        //we open the connection to the database and create the table (if it does not already exist)
        //at the start of the program
        accRepo.openConnectionToDatabase();
        accRepo.createTable();
        Scanner scanner = new Scanner(System.in);
        String input;

        while (running) {
            System.out.println("\n1. Create an account\n" +
                                 "2. Log into account\n" +
                                 "0. Exit");
            System.out.print(">");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> createNewAccount();
                case "2" -> login();
                case "0" -> exit();
                default -> System.out.println("\nSomething went wrong in main menu!");
            }
        }
    }

    private void createNewAccount() {
        int nextId = accRepo.getLastId() + 1;
        Account newAccount = new Account(nextId);

        accRepo.addEntry(newAccount);
        System.out.println("\nYour account has been created\n" +
                            "Your card number:\n" + newAccount.getNumber() +
                            "\nYour pin:\n" + newAccount.getPin());
    }

    private void login() {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.print("\nEnter your card number:\n" + ">");
        String myAccountNumber = scanner.nextLine();
        System.out.print("Enter your PIN:\n" + ">");
        String myAccountPin = scanner.nextLine();

        if (accRepo.validateLogin(myAccountNumber, myAccountPin)) {
            System.out.println("\nYou have successfully logged in!");
            int myAccountId = accRepo.getAccountId(myAccountNumber);
            loggedIn = true;

            while (loggedIn) {
                System.out.println("\n1. Balance\n" +
                                    "2. Add income\n" +
                                    "3. Do transfer\n" +
                                    "4. Close account\n" +
                                    "5. Log out\n" +
                                    "0. Exit");
                System.out.print(">");
                input = scanner.nextLine();

                switch (input) {
                    case "1" -> getBalance(myAccountId);
                    case "2" -> addIncome(myAccountId);
                    case "3" -> transfer(myAccountNumber, myAccountId);
                    case "4" -> closeAccount(myAccountId);
                    case "5" -> logOut();
                    case "0" -> exit();
                    default -> System.out.println("\nSomething went wrong in second (inner) menu!");
                }
            }
        } else {
            System.out.println("\nWrong card number or PIN!");
        }
    }

    private void exit() {
        loggedIn = false;
        running = false;
        System.out.println("\nBye!");
        accRepo.closeConnectionToDatabase();
    }

    private void getBalance(int myAccountId) {
        System.out.println("\nBalance: " + accRepo.getBalance(myAccountId));
    }

    private void addIncome(int myAccountId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter income:");
        System.out.print(">");
        int value = Integer.parseInt(scanner.nextLine());
        accRepo.addToBalance(myAccountId, value);
        System.out.println("Income was added!");
    }

    private void transfer(String myAccountNumber, int myAccountId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nTransfer");
        System.out.println("Enter card number:");
        System.out.print(">");
        String receiverAccountNumber = scanner.nextLine();

        if (receiverAccountNumber.equals(myAccountNumber)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!this.validateAccountNumber(receiverAccountNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (this.validateAccountNumber(receiverAccountNumber) && !accRepo.accountExists(receiverAccountNumber)) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            System.out.print(">");
            int value = Integer.parseInt(scanner.nextLine());

            if (accRepo.getBalance(myAccountId) >= value) {
                int receiverId = accRepo.getAccountId(receiverAccountNumber);
                accRepo.transferMoney(myAccountId, receiverId, value);
                System.out.println("Success!");
            } else {
                System.out.println("Not enough money!");
            }
        }
    }

    private void closeAccount(int myAccountId) {
        accRepo.deleteEntry(myAccountId);
        System.out.println("\nThe account has been closed!");
        loggedIn = false;
    }

    private void logOut() {
        System.out.println("\nYou have successfully logged out!");
        loggedIn = false;
    }

    //we check if the given number passes the check for Luhn's algorithm
    private boolean validateAccountNumber(String accountNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = accountNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(accountNumber.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}

