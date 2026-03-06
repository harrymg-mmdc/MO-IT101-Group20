import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Author: lr.kfaparece@mmdc.mcl.edu.ph

public class PayrollSystem {

        // using scanner for Login Input of username and password
    static Scanner scanner = new Scanner(System.in);

        // mapping to map same values from employee and attendance record
    static Map<String, String[]> employees = new HashMap<>();
    static Map<String, double[]> monthlyHours = new HashMap<>();
        // double[7][2] → months June(0) to December(6), 0=1st cutoff, 1=2nd cutoff

    // Main method – starting point of the payroll system program
    public static void main(String[] args) {

        // Load employee information from the employee CSV file
        loadEmployees();

        // Load attendance records and compute worked hours
        loadAttendance();

        // ================= LOGIN SYSTEM =================

        // Display the login title
        System.out.println("=== Payroll System Login ===");

        // Ask the user to enter a username
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        // Ask the user to enter a password
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        // Validate login credentials
        // Only two valid usernames exist: "employee" or "payroll_staff"
        // The password must be "12345"
        if (!(username.equals("employee") || username.equals("payroll_staff")) || !password.equals("12345")) {

            // Display error message if login credentials are incorrect
            System.out.println("Incorrect username and/or password.");
            return;
        }

        // Redirect the user based on their role
        // Employee users go to the employee menu
        if (username.equals("employee")) {
            employeeMenu();

            // Payroll staff users go to the payroll menu
        } else {
            payrollMenu();
        }
    }

    // ================= LOAD EMPLOYEE DATA =================

    // Method that reads the employee CSV file
    // and stores employee information into the system
    static void loadEmployees() {

        // Try-with-resources automatically closes the file after reading
        try (BufferedReader br = new BufferedReader(
                new FileReader("MotorPH_Employee Data (IT101 - H1101 - Group 20)  - Employee Details.csv"))) {

            // Skip the header row of the CSV file
            br.readLine();

            String line;

            // Read each row from the employee details file
            while ((line = br.readLine()) != null) {

                // Split the CSV row while correctly handling commas inside quotation marks
                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // Extract the employee number (unique identifier)
                String empNo = f[0].trim();

                // Combine last name and first name to create the employee's full name
                String name = f[2].trim() + " " + f[1].trim();

                // Extract employee birthday from the CSV file
                String birthday = f[3].trim();

                // Extract the hourly rate
                // Remove quotation marks and commas before storing the value
                String hourly = f[18].replace("\"", "").replace(",", "").trim();

                // Store employee information in the employees map
                // Format: employee number → [name, birthday, hourly rate]
                employees.put(empNo, new String[]{name, birthday, hourly});

                // Initialize storage for monthly worked hours
                // Each employee gets 14 slots:
                // 7 months (June–December) × 2 cutoffs per month
                monthlyHours.put(empNo, new double[14]);
            }

        } catch (Exception e) {

            // Display error message if employee data cannot be loaded
            System.out.println("Error loading employees.");
        }
    }

    // ================= LOAD ATTENDANCE =================

