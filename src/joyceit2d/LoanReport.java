package joyceit2d;

import java.sql.*;
import java.util.Scanner;

public class LoanReport {

    public void generateReport() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.println("\nREPORT MENU:");
        System.out.println("1. Individual Report");
        System.out.println("2. General Report");

        System.out.print("Enter your choice: ");
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter 1 or 2.");
            sc.next();
            System.out.print("Enter your choice: ");
        }
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                generateIndividualReport(conf);
                break;
            case 2:
                generateGeneralReport(conf);
                break;
            default:
                System.out.println("Invalid choice. Please enter 1 or 2.");
                break;
        }
    }
public void generateIndividualReport(config conf) {
    Scanner sc = new Scanner(System.in);

    System.out.print("Enter Applicant ID for the report: ");
    int applicantId = sc.nextInt();

    if (!isApplicantExist(conf, applicantId)) {
        System.out.println("Applicant with ID " + applicantId + " does not exist.");
        return;
    }

    // Query to fetch applicant's details along with loan applications
    String query = "SELECT tbl_loan_application.*, tbl_applicant.a_fname, tbl_applicant.a_lname, tbl_applicant.a_phone, tbl_applicant.a_address "
                 + "FROM tbl_loan_application "
                 + "JOIN tbl_applicant ON tbl_applicant.a_id = tbl_loan_application.a_id "
                 + "WHERE tbl_loan_application.a_id = ?";
    ResultSet resultSet = conf.getData(query, applicantId);

    System.out.println("\n--- Individual Loan Report ---");

    // Fetch applicant's name and phone number to display above the table
    String firstName = "";
    String lastName = "";
    String phone = "";
    String address = "";

    try {
        if (resultSet.next()) {
            firstName = resultSet.getString("a_fname");
            lastName = resultSet.getString("a_lname");
            phone = resultSet.getString("a_phone"); // Fetch the phone number as well
            address = resultSet.getString("a_address"); // Fetch the address of the applicant
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Display the applicant's ID, name, phone number, and address in the requested format
    System.out.println("Applicant ID: " + applicantId);
    System.out.println("Name: " + firstName + " " + lastName);
    System.out.println("Phone: " + phone); // Display phone number
    System.out.println("Address: " + address); // Display applicant's address
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    // Now, set up the table header for the loan information
    String header = String.format("%-12s%-20s%-30s%-20s%-20s%-20s%-25s",
                                  "Loan ID", "Loan Type", "Loan Amount", "Loan Term", "Loan Status",
                                  "Repayable Amount", "Loan Date");
    System.out.println(header);
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    // Query again to fetch loan details for the applicant
    query = "SELECT * FROM tbl_loan_application "
          + "JOIN tbl_applicant ON tbl_applicant.a_id = tbl_loan_application.a_id "
          + "WHERE tbl_loan_application.a_id = ?";
    resultSet = conf.getData(query, applicantId);

    try {
        while (resultSet.next()) {
            String loanId = resultSet.getString("loan_id");
            String loanType = resultSet.getString("loan_type");
            double loanAmount = resultSet.getDouble("loan_amount");
            String loanTerm = resultSet.getString("loan_term");
            String loanStatus = resultSet.getString("loan_status");
            double repayableAmount = loanAmount * 1.10; // This is just an example calculation
            String loanDate = resultSet.getString("loan_date");

            // Display each loan in the report
            String row = String.format("%-12s%-20s%-20.2f%-30s%-20s%-20.2f%-25s",
                                       loanId, loanType, loanAmount, loanTerm, loanStatus, repayableAmount, loanDate);
            System.out.println(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public void generateGeneralReport(config conf) {

        String query = "SELECT tbl_applicant.a_id, tbl_applicant.a_fname, tbl_applicant.a_lname, "
                     + "SUM(tbl_loan_application.loan_amount) AS total_loan_amount "
                     + "FROM tbl_loan_application "
                     + "JOIN tbl_applicant ON tbl_applicant.a_id = tbl_loan_application.a_id "
                     + "WHERE tbl_loan_application.loan_status NOT IN ('Pending', 'Denied') "
                     + "GROUP BY tbl_applicant.a_id, tbl_applicant.a_fname, tbl_applicant.a_lname";
        ResultSet resultSet = conf.getData(query);

        System.out.println("\n--- General Loan Report ---");

       
        String header = String.format("%-20s%-20s%-20s%-20s",
                                      "Applicant ID", "First Name", "Last Name", "Total Loan Amount");
        System.out.println(header);
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

   
        try {
            while (resultSet.next()) {
                String applicantId = resultSet.getString("a_id");
                String firstName = resultSet.getString("a_fname");
                String lastName = resultSet.getString("a_lname");
                double totalLoanAmount = resultSet.getDouble("total_loan_amount");

       
                String row = String.format("%-12s%-20s%-20s%-25.2f",
                                           applicantId, firstName, lastName, totalLoanAmount);
                System.out.println(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isApplicantExist(config conf, int applicantId) {
        String query = "SELECT COUNT(*) FROM tbl_applicant WHERE a_id = ?";
        ResultSet resultSet = conf.getData(query, applicantId);

        try {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}