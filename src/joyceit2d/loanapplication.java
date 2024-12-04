package joyceit2d;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class loanapplication {

    public void manageLoanApplication() {
        Scanner sc = new Scanner(System.in);
        String response;
        boolean exit = true;

        do {
            System.out.println("\nLOAN APPLICATION MENU:");
            System.out.println("1. ADD");
            System.out.println("2. VIEW");
            System.out.println("3. UPDATE");
            System.out.println("4. DELETE");
            System.out.println("5. GENERATE REPORT");
            System.out.println("6. EXIT");

            System.out.print("Enter Action: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                sc.next();
                System.out.print("Enter Action: ");
            }
            int action = sc.nextInt();

            switch (action) {
                case 1:
                    addLoanApplication();
                    break;
                case 2:
                    viewLoanApplications();
                    break;
                case 3:
                    viewLoanApplications();
                    updateLoanApplication();
                    viewLoanApplications();
                    break;
                case 4:
                    viewLoanApplications();
                    deleteLoanApplication();
                    viewLoanApplications();
                    break;
                case 5:
                    LoanReport loanReport = new LoanReport();
                    loanReport.generateReport();
                    break;
                case 6:
                    System.out.print("Exiting... Are you sure? (yes/no): ");
                    String resp = sc.next();
                    if (resp.equalsIgnoreCase("yes")) {
                        exit = false;
                    }
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

            if (action >= 1 && action <= 5) {
                System.out.print("Do you want to make another transaction? (yes/no): ");
                response = sc.next();
                if (!response.equalsIgnoreCase("yes")) {
                    exit = false;
                }
            }
        } while (exit);

        System.out.println("Thank you, come again!");
    }

    public void addLoanApplication() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        applicant app = new applicant();
        app.viewApplicants();

        System.out.print("Enter the ID of the Selected Applicant: ");
        int applicantId = sc.nextInt();

        String appQuery = "SELECT a_id FROM tbl_applicant WHERE a_id=?";
        while (conf.getSingleValue(appQuery, applicantId) == 0) {
            System.out.print("Applicant ID does not exist! Try again: ");
            applicantId = sc.nextInt();
        }

        String loanType;
        do {
            System.out.print("Enter Loan Type : ");
            loanType = sc.next();
            if (loanType.isEmpty()) {
                System.out.println("Loan Type cannot be empty. Try again.");
            }
        } while (loanType.isEmpty());

        double loanAmount = 0;
        boolean validAmount = false;
        while (!validAmount) {
            System.out.print("Enter Loan Amount : ");
            if (sc.hasNextDouble()) {
                loanAmount = sc.nextDouble();
                if (loanAmount <= 0) {
                    System.out.println("Loan amount must be greater than 0.");
                } else {
                    validAmount = true;
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.next();
            }
        }

        int loanTerm;
        do {
            System.out.print("Enter Loan Term (in months): ");
            loanTerm = sc.nextInt();
            if (loanTerm <= 0) {
                System.out.println("Loan term must be greater than 0.");
            }
        } while (loanTerm <= 0);

        double interestRate;
        do {
            System.out.print("Enter Interest Rate: ");
            interestRate = sc.nextDouble();
            if (interestRate <= 0) {
                System.out.println("Interest rate must be greater than 0.");
            }
        } while (interestRate <= 0);

        // Simple interest formula: Loan Amount + (Loan Amount * (Interest Rate / 100) * Loan Term (in years))
        double repayableAmount = loanAmount + (loanAmount * (interestRate / 100) * (loanTerm / 12.0));

        System.out.print("Enter Loan Date (yyyy/MM/dd): ");
        sc.nextLine(); // consume any leftover newline
        String loanDateStr = sc.nextLine();

        // Validate date format
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        boolean validDate = false;
        while (!validDate) {
            try {
                LocalDate loanDate = LocalDate.parse(loanDateStr, dateFormat);
                loanDateStr = loanDate.format(dateFormat); // Reformat to correct format
                validDate = true;
            } catch (Exception e) {
                System.out.print("Invalid date format. Please enter the date in yyyy/MM/dd format: ");
                loanDateStr = sc.nextLine();
            }
        }

        System.out.print("Enter Loan Status (e.g., Pending, Approved, Denied): ");
        String loanStatus = sc.nextLine();
        if (loanStatus.isEmpty()) {
            loanStatus = "Pending"; // Default status
        }

        String sql = "INSERT INTO tbl_loan_application (a_id, loan_type, loan_amount, loan_term, interest_rate, repayable_amount, loan_date, loan_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, applicantId, loanType, loanAmount, loanTerm, interestRate, repayableAmount, loanDateStr, loanStatus);

        System.out.println("Loan application added successfully.");
    }

    public void viewLoanApplications() {
        config conf = new config();

        String query = "SELECT tbl_loan_application.loan_id, tbl_applicant.a_id, a_fname, a_lname, loan_type, loan_amount, loan_term, interest_rate, repayable_amount, loan_date, loan_status " +
                       "FROM tbl_loan_application " +
                       "LEFT JOIN tbl_applicant ON tbl_applicant.a_id = tbl_loan_application.a_id";

        String[] headers = {"Loan ID", "Applicant ID", "First Name", "Last Name", "Loan Type", "Loan Amount", "Loan Term", "Interest Rate", "Repayable Amount", "Loan Date", "Status"};
        String[] columns = {"loan_id", "a_id", "a_fname", "a_lname", "loan_type", "loan_amount", "loan_term", "interest_rate", "repayable_amount", "loan_date", "loan_status"};

        conf.viewRecords(query, headers, columns);
    }

    private void updateLoanApplication() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Loan ID to update: ");
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid Loan ID.");
            sc.next();
            System.out.print("Enter Loan ID to update: ");
        }
        int loanId = sc.nextInt();

        String loanQuery = "SELECT loan_id FROM tbl_loan_application WHERE loan_id=?";
        while (conf.getSingleValue(loanQuery, loanId) == 0) {
            System.out.print("Loan ID does not exist. Try again: ");
            loanId = sc.nextInt();
        }

        String loanType;
        do {
            System.out.print("Enter new Loan Type: ");
            loanType = sc.next();
            if (loanType.isEmpty()) {
                System.out.println("Loan Type cannot be empty. Try again.");
            }
        } while (loanType.isEmpty());

        double loanAmount;
        do {
            System.out.print("Enter new Loan Amount: ");
            loanAmount = sc.nextDouble();
            if (loanAmount <= 0) {
                System.out.println("Loan amount must be greater than 0.");
            }
        } while (loanAmount <= 0);

        int loanTerm;
        do {
            System.out.print("Enter new Loan Term (months, must be a positive number): ");
            loanTerm = sc.nextInt();
            if (loanTerm <= 0) {
                System.out.println("Loan term must be greater than 0.");
            }
        } while (loanTerm <= 0);

        double interestRate;
        do {
            System.out.print("Enter new Interest Rate (positive percentage): ");
            interestRate = sc.nextDouble();
            if (interestRate <= 0) {
                System.out.println("Interest rate must be greater than 0.");
            }
        } while (interestRate <= 0);

        // Recalculate the repayable amount with the updated values using the simple interest formula
        double repayableAmount = loanAmount + (loanAmount * (interestRate / 100));

        System.out.print("Enter new Loan Status: ");
        String loanStatus = sc.next();

        String sql = "UPDATE tbl_loan_application SET loan_type = ?, loan_amount = ?, loan_term = ?, interest_rate = ?, repayable_amount = ?, loan_status = ? WHERE loan_id = ?";
        conf.updateRecord(sql, loanType, loanAmount, loanTerm, interestRate, repayableAmount, loanStatus, loanId);

        System.out.println("Loan application updated successfully.");
    }

    public void deleteLoanApplication() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Loan ID to delete: ");
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid Loan ID.");
            sc.next();
            System.out.print("Enter Loan ID to delete: ");
        }
        int loanId = sc.nextInt();

        String loanQuery = "SELECT loan_id FROM tbl_loan_application WHERE loan_id=?";
        while (conf.getSingleValue(loanQuery, loanId) == 0) {
            System.out.print("Loan ID does not exist. Try again: ");
            loanId = sc.nextInt();
        }

        String deleteQuery = "DELETE FROM tbl_loan_application WHERE loan_id=?";
        conf.deleteRecord(deleteQuery, loanId);

        System.out.println("Loan application deleted successfully.");
    }
}
