import java.util.Scanner;
import java.sql.*;

public class CustomerMenu {
    public static void display() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n-- Customer Menu --");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. View Transaction History");
            System.out.println("6. View Account Details");
            System.out.println("7. Loan Repayments");
            System.out.println("8. Delete Account");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> CustomerOperations.openAccount();
                case 2 -> CustomerOperations.deposit();
                case 3 -> CustomerOperations.withdraw();
                case 4 -> CustomerOperations.transfer();
                case 5 -> CustomerOperations.viewTransactionHistory();
                case 6 -> CustomerOperations.viewAccountDetails();
                case 7 -> CustomerOperations.loanRepayment();
                case 8 -> CustomerOperations.deleteAccount();
                case 0 -> System.out.println("Returning to Main Menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice!=0);
    }
}