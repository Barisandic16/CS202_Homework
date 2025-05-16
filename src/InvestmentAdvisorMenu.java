import java.util.Scanner;
import java.sql.*;

public class InvestmentAdvisorMenu {
    public static void display() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n-- Investment Advisor Menu --");
            System.out.println("1. Manage Portfolio");
            System.out.println("2. Suggest Investments");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> InvestmentOperations.managePortfolio();
                case 2 -> InvestmentOperations.suggestInvestments();
                case 0 -> System.out.println("Returning to Main Menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice!=0);
    }
}