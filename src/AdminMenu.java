import java.util.InputMismatchException;
import java.util.Scanner;

public class AdminMenu {

    public static void display() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        do {
            System.out.println("\n======= Administrator Menu =======");
            System.out.println("1. Create Account Type");
            System.out.println("2. Delete Account Type");
            System.out.println("3. Modify Account Type");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // clear the buffer

                switch (choice) {
                    case 1 -> AdminOperations.createAccountType();
                    case 2 -> AdminOperations.deleteAccountType();
                    case 3 -> AdminOperations.modifyAccountType();
                    case 0 -> System.out.println("Returning to Main Menu...");
                    default -> System.out.println("Invalid option. Please try again.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }

        } while (choice != 0);
    }
}
