import java.sql.*;
import java.util.Scanner;

public class InvestmentOperations {

    public static void managePortfolio() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Customer ID: ");
            int customerId = scanner.nextInt();

            String sql = "SELECT ip.p_id, i.i_id, i.i_type, i.i_date, i.i_amount " +
                    "FROM Investment_Portfolio ip " +
                    "JOIN Investment i ON ip.p_id = i.p_id " +
                    "JOIN Account a ON ip.a_id = a.a_id " +
                    "WHERE a.c_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-- Investment Portfolio Details --");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("Portfolio ID: %d | Investment ID: %d | Type: %s | Date: %s | Amount: %.2f\n",
                        rs.getInt("p_id"),
                        rs.getInt("i_id"),
                        rs.getString("i_type"),
                        rs.getDate("i_date"),
                        rs.getDouble("i_amount"));
            }
            if (!found) {
                System.out.println("No investments found for this customer.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void suggestInvestments() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT i_type, COUNT(*) AS total_investments, AVG(i_amount) AS avg_invested " +
                    "FROM Investment " +
                    "GROUP BY i_type " +
                    "ORDER BY avg_invested DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-- Suggested Investment Avenues (by average size) --");
            while (rs.next()) {
                System.out.printf("Type: %s | Total: %d | Avg Invested: %.2f\n",
                        rs.getString("i_type"),
                        rs.getInt("total_investments"),
                        rs.getDouble("avg_invested"));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
