import java.util.Scanner;
import java.sql.*;

class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/BankSystem";
    private static final String USER = "root";
    private static final String PASSWORD = "Utkubaris9703";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}