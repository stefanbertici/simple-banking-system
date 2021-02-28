package database;

import banking.Account;

import java.sql.*;

public class accountRepository {
    private Connection c;
    private Statement stmt;
    private ResultSet rs;
    private final String url;

    public accountRepository(String dbName) {
        this.url = "jdbc:sqlite:" + dbName;
        this.stmt = null;
    }

    public void connectToDatabase() {
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

    public void deleteTable() {
        try {
            stmt = c.createStatement();
            String sql = "DROP TABLE card;";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

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

    public void addToTable(Account account) {
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

    public boolean canLogIn(String inputNumber, String inputPin) {
        try {
            stmt = c.createStatement();
            String sql = "SELECT number, pin FROM card WHERE number = '" +
                    inputNumber + "' AND pin = '" +
                    inputPin + "';";
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

    public int getBalance(String inputNumber) {
        try {
            stmt = c.createStatement();
            String sql = "SELECT number, balance FROM card WHERE number = '" + inputNumber + "';";
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
}

