import java.sql.*;
import java.util.Scanner;

public class CustomerOperations {

    public static void openAccount() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Account Type (e.g., Savings, Checking): ");
            String accountType = scanner.nextLine();

            System.out.print("Enter Initial Balance: ");
            double balance = scanner.nextDouble();

            System.out.print("Enter Branch ID: ");
            int branchId = scanner.nextInt();

            System.out.print("Enter Customer ID: ");
            int customerId = scanner.nextInt();

            int accountId = generateAccountId(conn);

            String sql = "INSERT INTO Account (a_id, balance, type, b_id, c_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            pstmt.setDouble(2, balance);
            pstmt.setString(3, accountType);
            pstmt.setInt(4, branchId);
            pstmt.setInt(5, customerId);

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Account opened successfully. Your Account ID is: " + accountId : "Failed to open account.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void deposit() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Account ID: ");
            int accountId = scanner.nextInt();

            System.out.print("Enter Amount to Deposit: ");
            double amount = scanner.nextDouble();

            System.out.print("Enter Employee ID: ");
            int employeeId = scanner.nextInt();

            conn.setAutoCommit(false);

            PreparedStatement updateStmt = conn.prepareStatement("UPDATE Account SET balance = balance + ? WHERE a_id = ?");
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, accountId);

            int rows = updateStmt.executeUpdate();
            if (rows > 0) {
                PreparedStatement logStmt = conn.prepareStatement("INSERT INTO Transaction (t_id, t_type, amount, date, a_id, e_id) VALUES (?, ?, ?, ?, ?, ?)");
                logStmt.setInt(1, generateTransactionId(conn));
                logStmt.setString(2, "Deposit");
                logStmt.setDouble(3, amount);
                logStmt.setDate(4, new Date(System.currentTimeMillis()));
                logStmt.setInt(5, accountId);
                logStmt.setInt(6, employeeId);
                logStmt.executeUpdate();

                System.out.println("Deposit successful.");
                conn.commit();
            } else {
                System.out.println("Deposit failed. Account not found.");
                conn.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void withdraw() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Account ID: ");
            int accountId = scanner.nextInt();

            System.out.print("Enter Amount to Withdraw: ");
            double amount = scanner.nextDouble();

            System.out.print("Enter Employee ID: ");
            int employeeId = scanner.nextInt();

            conn.setAutoCommit(false);

            PreparedStatement checkStmt = conn.prepareStatement("SELECT balance FROM Account WHERE a_id = ?");
            checkStmt.setInt(1, accountId);
            ResultSet rs = checkStmt.executeQuery();


            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE Account SET balance = balance - ? WHERE a_id = ?");
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, accountId);
                    updateStmt.executeUpdate();

                    PreparedStatement logStmt = conn.prepareStatement("INSERT INTO Transaction (t_id, t_type, amount, date, a_id, e_id) VALUES (?, ?, ?, ?, ?, ?)");
                    logStmt.setInt(1, generateTransactionId(conn));
                    logStmt.setString(2, "Withdrawal");
                    logStmt.setDouble(3, amount);
                    logStmt.setDate(4, new Date(System.currentTimeMillis()));
                    logStmt.setInt(5, accountId);
                    logStmt.setInt(6, employeeId);
                    logStmt.executeUpdate();

                    System.out.println("Withdrawal successful.");
                    conn.commit();
                } else {
                    System.out.println("Insufficient balance.");
                    conn.rollback();
                }
            } else {
                System.out.println("Account not found.");
                conn.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void transfer() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Source Account ID: ");
            int fromId = scanner.nextInt();
            System.out.print("Enter Destination Account ID: ");
            int toId = scanner.nextInt();
            System.out.print("Enter Amount to Transfer: ");
            double amount = scanner.nextDouble();
            System.out.print("Enter Employee ID: ");
            int employeeId = scanner.nextInt();

            conn.setAutoCommit(false);

            PreparedStatement checkStmt = conn.prepareStatement("SELECT balance FROM Account WHERE a_id = ?");
            checkStmt.setInt(1, fromId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getDouble("balance") >= amount) {
                PreparedStatement withdraw = conn.prepareStatement("UPDATE Account SET balance = balance - ? WHERE a_id = ?");
                withdraw.setDouble(1, amount);
                withdraw.setInt(2, fromId);
                withdraw.executeUpdate();

                PreparedStatement deposit = conn.prepareStatement("UPDATE Account SET balance = balance + ? WHERE a_id = ?");
                deposit.setDouble(1, amount);
                deposit.setInt(2, toId);
                deposit.executeUpdate();

                PreparedStatement logStmt = conn.prepareStatement("INSERT INTO Transaction (t_id, t_type, amount, date, a_id, e_id) VALUES (?, ?, ?, ?, ?, ?)");
                int transId = generateTransactionId(conn);
                logStmt.setInt(1, transId);
                logStmt.setString(2, "Transfer");
                logStmt.setDouble(3, amount);
                logStmt.setDate(4, new Date(System.currentTimeMillis()));
                logStmt.setInt(5, fromId);
                logStmt.setInt(6, employeeId);
                logStmt.executeUpdate();

                conn.commit();
                System.out.println("Transfer successful.");
            } else {
                System.out.println("Transfer failed: insufficient funds or account not found.");
                conn.rollback();
            }

            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void viewTransactionHistory() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Account ID: ");
            int accountId = scanner.nextInt();

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Transaction WHERE a_id = ? ORDER BY date DESC");
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-- Transaction History --");
            while (rs.next()) {
                System.out.printf("ID: %d, Type: %s, Amount: %.2f, Date: %s, Employee ID: %d\n",
                        rs.getInt("t_id"),
                        rs.getString("t_type"),
                        rs.getDouble("amount"),
                        rs.getDate("date"),
                        rs.getInt("e_id"));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void viewAccountDetails() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Customer ID: ");
            int customerId = scanner.nextInt();

            String sql = "SELECT a.a_id, a.type, a.balance, b.b_name, b.b_address " +
                    "FROM Account a " +
                    "JOIN Branch b ON a.b_id = b.b_id " +
                    "WHERE a.c_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-- Account Details --");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("Account ID: %d\nType: %s\nBalance: %.2f\nBranch: %s (%s)\n\n",
                        rs.getInt("a_id"),
                        rs.getString("type"),
                        rs.getDouble("balance"),
                        rs.getString("b_name"),
                        rs.getString("b_address"));
            }

            if (!found) {
                System.out.println("No accounts found for this customer.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static int generateTransactionId(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(t_id) FROM Transaction");
        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
        return 501; // start ID if table is empty
    }

    private static int generateAccountId(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(a_id) FROM Account");
        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
        return 301; // start ID if table is empty
    }

    public static void loanRepayment(){
        Scanner scanner = new Scanner(System.in);
        try(Connection conn= DBConnection.getConnection()){
            System.out.print("Enter Loan ID: ");
            int loanid = scanner.nextInt();
            System.out.print("Enter repayment amount: ");
            double amount = scanner.nextDouble();
            System.out.print("Enter Account ID: ");
            int accountId = scanner.nextInt();
            System.out.println("Enter Employee ID: ");
            int employeeId = scanner.nextInt();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("SELECT balance FROM Account WHERE a_id = ?");
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getDouble("balance") >= amount) {
                PreparedStatement wthdrw = conn.prepareStatement("UPDATE Account SET balance = balance + ? WHERE a_id = ?");
                wthdrw.setDouble(1, amount);
                wthdrw.setInt(2, accountId);
                wthdrw.executeUpdate();
                int transId = generateTransactionId(conn);

                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Transaction VALUES (?, 'Repayment', ?, CURRENT_DATE, ?, ?)");
                stmt.setInt(1, transId);
                stmt.setDouble(2, amount);
                stmt.setInt(3, accountId);
                stmt.setInt(4, employeeId);

                stmt.executeUpdate();

                PreparedStatement rep = conn.prepareStatement("UPDATE Loan SET l_amount = l_amount-? WHERE l_id = ?");
                rep.setDouble(1, amount);
                rep.setInt(2, loanid);

                int update = rep.executeUpdate();
                if (update > 0) {
                    conn.commit();
                    System.out.println("Loan Repayment successful.");
                }else{
                    System.out.println("Loan Repayment failed.");
                    conn.rollback();
                }

            }else{
                System.out.println("Loan Repayment failed (Insufficient Balance).");
                conn.rollback();

            }

        }catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());

        }
    }

    public static void deleteAccount() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Account ID: ");
        int accountId = scanner.nextInt();
        try(Connection conn = DBConnection.getConnection()){
            String sql = "DELETE FROM Account WHERE a_id = ?";
            PreparedStatement ptstmt = conn.prepareStatement(sql);
            ptstmt.setInt(1, accountId);
            int update = ptstmt.executeUpdate();
            if (update > 0) {
                System.out.println("Account Deleted Successfully.");
            }else{
                System.out.println("Account Deletion failed.");
            }

        }catch(SQLException e){
            System.out.println("Database error: " + e.getMessage());
        }
    }
//   buy&sell investment
}
