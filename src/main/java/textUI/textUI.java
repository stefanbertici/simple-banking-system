package textUI;

import banking.Account;
import database.accountRepository;

import java.util.Scanner;

public class textUI {
    private final accountRepository accRepo;

    public textUI(accountRepository accRepo) {
        this.accRepo = accRepo;
    }

    public void start() {
        this.accRepo.openConnectionToDatabase();
        this.accRepo.createTable();
        Scanner scanner = new Scanner(System.in);
        boolean mainMenu = true;
        String input;

        while (mainMenu) {
            System.out.println("\n1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            System.out.print(">");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    int nextId = this.accRepo.getLastId() + 1;
                    Account newAccount = new Account(nextId);
                    this.accRepo.addEntry(newAccount);
                    System.out.println("\nYour account has been created\n" +
                            "Your card number:\n" +
                            newAccount.getNumber() +
                            "\nYour pin:\n" +
                            newAccount.getPin());
                }
                case "2" -> {
                    System.out.print("\nEnter your card number:\n" + ">");
                    String myAccountNumber = scanner.nextLine();

                    System.out.print("Enter your PIN:\n" + ">");
                    String myAccountPin = scanner.nextLine();

                    if (accRepo.validateLogin(myAccountNumber, myAccountPin)) {
                        System.out.println("\nYou have successfully logged in!");
                        int myAccountId = accRepo.getAccountId(myAccountNumber);
                        boolean loggedIn = true;

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
                                case "1" -> System.out.println("\nBalance: " + this.accRepo.getBalance(myAccountId));

                                case "2" -> {
                                    System.out.println("\nEnter income:");
                                    System.out.print(">");
                                    int value = Integer.parseInt(scanner.nextLine());
                                    this.accRepo.addToBalance(myAccountId, value);
                                    System.out.println("Income was added!");
                                }
                                case "3" -> {
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
                                case "4" -> {
                                    accRepo.deleteEntry(myAccountId);
                                    System.out.println("\nThe account has been closed!");
                                    loggedIn = false;
                                }
                                case "5" -> {
                                    System.out.println("\nYou have successfully logged out!");
                                    loggedIn = false;
                                }
                                case "0" -> {
                                    System.out.println("\nBye!");
                                    accRepo.closeConnectionToDatabase();
                                    loggedIn = false;
                                    mainMenu = false;
                                }
                                default -> System.out.println("\nSomething went wrong in second (inner) menu!");
                            }
                        }
                    } else {
                        System.out.println("\nWrong card number or PIN!");
                    }
                }
                case "0" -> {
                    System.out.println("\nBye!");
                    this.accRepo.closeConnectionToDatabase();
                    mainMenu = false;
                }
                default -> System.out.println("\nSomething went wrong in main menu!");
            }
        }
    }

    //we check if given number passes Luhn's algorithm
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

