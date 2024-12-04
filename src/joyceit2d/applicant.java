package joyceit2d;
import java.util.Scanner;

public class applicant {

    public void manageApplicant() {
        Scanner sc = new Scanner(System.in);
        String response;
        boolean exit = true;

        do {
            System.out.println("\nAPPLICANT MENU:");
            System.out.println("1. ADD");
            System.out.println("2. VIEW");
            System.out.println("3. UPDATE");
            System.out.println("4. DELETE");
            System.out.println("5. EXIT");

            System.out.print("Enter Action: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                sc.next();
                System.out.print("Enter Action: ");
            }
            int action = sc.nextInt();

            switch (action) {
                case 1:
                    addApplicant();
                    break;
                case 2:
                    viewApplicants();
                    break;
                case 3:
                    viewApplicants();
                    updateApplicant();
                    viewApplicants();
                    break;
                case 4:
                    viewApplicants();
                    deleteApplicant();
                    viewApplicants();
                    break;
                case 5:
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

            if (action >= 1 && action <= 4) {
                System.out.print("Do you want to make another transaction? (yes/no): ");
                response = sc.next();
                if (!response.equalsIgnoreCase("yes")) {
                    exit = false;
                }
            }
        } while (exit);

        System.out.println("Thank you, come again!");
    }

    public void addApplicant() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        int applicantId;
        do {
            System.out.print("Enter Applicant's : ");
            applicantId = sc.nextInt();
            sc.nextLine();

            if (applicantId < 101) {
                System.out.println("Applicant ID must be 101 or above. Please enter a valid ID.");
            } else if (isApplicantIdExist(applicantId)) {
                System.out.println("Applicant ID already exists. Please enter a unique ID.");
            }
        } while (applicantId < 101 || isApplicantIdExist(applicantId));

        System.out.print("Enter First Name: ");
        String firstName = sc.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = sc.nextLine();

        String phone;
        do {
            System.out.print("Enter Phone: ");
            phone = sc.nextLine();
            if (!isValidPhoneNumber(phone)) {
                System.out.println("Invalid phone number. Please enter an 11-digit phone number.");
            }
        } while (!isValidPhoneNumber(phone));

        System.out.print("Enter Address: ");
        String address = sc.nextLine();

        int age;
        do {
            System.out.print("Enter Age : ");
            age = sc.nextInt();
            if (age < 18 || age > 65) {
                System.out.println("Age should be between 18 and 65.");
            }
        } while (age < 18 || age > 65);

        sc.nextLine(); 

        System.out.print("Enter Employment Status (Employed/Unemployed): ");
        String employmentStatus = sc.nextLine();

        double income;
        do {
            System.out.print("Enter Income: ");
            income = sc.nextDouble();
            if (income < 5000) {
                System.out.println("Income should be at least 5000 per month.");
            }
        } while (income < 5000);

        String sql = "INSERT INTO tbl_applicant (a_id, a_fname, a_lname, a_phone, a_address, a_age, a_employment_status, a_income) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, applicantId, firstName, lastName, phone, address, age, employmentStatus, income);

        System.out.println("Applicant added successfully.");
    }

    public void viewApplicants() {
        config conf = new config();

        String query = "SELECT a_id, a_fname, a_lname, a_phone, a_address, a_age, a_employment_status, a_income FROM tbl_applicant";

        String[] headers = {"Applicant ID", "First Name", "Last Name", "Phone", "Address", "Age", "Employment Status", "Income"};
        String[] columns = {"a_id", "a_fname", "a_lname", "a_phone", "a_address", "a_age", "a_employment_status", "a_income"};

        conf.viewRecords(query, headers, columns);
    }

    private void updateApplicant() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Applicant ID to update: ");
        int applicantId = sc.nextInt();
        sc.nextLine(); 

        while (!isValidApplicantId(applicantId)) {
            System.out.println("Applicant ID does not exist! Please enter a valid Applicant ID.");
            System.out.print("Enter Applicant ID to update: ");
            applicantId = sc.nextInt();
            sc.nextLine();  
        }

        System.out.print("Enter new First Name: ");
        String firstName = sc.nextLine();

        System.out.print("Enter new Last Name: ");
        String lastName = sc.nextLine();

        String phone;
        do {
            System.out.print("Enter new Phone: ");
            phone = sc.nextLine();
            if (!isValidPhoneNumber(phone)) {
                System.out.println("Invalid phone number. Please enter an 11-digit phone number.");
            }
        } while (!isValidPhoneNumber(phone));

        System.out.print("Enter new Address: ");
        String address = sc.nextLine();

        int age;
        do {
            System.out.print("Enter new Age (must be between 18 and 65): ");
            age = sc.nextInt();
            if (age < 18 || age > 65) {
                System.out.println("Age should be between 18 and 65.");
            }
        } while (age < 18 || age > 65);

        sc.nextLine();  

        System.out.print("Enter new Employment Status (Employed/Unemployed): ");
        String employmentStatus = sc.nextLine();

        double income;
        do {
            System.out.print("Enter new Income: ");
            income = sc.nextDouble();
            if (income < 5000) {
                System.out.println("Income should be at least 5000 per month.");
            }
        } while (income < 5000);

        String sql = "UPDATE tbl_applicant SET a_fname = ?, a_lname = ?, a_phone = ?, a_address = ?, a_age = ?, a_employment_status = ?, a_income = ? WHERE a_id = ?";
        conf.updateRecord(sql, firstName, lastName, phone, address, age, employmentStatus, income, applicantId);

        System.out.println("Applicant updated successfully.");
    }

    public void deleteApplicant() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Applicant ID to delete: ");
        int applicantId = sc.nextInt();
        sc.nextLine(); 

        // Validate if the applicant ID exists
        while (!isValidApplicantId(applicantId)) {
            System.out.println("Applicant ID does not exist! Please enter a valid Applicant ID.");
            System.out.print("Enter Applicant ID to delete: ");
            applicantId = sc.nextInt();
            sc.nextLine();  
        }

        String sql = "DELETE FROM tbl_applicant WHERE a_id = ?";
        conf.deleteRecord(sql, applicantId);

        System.out.println("Applicant deleted successfully.");
    }

    private boolean isValidApplicantId(int applicantId) {
        config conf = new config();
        String query = "SELECT COUNT(*) FROM tbl_applicant WHERE a_id = ?";
        return conf.getSingleValue(query, applicantId) > 0; 
    }

    private boolean isApplicantIdExist(int applicantId) {
        config conf = new config();
        String query = "SELECT COUNT(*) FROM tbl_applicant WHERE a_id = ?";
        return conf.getSingleValue(query, applicantId) > 0; 
    }

    private boolean isValidPhoneNumber(String phone) {
        String phonePattern = "^[0-9]{11}$";
        return phone.matches(phonePattern);
    }
}


