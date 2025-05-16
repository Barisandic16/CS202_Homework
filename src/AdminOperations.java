import java.util.Scanner;
import java.sql.*;

public class AdminOperations {

    public static void createAccountType() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Account ID: ");
            String ID = scanner.nextLine();
            System.out.print("Enter Account Type Name: ");
            String typeName = scanner.nextLine();


            String sql = "UPDATE Account SET type = ? WHERE a_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typeName);
            pstmt.setString(2, ID);

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Account type created successfully." : "Failed to create account type.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void deleteAccountType() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Account Type ID to delete: ");
            int typeId = scanner.nextInt();

            String sql = "UPDATE Account SET type = null WHERE a_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, typeId);

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Account type deleted successfully." : "No such account type found.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void modifyAccountType() {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter new type name: ");
            String type = scanner.nextLine();
            System.out.print("Enter Account ID: ");
            int accountID = scanner.nextInt();

            String sql = "UPDATE Account SET type = ? WHERE a_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            pstmt.setInt(2, accountID);

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Account type updated successfully." : "No such account type found.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
