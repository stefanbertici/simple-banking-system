package database;

import banking.Account;

import java.sql.*;

public class accountRepository {
    private final String url;
    private Connection c;
    private Statement stmt;
    private PreparedStatement pstmt;
    private ResultSet rs;

    //SQLite db (.txt file) name is passed on to the repository
    //from the command line arguments of the Main class
    public accountRepository(String dbName) {
        this.url = "jdbc:sqlite:" + dbName;
        this.c = null;
        this.stmt = null;
        this.pstmt = null;
        this.rs = null;
    }

    public void openConnectionToDatabase() {
        try {
            c = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("\n* Connection to database has been successfully opened! *");
    }

    public void closeConnectionToDatabase() {
        try {
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.out.println("\n* Connection to database has been successfully closed! *");
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS card " +
                "(id      INT," +
                " number  TEXT," +
                " pin     TEXT," +
                " balance INTEGER DEFAULT 0);";

        try {
            stmt = c.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    /*FOR EMERGENCIES ONLY
    public void deleteTable() {
        String sql = "DROP TABLE card;";

        try {
            stmt = c.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }*/

    public int getLastId() {
        String query = "SELECT MAX(id) AS id FROM card;";
        int lastId = -1;

        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                lastId = rs.getInt("id");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                rs.close();
                stmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        return lastId;
    }

    /*From this point on we will use inputs in our queries.
    We'll provide those inputs via PreparedStatement’s setter methods.
    That way the values received will be treated as only data and no SQL Injection will happen.*/
    public int getAccountId(String accountNumber) {
        String query = "SELECT id FROM card WHERE number = ?;";
        int accountId = -1;

        try {
            pstmt = c.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt("id");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        return accountId;
    }

    public void addEntry(Account account) {
        String query = "INSERT INTO card (id, number, pin, balance) "
                + "VALUES (?, ?, ?, 0);";

        try {
            pstmt = c.prepareStatement(query);
            pstmt.setInt(1, account.getId());
            pstmt.setString(2, account.getNumber());
            pstmt.setString(3, account.getPin());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public void deleteEntry(int myAccountId) {
        String query = "DELETE FROM card WHERE id = ?;";

        try {
            pstmt = c.prepareStatement(query);
            pstmt.setInt(1, myAccountId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public boolean validateLogin(String myAccountNumber, String myAccountPin) {
        String query = "SELECT * FROM card WHERE number = ? AND pin = ?;";
        boolean canLogin = false;

        try {
            pstmt = c.prepareStatement(query);
            pstmt.setString(1, myAccountNumber);
            pstmt.setString(2, myAccountPin);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                canLogin = true;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        return canLogin;
    }

    public boolean accountExists(String accountNumber) {
        String query = "SELECT number FROM card WHERE number = ?;";
        boolean doesExist = false;

        try {
            pstmt = c.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                doesExist = true;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        return doesExist;
    }

    public int getBalance(int accountId) {
        String query = "SELECT balance FROM card WHERE id = ?;";
        int balance = -1;

        try {
            pstmt = c.prepareStatement(query);
            pstmt.setInt(1, accountId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                balance = rs.getInt("balance");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        return balance;
    }

    public void addToBalance(int accountId, int value) {
        int newBalance = this.getBalance(accountId) + value;
        String query = "UPDATE card SET balance = ? WHERE id = ?;";

        try {
            pstmt = c.prepareStatement(query);
            pstmt.setInt(1, newBalance);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public void transferMoney(int senderId, int receiverId, int value) {
        int senderNewBalance = this.getBalance(senderId) - value;
        int receiverNewBalance = this.getBalance(receiverId) + value;
        String query = "UPDATE card SET balance = ? WHERE id = ?";

        try {
            pstmt = c.prepareStatement(query);
            //update sender account balance
            pstmt.setInt(1, senderNewBalance);
            pstmt.setInt(2, senderId);
            pstmt.executeUpdate();
            //update receiver account balance
            pstmt.setInt(1, receiverNewBalance);
            pstmt.setInt(2, receiverId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
}