    // Method that reads the attendance CSV file
    // and computes the total worked hours for each employee per cutoff period
    static void loadAttendance() {

        // Try-with-resources automatically closes the file after reading
        try (BufferedReader br = new BufferedReader(
                new FileReader("MotorPH_Employee Data (IT101 - H1101 - Group 20)  - Attendance Record.csv"))) {

            // Skip the header row of the CSV file
            br.readLine();

            String line;

            // Read each row of the attendance file
            while ((line = br.readLine()) != null) {

                // Split the CSV row while correctly handling commas inside quotes
                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // Extract employee number from the row
                String empNo = f[0].trim();

                // Skip the record if the employee does not exist in the employee list
                if (!employees.containsKey(empNo)) continue;

                // Extract the date from the attendance record (format: MM/DD/YYYY)
                String[] date = f[3].split("/");

                // Get the month and day values
                int month = Integer.parseInt(date[0]);
                int day = Integer.parseInt(date[1]);

                // Only process attendance records from June to December
                if (month < 6 || month > 12) continue;

                // Determine the correct last day of the month for the year 2024
                YearMonth ym = YearMonth.of(2024, month);
                int lastDay = ym.lengthOfMonth();

                // Skip invalid days that exceed the actual last day of the month
                if (day > lastDay) continue;

                // Compute the number of hours worked based on time-in and time-out
                double hours = computeHours(f[4], f[5]);

                // Convert month number into index starting from June
                // June = 0, July = 1, ..., December = 6
                int monthIndex = month - 6;

                // Determine which payroll cutoff the record belongs to
                int cutoff;

                // First cutoff covers days 1–15
                if (day <= 15) {
                    cutoff = 0;

                    // Second cutoff covers days 16–last day of the month
                } else {
                    cutoff = 1;
                }

                // Add the worked hours to the correct month and cutoff slot
                // monthlyHours stores 2 slots per month (1st cutoff and 2nd cutoff)
                monthlyHours.get(empNo)[monthIndex * 2 + cutoff] += hours;
            }

        } catch (Exception e) {

            // Display error message if the attendance file cannot be loaded
            System.out.println("Error loading attendance.");
        }
    }

    // ================= COMPUTE HOURS =================
    // Method to compute the total worked hours between time-in and time-out
    // Applies work schedule rules, grace period, and lunch deduction
    static double computeHours(String in, String out) {
        try {

            // Formatter used to convert time strings (e.g., "8:30") into LocalTime objects
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

            // Convert the time-in and time-out strings into LocalTime objects
            LocalTime timeIn = LocalTime.parse(in, formatter);
            LocalTime timeOut = LocalTime.parse(out, formatter);

            // Define official working hours
            LocalTime workStart = LocalTime.of(8, 0);   // Work starts at 8:00 AM
            LocalTime workEnd = LocalTime.of(17, 0);    // Work ends at 5:00 PM
            LocalTime graceLimit = LocalTime.of(8, 10); // Grace period allowed until 8:10 AM

            // If employee clocks in earlier than 8:00 AM, treat it as exactly 8:00 AM
            if (timeIn.isBefore(workStart)) {
                timeIn = workStart;
            }

            // If employee clocks out later than 5:00 PM, treat it as exactly 5:00 PM
            if (timeOut.isAfter(workEnd)) {
                timeOut = workEnd;
            }

            // Apply grace period rule
            // If the employee arrives between 8:00 and 8:10, treat it as 8:00 (not late)
            if (!timeIn.isAfter(graceLimit)) {
                timeIn = workStart;
            }

            // If time-out is earlier than or equal to time-in, it is invalid
            // Return 0 worked hours
            if (!timeOut.isAfter(timeIn)) {
                return 0;
            }

            // Compute the total worked minutes between time-in and time-out
            long workedMinutes = Duration.between(timeIn, timeOut).toMinutes();

            // Deduct the standard 1-hour lunch break (60 minutes)
            workedMinutes -= 60;

            // Prevent negative worked hours after lunch deduction
            if (workedMinutes < 0) {
                return 0;
            }

            // Convert worked minutes into hours (decimal format)
            return workedMinutes / 60.0;

        } catch (Exception e) {

            // If any parsing or computation error occurs, return 0 hours
            return 0;
        }
    }

    // ================= EMPLOYEE MENU =================
    // ================= EMPLOYEE MENU =================

