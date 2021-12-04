package banking;


import database.accountRepository;
import textUI.textUI;

public class Main {
    public static void main(String[] args) {
        accountRepository accRepo = new accountRepository(args[0]);
        textUI ui = new textUI(accRepo);

        ui.start();
    }
}
