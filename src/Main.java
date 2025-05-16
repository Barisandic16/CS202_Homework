import java.util.Scanner;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Bank Management System ---");
            System.out.println("1. Customer");
            System.out.println("2. Administrator");
            System.out.println("3. Loan Officer");
            System.out.println("4. Investment Advisor");
            System.out.println("0. Exit");
            System.out.print("Choose your role: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    CustomerMenu.display();
                    break;
                case 2:
                    AdminMenu.display();
                    break;
                case 3:
                    LoanOfficerMenu.display();
                    break;
                case 4:
                    InvestmentAdvisorMenu.display();
                    break;
                case 0:
                    System.out.println("Exiting system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }
}