    // Method that displays the employee menu
    // Allows employees to enter their employee number and view basic information
    static void employeeMenu() {

        // Infinite loop so the menu keeps appearing until the user chooses to exit
        while (true) {

            // Display employee menu options
            System.out.println("\n1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.print("Choice: ");

            // Read the user's menu choice
            String c = scanner.nextLine();

            // ===== OPTION 1: ENTER EMPLOYEE NUMBER =====
            if (c.equals("1")) {

                // Ask the user to input their employee ID
                System.out.print("Enter employee number: ");
                String id = scanner.nextLine();

                // Check if the employee ID exists in the employee records
                if (!employees.containsKey(id)) {

                    // If the employee ID is not found, display an error message
                    System.out.println("Employee number does not exist.");

                } else {

                    // Retrieve the employee data from the employees map
                    String[] data = employees.get(id);

                    // Display employee information
                    System.out.println("Employee Number: " + id);
                    System.out.println("Employee Name: " + data[0]);
                    System.out.println("Birthday: " + data[1]);
                }

                // ===== OPTION 2: EXIT PROGRAM =====
            } else {

                // Exit the employee menu and return to the previous program flow
                return;
            }
        }
    }

    // ================= PAYROLL MENU =================

    // Method that displays the payroll staff menu
    // This allows the payroll user to process payroll or exit the system
    public static void payrollMenu() {

        // Infinite loop so the menu keeps appearing until the user chooses to exit
        while (true) {

            // Display the payroll menu options
            System.out.println("\n1. Process Payroll");
            System.out.println("2. Exit the program");
            System.out.print("Choice: ");

            // Read the user's menu choice
            String c = scanner.nextLine();

            // If the user selects option 1, start the payroll processing menu
            if (c.equals("1")) {
                processPayroll();

                // Any other input exits the payroll menu and returns to the previous program flow
            } else {
                return;
            }
        }
    }

    // Method that allows the payroll staff to process payroll
    // It gives options to process payroll for one employee or all employees
    public static void processPayroll() {

        // Infinite loop to keep the payroll menu running until the user chooses to exit
        while (true) {

            // Display payroll processing options
            System.out.println("\n1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            // Read the user's menu choice
            String c = scanner.nextLine();

            // ===== OPTION 1: PROCESS PAYROLL FOR ONE EMPLOYEE =====
            if (c.equals("1")) {

                // Ask the user to input an employee number
                System.out.print("Enter employee number: ");
                String id = scanner.nextLine();

                // Check if the entered employee ID exists in the employee records
                if (!employees.containsKey(id)) {

                    // If employee ID is not found, show an error message
                    System.out.println("Employee number does not exist.");

                } else {

                    // If employee exists, display that employee's payroll information
                    displayPayroll(id);
                }

                // ===== OPTION 2: PROCESS PAYROLL FOR ALL EMPLOYEES =====
            } else if (c.equals("2")) {

                // Loop through all employee IDs stored in the employees map
                for (String id : employees.keySet()) {

                    // Display payroll details for each employee
                    displayPayroll(id);

                    // Print a separator line between employee payroll outputs
                    System.out.println("------------------------------------");
                }

                // ===== OPTION 3: EXIT PAYROLL PROCESSING =====
            } else {

                // Exit the payroll processing menu and return to the previous menu
                return;
            }
        }
    }

    // Method to compute the employee's SSS contribution
    // The contribution depends on the employee's monthly gross salary
    // Values are based on the SSS contribution table where each salary range
    // corresponds to a fixed employee contribution amount
    public static double computeSSSContribution(double monthlyGross) {

        // Check which salary bracket the employee falls under
        // and return the corresponding SSS employee contribution

        if (monthlyGross < 3250) return 135.00;        // Salary below 3,250
        else if (monthlyGross < 3750) return 157.50;   // 3,250 – 3,749.99
        else if (monthlyGross < 4250) return 180.00;   // 3,750 – 4,249.99
        else if (monthlyGross < 4750) return 202.50;   // 4,250 – 4,749.99
        else if (monthlyGross < 5250) return 225.00;   // 4,750 – 5,249.99
        else if (monthlyGross < 5750) return 247.50;   // 5,250 – 5,749.99
        else if (monthlyGross < 6250) return 270.00;   // 5,750 – 6,249.99
        else if (monthlyGross < 6750) return 292.50;   // 6,250 – 6,749.99
        else if (monthlyGross < 7250) return 315.00;   // 6,750 – 7,249.99
        else if (monthlyGross < 7750) return 337.50;   // 7,250 – 7,749.99
        else if (monthlyGross < 8250) return 360.00;   // 7,750 – 8,249.99
        else if (monthlyGross < 8750) return 382.50;   // 8,250 – 8,749.99
        else if (monthlyGross < 9250) return 405.00;   // 8,750 – 9,249.99
        else if (monthlyGross < 9750) return 427.50;   // 9,250 – 9,749.99
        else if (monthlyGross < 10250) return 450.00;  // 9,750 – 10,249.99
        else if (monthlyGross < 10750) return 472.50;  // 10,250 – 10,749.99
        else if (monthlyGross < 11250) return 495.00;  // 10,750 – 11,249.99
        else if (monthlyGross < 11750) return 517.50;  // 11,250 – 11,749.99
        else if (monthlyGross < 12250) return 540.00;  // 11,750 – 12,249.99
        else if (monthlyGross < 12750) return 562.50;  // 12,250 – 12,749.99
        else if (monthlyGross < 13250) return 585.00;  // 12,750 – 13,249.99
        else if (monthlyGross < 13750) return 607.50;  // 13,250 – 13,749.99
        else if (monthlyGross < 14250) return 630.00;  // 13,750 – 14,249.99
        else if (monthlyGross < 14750) return 652.50;  // 14,250 – 14,749.99
        else if (monthlyGross < 15250) return 675.00;  // 14,750 – 15,249.99
        else if (monthlyGross < 15750) return 697.50;  // 15,250 – 15,749.99
        else if (monthlyGross < 16250) return 720.00;  // 15,750 – 16,249.99
        else if (monthlyGross < 16750) return 742.50;  // 16,250 – 16,749.99
        else if (monthlyGross < 17250) return 765.00;  // 16,750 – 17,249.99
        else if (monthlyGross < 17750) return 787.50;  // 17,250 – 17,749.99
        else if (monthlyGross < 18250) return 810.00;  // 17,750 – 18,249.99
        else if (monthlyGross < 18750) return 832.50;  // 18,250 – 18,749.99
        else if (monthlyGross < 19250) return 855.00;  // 18,750 – 19,249.99
        else if (monthlyGross < 19750) return 877.50;  // 19,250 – 19,749.99
        else if (monthlyGross < 20250) return 900.00;  // 19,750 – 20,249.99
        else if (monthlyGross < 20750) return 922.50;  // 20,250 – 20,749.99
        else if (monthlyGross < 21250) return 945.00;  // 20,750 – 21,249.99
        else if (monthlyGross < 21750) return 967.50;  // 21,250 – 21,749.99
        else if (monthlyGross < 22250) return 990.00;  // 21,750 – 22,249.99
        else if (monthlyGross < 22750) return 1012.50; // 22,250 – 22,749.99
        else if (monthlyGross < 23250) return 1035.00; // 22,750 – 23,249.99
        else if (monthlyGross < 23750) return 1057.50; // 23,250 – 23,749.99
        else if (monthlyGross < 24250) return 1080.00; // 23,750 – 24,249.99
        else if (monthlyGross < 24750) return 1102.50; // 24,250 – 24,749.99

            // If salary is 24,750 or above, the maximum employee contribution applies
        else return 1125.00;
    }

    // Method to compute the employee's Pag-IBIG contribution
    // The contribution depends on the employee's monthly gross salary
    public static double computePagibigContribution(double monthlyGross) {

        // Determine the employee contribution rate
        // If salary is 1,500 or below → 1% contribution
        // If salary is above 1,500 → 2% contribution
        double employeeRate = (monthlyGross <= 1500) ? 0.01 : 0.02;

        // Compute the employee share based on the salary and rate
        double employeeShare = monthlyGross * employeeRate;

        // Pag-IBIG has a maximum employee contribution of PHP 100
        // because the total contribution cap is PHP 200
        // (split equally between employee and employer)
        if (employeeShare > 100) {
            employeeShare = 100;
        }

        // Return the employee's Pag-IBIG contribution
        return employeeShare;
    }

    // Method to compute the employee's PhilHealth contribution
    // Based on the monthly gross salary of the employee
    public static double computePhilHealthContribution(double monthlyGross) {

        // PhilHealth premium rate (3% of monthly salary for 2024)
        double premiumRate = 0;

        // Minimum salary floor used for PhilHealth computation
        double minSalary = 10000;

        // Maximum salary ceiling used for PhilHealth computation
        double maxSalary = 60000;

        // ===== Apply salary floor and ceiling rules =====

        // If salary is below the minimum salary floor
        // Use the minimum rate for contribution calculation
        if (monthlyGross < minSalary) premiumRate = .03;

            // If salary is between the minimum and maximum salary limits
            // Apply the same 3% premium rate
        else if (monthlyGross < maxSalary && monthlyGross > minSalary) {
            premiumRate = .03;

            // If salary exceeds the maximum salary ceiling
            // The same rate applies but contribution is based on the ceiling
        } else if (monthlyGross > maxSalary) {
            premiumRate = .03;
        }

        // Compute the total PhilHealth premium
        // Premium = monthly salary × premium rate
        double totalPremium = monthlyGross * premiumRate;

        // Return only the employee share (50% of the total premium)
        // because the employer pays the other half
        return totalPremium / 2;
    }

    // Method that computes the withholding tax based on the employee's taxable income
    // The tax rates follow the Philippine TRAIN Law tax table
    public static double computeWithholdingTax(double taxableIncome) {

        // Variable to store the computed tax
        double tax;

        // If taxable income is 20,832 or below → no tax
        if (taxableIncome <= 20832) {
            tax = 0;

            // If income is between 20,833 and 33,332
            // Tax = 20% of the excess over 20,833
        } else if (taxableIncome <= 33332) {
            tax = (taxableIncome - 20833) * 0.20;

            // If income is between 33,333 and 66,666
            // Base tax = 2,500 + 25% of excess over 33,333
        } else if (taxableIncome <= 66666) {
            tax = 2500 + (taxableIncome - 33333) * 0.25;

            // If income is between 66,667 and 166,666
            // Base tax = 10,833 + 30% of excess over 66,667
        } else if (taxableIncome <= 166666) {
            tax = 10833 + (taxableIncome - 66667) * 0.30;

            // If income is between 166,667 and 666,666
            // Base tax = 40,833.33 + 32% of excess over 166,667
        } else if (taxableIncome <= 666666) {
            tax = 40833.33 + (taxableIncome - 166667) * 0.32;

            // If income is above 666,667
            // Base tax = 200,833.33 + 35% of the excess over 666,667
        } else {
            tax = 200833.33 + (taxableIncome - 666667) * 0.35;
        }

        // Return the calculated withholding tax
        return tax;
    }

    // ================= DISPLAY PAYROLL =================
    // Method responsible for displaying the payroll details of a specific employee
    static void displayPayroll(String id) {

        // Retrieve the employee information from the employees map using the employee ID
        String[] emp = employees.get(id);

        // Convert the hourly rate (stored as String in the CSV) into a double for salary calculations
        double hourly = Double.parseDouble(emp[2]);

        // ===== PAYROLL HEADER =====

        // Print a title section for the payroll summary
        System.out.println("\n=================================================");
        System.out.println("                 PAYROLL SUMMARY                 ");
        System.out.println("=================================================");

        // Display the employee ID
        System.out.printf("Employee ID   : %s\n", id);

        // Display the employee full name
        System.out.printf("Employee Name : %s\n", emp[0]);

        // Display the employee birthday
        System.out.printf("Birthday      : %s\n", emp[1]);

        // Print separator line before the payroll details begin
        System.out.println("=================================================");

        // Array containing the months processed by the payroll system
        // The system only processes months from June to December
        String[] months = {"June","July","August","September","October","November","December"};

        // Loop through each payroll month from June to December
        // m represents the index of the months array (0 = June, 6 = December)
        for (int m = 0; m < 7; m++) {

            // Get the correct month in the year 2024 starting from June (6)
            // m starts at 0 in the loop, so m + 6 converts it to real month numbers (6–12)
            YearMonth ym = YearMonth.of(2024, m + 6);

            // Get the actual last day of the month (30 or 31 depending on the month)
            int lastDay = ym.lengthOfMonth();

            // Retrieve total worked hours for the first cutoff (days 1–15)
            double first = monthlyHours.get(id)[m * 2];

            // Retrieve total worked hours for the second cutoff (days 16–end of month)
            double second = monthlyHours.get(id)[m * 2 + 1];

            // Compute gross salary for the first cutoff
            // Gross salary = worked hours × hourly rate
            double gross1 = first * hourly;

            // Compute gross salary for the second cutoff
            double gross2 = second * hourly;

            // Compute total gross salary for the entire month
            double monthlyGross = gross1 + gross2;

            // Compute government contributions based on monthly gross salary

            // Social Security System (SSS) contribution
            double sss = computeSSSContribution(monthlyGross);

            // Pag-IBIG housing fund contribution
            double pagibig = computePagibigContribution(monthlyGross);

            // PhilHealth health insurance contribution
            double phil = computePhilHealthContribution(monthlyGross);


            // Compute taxable income after mandatory contributions are deducted
            double taxableIncome = monthlyGross - sss - phil - pagibig;

            // Compute withholding tax based on taxable income
            double tax = computeWithholdingTax(taxableIncome);


            // Compute the total deductions applied to the payroll
            double totalDeduction = sss + phil + pagibig + tax;


            // Compute net salary for the second cutoff after deductions
            double net2 = gross2 - totalDeduction;


            // Compute the final net salary for the entire month
            // First cutoff has no deductions, second cutoff includes deductions
            double totalNet = gross1 + net2;


            // Print the payroll header for the current month
            // %-10s aligns the month name to the left within 10 spaces
            System.out.printf("\n%-10s Payroll\n", months[m]);

            // Print a separator line for readability
            System.out.println("---------------------------------------------");

            // ===== FIRST CUTOFF (DAY 1–15) =====

            // Display the cutoff period for the first half of the month
            System.out.printf("CutOff Date: %s 1 to 15\n", months[m]);

            // Display the total worked hours from day 1 to 15
            System.out.printf("1-15 Worked Hours      : %f hrs\n", first);

            // Display the gross salary for the first cutoff
            // Gross = worked hours × hourly rate
            System.out.printf("1-15 Gross Salary      : PHP %f\n", gross1);

            // Display the net salary for the first cutoff
            // In this implementation deductions are applied in the second cutoff
            System.out.printf("1-15 Net Salary        : PHP %f\n", gross1);

            // Print a blank line to separate the two cutoffs
            System.out.println();


            // ===== SECOND CUTOFF (DAY 16–END OF MONTH) =====

            // Display the second cutoff period
            // lastDay automatically becomes 30 or 31 depending on the month
            System.out.printf("CutOff Date: %s 16 to %d\n", months[m], lastDay);

            // Display the total worked hours from day 16 to the last day
            System.out.printf("16-%d Worked Hours     : %f hrs\n", lastDay, second);

            // Display the gross salary for the second cutoff
            System.out.printf("16-%d Gross Salary     : PHP %f\n", lastDay, gross2);

            // Display the net salary before deductions
            System.out.printf("16-%d Net Salary       : PHP %f\n", lastDay, gross2);


            // ===== DEDUCTIONS SECTION =====

            // Show the list of mandatory payroll deductions
            System.out.println("Each Deductions");

            // Social Security System contribution
            System.out.printf("    SSS                : PHP %f\n", sss);

            // PhilHealth insurance contribution
            System.out.printf("    PhilHealth         : PHP %f\n", phil);

            // Pag-IBIG housing fund contribution
            System.out.printf("    Pag-IBIG           : PHP %f\n", pagibig);

            // Government withholding tax
            System.out.printf("    Withholding Tax    : PHP %f\n", tax);

            // Total of all deductions combined
            System.out.printf("Total Deduction        : PHP %f\n", totalDeduction);


            // ===== FINAL NET SALARY =====

            // Print another separator line
            System.out.println("---------------------------------------------");

            // Display the final net salary after all deductions
            System.out.printf("Net Salary             : PHP %f\n", totalNet);

            // Print closing line for the payroll section
            System.out.println("=================================================");
        }
    }
}


