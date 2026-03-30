/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.milestone2group20;

// Import required java classes.
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/*
 *
 * @author Group 20 - IT101 - H1101
 *         Members:
 *          1. APARECE, Karl Franklyn
 *          2. FALCULAN, Rina Mae
 *          3. GERTOS, Harry
 *          4. TAPERE, Lady Mae
 *
 */

public class Milestone2group20 {

    // Scanner for reading user input.
    static Scanner scanner = new Scanner(System.in);

    // This map stores employee info using their employee number as the key.
    static Map<String, String[]> employees = new LinkedHashMap<>();

    /**
     * Stores worked hours per employee per YearMonth.
     * Key   : Employee ID
     * Value : Map<YearMonth, double[2]> → [Cutoff1, Cutoff2]
     */
    static Map<String, Map<YearMonth, double[]>> monthlyHours = new LinkedHashMap<>();

    // File paths for the CSV files.
    static final String EMPLOYEE_FILE = "resources/employee_details.csv";
    static final String ATTENDANCE_FILE = "resources/attendance_record.csv";

    // This is the main method.
    public static void main(String[] args) {

        // Load all employee data and attendance records.
        loadEmployees();
        loadAttendance();

        // Start login screen.
        loginSystem();
    }

    /**
     * ================= LOGIN =================
     * This method handles the login for both employee and payroll staff users.
     */
    static void loginSystem() {

        System.out.println("========[ MotorPH Payroll System - Group 20 ]========");

        // Ask for username and password
        System.out.print("Enter your Username: ");
        String userName = scanner.nextLine().trim();

        System.out.print("Enter your Password: ");
        String passWord = scanner.nextLine().trim();

        // Check if the username is valid and if the password matches.
        if (!(userName.equals("employee") || userName.equals("payroll_staff")) || !passWord.equals("12345")) {

            System.out.println("Incorrect username and/or password.");
            return; // Display error message if username or password is incorrect
        }

        // Proceed to the correct menu based on role.
        if (userName.equals("employee")) {
            employeeMenu();
        } else {
            payrollMenu();
        }
    }

    /**
     * ================= LOAD EMPLOYEES =================
     * Loads employee data and initializes storage for payroll tracking.
     */
    static void loadEmployees() {

        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {

            br.readLine(); // Skip the header row/first line of the CSV.
            String line;

            // Read the file line by line.
            while ((line = br.readLine()) != null) {

                // Using regex for parsing CSV (ignoring commas in quotes).
                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                 // Get the employee number, name, and birthday from the column in CSV file.
                String employeeId = f[0].trim();
                String employeeName = f[2].trim() + " " + f[1].trim(); // First name + Last name.
                String employeeBirthday = f[3].trim();

                // Hourly rate is in column 18 (from 0-18), remove quotes and commas to get the number.
                String hourly = f[18]
                        .replace("\"", "")
                        .replace(",", "")
                        .trim();

                // Store the employee info in the map with their ID as the key.
                employees.put(employeeId, new String[]{employeeName, employeeBirthday, hourly});

                // Initialize dynamic YearMonth storage
                monthlyHours.put(employeeId, new LinkedHashMap<>());
            }

        } catch (Exception e) {
            System.out.println("Error loading employee data.");
        }
    }

    /**
     * ================= LOAD ATTENDANCE =================
     * This method reads the attendance_record.csv file.
     */
    static void loadAttendance() {

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {

            br.readLine(); // Skip the header row
            String line;

            while ((line = br.readLine()) != null) {

                // Same regex in loadEmployees method.
                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                String employeeId = f[0].trim();

                // Skip this row if the employee is not in our employee list.
                if (!employees.containsKey(employeeId)) continue;

                // Parse date MM/DD/YYYY */
                String[] date = f[3].split("/");

                int month = Integer.parseInt(date[0]);
                int day = Integer.parseInt(date[1]);
                int year = Integer.parseInt(date[2]);

                // Create YearMonth key */
                YearMonth ym = YearMonth.of(year, month);

                double hours = computeHours(f[4], f[5]);

                Map<YearMonth, double[]> empMap = monthlyHours.get(employeeId);

                // Initialize if not existing */
                empMap.putIfAbsent(ym, new double[2]);

                int cutoff = (day <= 15) ? 0 : 1;

                empMap.get(ym)[cutoff] += hours;
            }

        } catch (Exception e) {
            System.out.println("Error loading attendance data.");
        }
    }

