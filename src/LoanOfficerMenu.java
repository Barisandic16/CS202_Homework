import java.util.Scanner;
import java.sql.*;

public class LoanOfficerMenu {
    public static void display() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n-- Loan Officer Menu --");
            System.out.println("1. Review Applications");
            System.out.println("2. Approve/Reject Loans");
            System.out.println("3. Track Repayments");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> LoanOperations.reviewApplications();
                case 2 -> LoanOperations.approveRejectLoans();
                case 3 -> LoanOperations.trackRepayments();
                case 0 -> System.out.println("Returning to Main Menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice!=0);
    }
}