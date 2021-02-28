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
        this.accRepo.connectToDatabase();
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
                    this.accRepo.addToTable(newAccount);
                    System.out.println("\nYour account has been created\n" +
                            "Your card number:\n" +
                            newAccount.getNumber() +
                            "\nYour pin:\n" +
                            newAccount.getPin());
                }
                case "2" -> {
                    System.out.print("\nEnter your card number:\n" + ">");
                    String inputNumber = scanner.nextLine();
                    System.out.print("Enter your PIN:\n" + ">");
                    String inputPin = scanner.nextLine();
                    if (this.accRepo.canLogIn(inputNumber, inputPin)) {
                        System.out.println("\nYou have successfully logged in!");
                        boolean loggedIn = true;

                        while (loggedIn) {
                            System.out.println("\n1. Balance\n" +
                                    "2. Log out\n" +
                                    "0. Exit");

                            System.out.print(">");
                            input = scanner.nextLine();

                            switch (input) {
                                case "1" -> System.out.println("\nBalance: " + this.accRepo.getBalance(inputNumber));
                                case "2" -> {
                                    System.out.println("\nYou have successfully logged out!");
                                    loggedIn = false;
                                }
                                case "0" -> {
                                    System.out.println("\nBye!");
                                    this.accRepo.closeConnectionToDatabase();
                                    loggedIn = false;
                                    mainMenu = false;
                                }
                                default -> System.out.println("\nSomething went wrong in logged in!");
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
}