    /**
     * ================= COMPUTE HOURS =================
     * This method calculates the number of hours worked for the day based on the login/logout from the attendance record.
     */
    static double computeHours(String in, String out) {

        try {

            // Parse the time strings (e.g. "8:05", "17:00") into LocalTime objects.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

            LocalTime timeIn = LocalTime.parse(in, formatter);
            LocalTime timeOut = LocalTime.parse(out, formatter);

            // Define the work schedule boundaries.
            LocalTime startWork = LocalTime.of(8, 0);   // Work starts at 8:00 AM.
            LocalTime endWork = LocalTime.of(17, 0);     // Work ends at 5:00 PM.
            LocalTime gracePeriod = LocalTime.of(8, 10);   // Grace period until 8:10 AM.

            // If employee came in before 8:00 AM, we only count from 8:00.
            if (timeIn.isBefore(startWork)) timeIn = startWork;

            // If employee logged out after 5:00 PM, we only count up to 5:00.
            if (timeOut.isAfter(endWork)) timeOut = endWork;

            // Apply grace period.
            if (!timeIn.isAfter(gracePeriod)) timeIn = startWork;

            // If time out is not after time in, something is wrong so return 0.
            if (!timeOut.isAfter(timeIn)) return 0;

            // Calculate total minutes between adjusted time-in and time-out.
            long minutes = Duration.between(timeIn, timeOut).toMinutes();

            // Deduct 1 hour (60 minutes) for unpaid lunch break but only if they worked more than 1 hour total.
            if (minutes > 60) {
                minutes -= 60;
            } else {
                minutes = 0;
            }

            // Convert minutes to hours (as a decimal).
            return minutes / 60.0;

        } catch (Exception e) {
            return 0; // Return 0 if there's any error parsing the time.
        }
    }
    /** ================= INPUT VALIDATION =================
     * Reads and validates menu input.
     * Ensures the user enters a valid numeric choice within range.
     *
     * @param min minimum valid option
     * @param max maximum valid option
     * @return valid integer choice
     */
    static int getValidatedChoice(int min, int max) {

        while (true) {
            /**
            *Prompt user for input
            * Read input as String to safely handle invalid entries (example: letters)
            */
            String input = scanner.nextLine();

            try {
                /**
                 * Attempt to convert input into an integer.
                 * If input is not numeric, this will throw NumberFormatException.
                 */
                int choice = Integer.parseInt(input);

                // Check if the input is within the allowed range.
                if (choice >= min && choice <= max) {
                     // Valid input → return the value
                    return choice;
                } else {
                    /**
                     * Input is numeric but outside valid range.
                     * Inform the user and loop again.
                     */
                    System.out.println("Invalid choice. Please select between " + min + " and " + max + ".");
                    System.out.print("\nChoice: ");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                System.out.print("\nChoice: ");
            }
        }
    }

    /**
     * ================= EMPLOYEE MENU =================
     * This menu is shown when an employee logs in.
     */
    static void employeeMenu() {

        // while loop keeps showing the menu until the user picks Exit (2).
        while (true) {

            System.out.println("\n========[ Employee Menu ]========");
            System.out.println("\n1. View Employee Details");
            System.out.println("2. Exit");
            System.out.print("\nChoice: ");

            int choice = getValidatedChoice(1, 2);

            if (choice == 1) {

                // Ask for employee number.
                System.out.print("Employee Number: ");
                String id = scanner.nextLine();

                if (!employees.containsKey(id)) {

                    System.out.println("Employee number does not exist.");

                } else {

                    // Get the employee info and display it.
                    String[] e = employees.get(id);

                    System.out.println("\n========[ Employee Details ]========");
                    System.out.println("\nEmployee Number: " + id);
                    System.out.println("Employee Name: " + e[0]);
                    System.out.println("Birthday: " + e[1]);
                }

            }  else if (choice == 2) {
                return; // Exit the menu.
            } else {
                System.out.println("Wrong choice. Please try again.");
            }
        }
    }

    /**
     * ================= PAYROLL MENU =================
     * This menu is shown when payroll staff logs in.
     */
    static void payrollMenu() {
        while (true) {
            System.out.println("\n========[ Payroll Staff Menu ]========");
            System.out.println("\n1. Process Payroll");
            System.out.println("2. Exit");
            System.out.print("\nChoice: ");

            int choice = getValidatedChoice(1, 2);

            if (choice == 1) {
                processPayrollSubMenu(); // Goes to the sub-menu
            } else if (choice == 2) {
                return;
            }
        }
    }

    static void processPayrollSubMenu() {
        while (true) {
            System.out.println("\n========[ Process Payroll ]========");
            System.out.println("\n1. Process ONE Employee");
            System.out.println("2. Process ALL Employees");
            System.out.println("3. Exit");
            System.out.print("\nChoice: ");

            int choice = getValidatedChoice(1, 3);

            if (choice == 1) {
                System.out.print("Employee Number: ");
                String id = scanner.nextLine();
                if (!employees.containsKey(id)) {
                    System.out.println("Employee number does not exist.");
                } else {
                    displayPayroll(id);
                }
                return; // Return after processing
            } else if (choice == 2) {
                for (String id : employees.keySet()) {
                    displayPayroll(id);
                    System.out.println("----------------------------------");
                }
                return; // Return after processing
            } else if (choice == 3) {
                return;
            }
        }
    }

    /**
     * ================= DISPLAY PAYROLL =================
     * Supports all months and multiple years dynamically.
     * This method displays the complete payroll of one employee
     */
    static void displayPayroll(String id) {

        String[] employee = employees.get(id);

        // Get the hourly rate and convert it from String to double for calculations.
        double hourly = Double.parseDouble(employee[2]);

        System.out.println("\n=======================================");
        System.out.println("Employee Number: " + id);
        System.out.println("Employee Name: " + employee[0]);
        System.out.println("Employee Birthday: " + employee[1]);
        System.out.println("=======================================");

        Map<YearMonth, double[]> empData = monthlyHours.get(id);

        // Sort by YearMonth */
        for (YearMonth ym : new java.util.TreeMap<>(empData).keySet()) {

            String monthName = ym.getMonth().toString().charAt(0)
                    + ym.getMonth().toString().substring(1).toLowerCase();

            int year = ym.getYear();
            int lastDay = ym.lengthOfMonth();

            // Get hours for 1st cutoff (days 1-15) and 2nd cutoff (days 16-end).
            double firstCutoff = empData.get(ym)[0];
            double secondCutoff = empData.get(ym)[1];

            // Calculate gross salary for each cutoff: hours worked x hourly rate.
            double gross1 = firstCutoff * hourly;
            double gross2 = secondCutoff * hourly;

            // Combine both cutoffs to get the total monthly gross salary. We need full monthly gross to compute govt deductions.
            double monthlyGross = gross1 + gross2;

            // Calculate government deductions based on the combined monthly gross.
            double sss = computeSSS(monthlyGross);
            double pagibig = computePagibig(monthlyGross);
            double philhealth = computePhilHealth(monthlyGross);

            // Taxable income = monthly gross minus the three government contributions.
            double taxable = monthlyGross - sss - pagibig - philhealth;

            // Calculate withholding tax based on the taxable income.
            double withholdTax = computeTax(taxable);

            // Add up all deductions.
            double totalDeduction = sss + pagibig + philhealth + withholdTax;

            // All deductions are taken from the 2nd cutoff salary.
            double net2 = gross2 - totalDeduction;

            // Display/print the variables for Cutoff 1.
            System.out.println("\n========== " + monthName + " " + year + " ==========");

            System.out.println("\nCutoff 1 - Date: " + monthName + " 1 to " + monthName + " 15");
            System.out.println("Total Hours Worked: " + firstCutoff);
            System.out.println("Gross Salary: " + gross1);
            System.out.println("Net Salary: " + gross1);

            // Display/print the variables for Cutoff 2.
            System.out.println("\nCutoff 2 - Date: " + monthName + " 16 to " + monthName + " " + lastDay);
            System.out.println("Total Hours Worked: " + secondCutoff);
            System.out.println("Gross Salary: " + gross2);

            System.out.println("Deductions:");
            System.out.println("  > SSS: " + sss);
            System.out.println("  > PhilHealth: " + philhealth);
            System.out.println("  > Pag-IBIG: " + pagibig);
            System.out.println("  > Tax: " + withholdTax);

            System.out.println("Total Deduction: " + totalDeduction);
            System.out.println("Net Salary: " + net2);

            System.out.println("\n=======================================");
        }
    }

    /**
     * ================= DEDUCTIONS =================
     * This method returns the SSS contribution based on the monthly gross salary (from MotorPH matrix).
     */
    static double computeSSS(double gross) {

        if (gross < 3250) return 135.00;
        else if (gross < 3750) return 157.50;
        else if (gross < 4250) return 180.00;
        else if (gross < 4750) return 202.50;
        else if (gross < 5250) return 225.00;
        else if (gross < 5750) return 247.50;
        else if (gross < 6250) return 270.00;
        else if (gross < 6750) return 292.50;
        else if (gross < 7250) return 315.00;
        else if (gross < 7750) return 337.50;
        else if (gross < 8250) return 360.00;
        else if (gross < 8750) return 382.50;
        else if (gross < 9250) return 405.00;
        else if (gross < 9750) return 427.50;
        else if (gross < 10250) return 450.00;
        else if (gross < 10750) return 472.50;
        else if (gross < 11250) return 495.00;
        else if (gross < 11750) return 517.50;
        else if (gross < 12250) return 540.00;
        else if (gross < 12750) return 562.50;
        else if (gross < 13250) return 585.00;
        else if (gross < 13750) return 607.50;
        else if (gross < 14250) return 630.00;
        else if (gross < 14750) return 652.50;
        else if (gross < 15250) return 675.00;
        else if (gross < 15750) return 697.50;
        else if (gross < 16250) return 720.00;
        else if (gross < 16750) return 742.50;
        else if (gross < 17250) return 765.00;
        else if (gross < 17750) return 787.50;
        else if (gross < 18250) return 810.00;
        else if (gross < 18750) return 832.50;
        else if (gross < 19250) return 855.00;
        else if (gross < 19750) return 877.50;
        else if (gross < 20250) return 900.00;
        else if (gross < 20750) return 922.50;
        else if (gross < 21250) return 945.00;
        else if (gross < 21750) return 967.50;
        else if (gross < 22250) return 990.00;
        else if (gross < 22750) return 1012.50;
        else if (gross < 23250) return 1035.00;
        else if (gross < 23750) return 1057.50;
        else if (gross < 24250) return 1080.00;
        else if (gross < 24750) return 1102.50;
        else return 1125.00; // Maximum contribution for salary ₱24,750 and above.
    }

    // This method computes the Pag-IBIG contribution (from MotorPH Matrix).
    static double computePagibig(double gross) {

        // Determine the employee contribution rate
        // If salary is 1,500 or below → 1% contribution
        // If salary is above 1,500 → 2% contribution
        double employeeRate = (gross <= 1500) ? 0.01 : 0.02;

        // Compute the employee share based on the salary and rate
        double employeeShare = gross * employeeRate;

        // Pag-IBIG has a maximum employee contribution of PHP 100
        // because the total contribution cap is PHP 200
        // (split equally between employee and employer)
        if (employeeShare > 100) {
            employeeShare = 100;
        }

        // Return the employee's Pag-IBIG contribution
        return employeeShare;
    }

    // This method computes the PhilHealth employee premium (from MotorPH matrix).
    static double computePhilHealth(double monthlyGross) {
        double premiumRate = 0.03;
        double totalPremium = monthlyGross * premiumRate;
        return totalPremium / 2;
    }
    
    // This method computes the withholding tax based on taxable income.
    static double computeTax(double taxable) {

        // No tax if taxable income is ₱20,832 or below.
        if (taxable <= 20832) return 0;

            // 20% of the amount over ₱20,833.
        else if (taxable <= 33332)
            return (taxable - 20833) * 0.20;

            // ₱2,500 + 25% in excess of ₱33,333.
        else if (taxable <= 66666)
            return 2500 + (taxable - 33333) * 0.25;

            // ₱10,833 + 30% in excess of ₱66,667.
        else if (taxable <= 166666)
            return 10833 + (taxable - 66667) * 0.30;

            // ₱40,833.33 + 32% in excess of ₱166,667.
        else if (taxable <= 666666)
            return 40833.33 + (taxable - 166667) * 0.32;

            // ₱200,833.33 + 35% in excess of ₱666,667.
        else
            return 200833.33 + (taxable - 666667) * 0.35;
    }
}

