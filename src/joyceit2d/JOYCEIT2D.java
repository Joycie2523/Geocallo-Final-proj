
package joyceit2d;
import java.util.InputMismatchException;
import java.util.Scanner;

public class JOYCEIT2D {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        applicant applicantManager = new applicant();
        loanapplication loanManager = new loanapplication();
        boolean running = true;

        while (running) {
            System.out.println("Welcome To Loan Management System!");
            System.out.println("1. Manage Applicant");
            System.out.println("2. Manage Loan Application");
            System.out.println("3. Exit");
            System.out.print("Enter action: ");

            try {
                int action = scanner.nextInt();

                switch (action) {
                    case 1:
                        applicantManager.manageApplicant();
                        break;
                    case 2:
                        loanManager.manageLoanApplication();
                        break;
                    case 3:
                        System.out.print("Are you sure you want to exit? (yes/no): ");
                        String confirmExit = scanner.next();
                        if (confirmExit.equalsIgnoreCase("yes")) {
                            System.out.println("Exiting...");
                            running = false; 
                        } else {
                            System.out.println("Returning to the main menu...");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); 
            }
        }

        scanner.close(); 
    }
}


