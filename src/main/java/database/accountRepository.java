package database;

import banking.Account;

import java.sql.*;

public class accountRepository {
    private Connection c;
    private Statement stmt;
    private ResultSet rs;
    private final String url;

    //SQLite db (.txt file) name is passed on to the repository
    //from the command line arguments of the Main class
    public accountRepository(String dbName) {
        this.url = "jdbc:sqlite:" + dbName;
        this.stmt = null;
    }

    public void openConnectionToDatabase() {
        try {
            c = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void closeConnectionToDatabase() {
        try {
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void createTable() {
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS card " +
                    "(id      INT," +
                    " number  TEXT," +
                    " pin     TEXT," +
                    " balance INTEGER DEFAULT 0);";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    /*FOR EMERGENCIES ONLY
    public void deleteTable() {
        try {
            stmt = c.createStatement();
            String sql = "DROP TABLE card;";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }*/

    public int getLastId() {
        int lastId = -1;

        try {
            stmt = c.createStatement();
            String sql = "SELECT MAX(id) AS id FROM card;";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lastId = rs.getInt("id");
            }

            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }

        return lastId;
    }

    public int getAccountId(String inputNumber) {
        int accountId = -1;

        try {
            stmt = c.createStatement();
            String sql = "SELECT id FROM card WHERE number = '" +
                    inputNumber + "';";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                accountId = rs.getInt("id");
            }

            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }

        return accountId;
    }

    public void addEntry(Account account) {
        try {
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = "INSERT INTO card (id, number, pin, balance) " + "VALUES (" +
                    account.getId() + ", " +
                    "'" + account.getNumber() + "', " +
                    "'" + account.getPin() + "', " +
                    "0);";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void deleteEntry(int myAccountId) {
        try {
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = "DELETE FROM card WHERE id = " + myAccountId + ";";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public boolean validateLogin(String myAccountNumber, String myAccountPin) {
        try {
            stmt = c.createStatement();
            String sql = "SELECT number, pin FROM card WHERE number = '" +
                    myAccountNumber + "' AND pin = '" +
                    myAccountPin + "';";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return true;
            }

            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }

        return false;
    }

    public boolean accountExists(String accountNumber) {
        try {
            stmt = c.createStatement();
            String sql = "SELECT number FROM card WHERE number = '" +
                    accountNumber + "';";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return true;
            }

            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }

        return false;
    }

    public int getBalance(int accountId) {
        try {
            stmt = c.createStatement();
            String sql = "SELECT balance FROM card WHERE id = " + accountId + ";";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("balance");
            }

            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }

        return -1;
    }

    public void addToBalance(int accountId, int value) {
        try {
            c.setAutoCommit(false);
            stmt = c.createStatement();
            int newBalance = this.getBalance(accountId) + value;
            String sql = "UPDATE card SET balance = " + newBalance + " WHERE id = " + accountId + ";";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void transferMoney(int senderId, int receiverId, int value) {
        try {
            c.setAutoCommit(false);
            stmt = c.createStatement();
            int senderBalance = this.getBalance(senderId) - value;
            String sql = "UPDATE card SET balance = " + senderBalance + " WHERE id = " + senderId + ";";
            stmt.executeUpdate(sql);

            int receiverBalance = this.getBalance(receiverId) + value;
            sql = "UPDATE card SET balance = " + receiverBalance + " WHERE id = " + receiverId + ";";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}