 /*
    //----------[Resources/References]----------//
    
    > Employee Details and Attendance Spreadsheet: https://docs.google.com/spreadsheets/d/189fvPCZS7JKMBYos00ZEoApqnBB05jAjy2vak6lD4g0/view
    > SSS computation matrix for method "calculateSSS": https://docs.google.com/spreadsheets/d/1g2gPgfqy1VNBAtnoJHLvdEA7m_GM9JOwZ22_uA-hpcU/view
    > PhilHealth computation matrix for method "calculatePhilHealth": https://docs.google.com/spreadsheets/d/16qXxY8K-DG8sm_QHtM4gHfHCNjhliNdSvUzF2yOz-bg/view
    > PagIbig computation matrix for method "calculatePagIbig": https://docs.google.com/spreadsheets/d/1cHwE0j3xplJcubD57hZf7R0Cih-n7urqO92KFVsciPs/view
    > Tax computation matrix for method "calculateTax": https://docs.google.com/spreadsheets/d/1mWxdCuYCmTd8n3DrNVxIb912xT8dWFCsQTUc2owv2UQ/view
    > Java Parser (CSV, double quotes/quotes fix): https://dev.to/sadiul_hakim/csv-quotes-explained-simply-with-java-example-5dom
                                                   https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
                                                   https://www.baeldung.com/java-split-string-commas
    > Java File Handling reading multiple records CSV files: https://www.youtube.com/watch?v=KI_a39BeCLU
                                                             https://www.youtube.com/watch?v=-Aud0cDh-J8
    > Java Hashmap: https://www.geeksforgeeks.org/java/java-util-hashmap-in-java-with-examples/
                    https://www.geeksforgeeks.org/java/reading-text-file-into-java-hashmap/
    > Coursera (scanner, operator, loops, file handling): https://www.coursera.org/learn/mo-it101/home/module/8
                                                          https://www.coursera.org/learn/mo-it101/home/module/9
                                                          https://www.coursera.org/learn/mo-it101/home/module/10
*/ 
