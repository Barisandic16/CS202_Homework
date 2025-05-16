import java.util.Scanner;
import java.sql.*;

public class LoanOperations {

    public static void reviewApplications() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT l_id, a_id, l_amount, l_state FROM Loan WHERE l_state = 'Pending'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-- Pending Loan Applications --");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("Loan ID: %d | Account ID: %d | Amount: %.2f | Status: %s\n",
                        rs.getInt("l_id"),
                        rs.getInt("a_id"),
                        rs.getDouble("l_amount"),
                        rs.getString("l_state"));
            }
            if (!found) {
                System.out.println("No pending applications found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void approveRejectLoans() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Loan ID: ");
            int loanId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Approve or Reject (A/R): ");
            String decision = scanner.nextLine().trim();
            String status = decision.equalsIgnoreCase("A") ? "Approved" :
                    decision.equalsIgnoreCase("R") ? "Rejected" : null;

            if (status == null) {
                System.out.println("Invalid input. Enter 'A' for Approve or 'R' for Reject.");
                return;
            }

            String sql = "UPDATE Loan SET l_state = ? WHERE l_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, loanId);

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ?
                    "Loan status updated to: " + status :
                    "Loan ID not found or update failed.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void trackRepayments() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Loan ID: ");
            int loanId = scanner.nextInt();

            String loanQuery = "SELECT a_id FROM Loan WHERE l_id = ?";
            PreparedStatement loanStmt = conn.prepareStatement(loanQuery);
            loanStmt.setInt(1, loanId);
            ResultSet loanRs = loanStmt.executeQuery();

            if (!loanRs.next()) {
                System.out.println("Loan ID not found.");
                return;
            }

            int accountId = loanRs.getInt("a_id");

            String sql = "SELECT t_id, amount, date FROM Transaction " +
                    "WHERE a_id = ? AND t_type = 'Repayment' ORDER BY date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-- Repayment History --");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("Transaction ID: %d | Amount Paid: %.2f | Date: %s\n",
                        rs.getInt("t_id"),
                        rs.getDouble("amount"),
                        rs.getDate("date"));
            }
            if (!found) {
                System.out.println("No repayments found for this loan.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
