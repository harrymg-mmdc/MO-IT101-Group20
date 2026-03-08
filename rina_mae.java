//package motorphtask3to3.pkg2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class rina_mae {

    // -------------------------------------------------------
    // FILE PATHS — make sure these match your project folder
    // -------------------------------------------------------
    static String empFile = "resources/employee_details.csv";
    static String attFile = "resources/attendance_record.csv";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println(new java.io.File("MotorPH_Employee Data - Employee Details.csv").getAbsolutePath());

        // Ask user for employee number
        System.out.print("Enter Employee #: ");
        String inputEmpNo = sc.nextLine().trim();

        // -------------------------------------------------------
        // PART 1 & 2: Import employee data + display details
        // -------------------------------------------------------
        displayEmployeeDetails(inputEmpNo);

        // -------------------------------------------------------
        // PART 3: Read and display salary data per week
        // -------------------------------------------------------
        displayWeeklySalary(inputEmpNo);

        sc.close();
    }

    // ===========================================================
    // PART 1 & 2: Import Employee Data + Present Employee Details
    // ===========================================================
    static void displayEmployeeDetails(String inputEmpNo) {

        // These variables will store the employee's info
        String empNo        = "";
        String lastName     = "";
        String firstName    = "";
        String birthday     = "";
        String address      = "";
        String phone        = "";
        String sss          = "";
        String philhealth   = "";
        String tin          = "";
        String pagibig      = "";
        String status       = "";
        String position     = "";
        String supervisor   = "";
        String basicSalary  = "";
        String riceSubsidy  = "";
        String phoneAllow   = "";
        String clothingAllow= "";
        String semiMonthly  = "";
        String hourlyRate   = "";

        boolean found = false;

        // Open and read the Employee Details CSV
        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {

            br.readLine(); // Skip the header row (first line)

            String line;
            while ((line = br.readLine()) != null) {

                // Skip blank lines
                if (line.trim().isEmpty()) continue;

                // Split the line by comma
                // Note: some fields have commas inside quotes (e.g. salary "90,000")
                // so we use a simple split and clean up quotes
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Remove surrounding quotes from each field
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replace("\"", "").trim();
                }

                // Check if this row matches the employee number entered
                if (data[0].equals(inputEmpNo)) {
                    empNo         = data[0];
                    lastName      = data[1];
                    firstName     = data[2];
                    birthday      = data[3];
                    address       = data[4];
                    phone         = data[5];
                    sss           = data[6];
                    philhealth    = data[7];
                    tin           = data[8];
                    pagibig       = data[9];
                    status        = data[10];
                    position      = data[11];
                    supervisor    = data[12];
                    basicSalary   = data[13];
                    riceSubsidy   = data[14];
                    phoneAllow    = data[15];
                    clothingAllow = data[16];
                    semiMonthly   = data[17];
                    hourlyRate    = data[18];
                    found = true;
                    break; // Stop reading once we found the employee
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading employee file: " + e.getMessage());
            return;
        }

        // If no employee matched, stop here
        if (!found) {
            System.out.println("Employee #" + inputEmpNo + " not found.");
            return;
        }

        // Print a nice display of the employee's details
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         EMPLOYEE DETAILS                 ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf( "║  Employee #     : %-24s║%n", empNo);
        System.out.printf( "║  Name           : %-24s║%n", lastName + ", " + firstName);
        System.out.printf( "║  Birthday       : %-24s║%n", birthday);
        System.out.printf( "║  Phone          : %-24s║%n", phone);
        System.out.printf( "║  Status         : %-24s║%n", status);
        System.out.printf( "║  Position       : %-24s║%n", position);
        System.out.printf( "║  Supervisor     : %-24s║%n", supervisor);
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║         SALARY INFORMATION               ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf( "║  Basic Salary   : PHP %-20s║%n", basicSalary);
        System.out.printf( "║  Semi-monthly   : PHP %-20s║%n", semiMonthly);
        System.out.printf( "║  Hourly Rate    : PHP %-20s║%n", hourlyRate);
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║         ALLOWANCES                       ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf( "║  Rice Subsidy   : PHP %-20s║%n", riceSubsidy);
        System.out.printf( "║  Phone Allow.   : PHP %-20s║%n", phoneAllow);
        System.out.printf( "║  Clothing Allow.: PHP %-20s║%n", clothingAllow);
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║         GOVERNMENT IDs                   ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf( "║  SSS #          : %-24s║%n", sss);
        System.out.printf( "║  PhilHealth #   : %-24s║%n", philhealth);
        System.out.printf( "║  TIN #          : %-24s║%n", tin);
        System.out.printf( "║  Pag-IBIG #     : %-24s║%n", pagibig);
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ===========================================================
    // PART 3: Read and Display Salary Data Per Week
    // ===========================================================
    static void displayWeeklySalary(String empNo) {

        // We need the hourly rate to compute salary
        // First, get it from the employee file
        double hourlyRate = getHourlyRate(empNo);

        if (hourlyRate <= 0) {
            System.out.println("Could not load hourly rate for employee.");
            return;
        }

        // Time format used in the attendance file (e.g. "8:59")
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║              WEEKLY SALARY SUMMARY              ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // Loop through each month (June to December 2024)
        for (int month = 6; month <= 12; month++) {

            // We'll track hours per week
            // week1 = days 1-7, week2 = 8-14, week3 = 15-21, week4 = 22-end
            double[] weeklyHours = new double[4];

            // Read attendance file
            try (BufferedReader br = new BufferedReader(new FileReader(attFile))) {

                String line;
                while ((line = br.readLine()) != null) {

                    if (line.trim().isEmpty()) continue;

                    String[] data = line.split(",");

                    // Skip rows that don't belong to this employee
                    if (!data[0].trim().equals(empNo)) continue;

                    // Parse the date (format: MM/DD/YYYY)
                    String[] dateParts = data[3].trim().split("/");
                    int recordMonth = Integer.parseInt(dateParts[0]);
                    int day         = Integer.parseInt(dateParts[1]);
                    int year        = Integer.parseInt(dateParts[2]);

                    // Only process records from the correct month and year
                    if (year != 2024 || recordMonth != month) continue;

                    // Parse login and logout times
                    LocalTime login  = LocalTime.parse(data[4].trim(), timeFormat);
                    LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);

                    // Calculate hours worked that day
                    double hours = computeHours(login, logout);

                    // Add hours to the correct week bucket
                    if (day <= 7)       weeklyHours[0] += hours;
                    else if (day <= 14) weeklyHours[1] += hours;
                    else if (day <= 21) weeklyHours[2] += hours;
                    else                weeklyHours[3] += hours;
                }

            } catch (Exception e) {
                System.out.println("Error reading attendance file: " + e.getMessage());
                return;
            }

            // Get the month name for display
            String[] monthNames = {"", "January", "February", "March", "April",
                                   "May", "June", "July", "August",
                                   "September", "October", "November", "December"};
            String monthName = monthNames[month];

            System.out.println("\n  📅 " + monthName + " 2024");
            System.out.println("  ┌─────────────────────────────────────────────┐");
            System.out.printf( "  │  %-12s │ Hours: %5.2f │ Salary: PHP %,.2f │%n",
                "Week 1 (1-7)",   weeklyHours[0], weeklyHours[0] * hourlyRate);
            System.out.printf( "  │  %-12s │ Hours: %5.2f │ Salary: PHP %,.2f │%n",
                "Week 2 (8-14)",  weeklyHours[1], weeklyHours[1] * hourlyRate);
            System.out.printf( "  │  %-12s │ Hours: %5.2f │ Salary: PHP %,.2f │%n",
                "Week 3 (15-21)", weeklyHours[2], weeklyHours[2] * hourlyRate);
            System.out.printf( "  │  %-12s │ Hours: %5.2f │ Salary: PHP %,.2f │%n",
                "Week 4 (22+)",   weeklyHours[3], weeklyHours[3] * hourlyRate);

            double totalHours  = weeklyHours[0] + weeklyHours[1] + weeklyHours[2] + weeklyHours[3];
            double totalSalary = totalHours * hourlyRate;
            System.out.println("  ├─────────────────────────────────────────────┤");
            System.out.printf( "  │  %-12s │ Hours: %5.2f │ Salary: PHP %,.2f │%n",
                "TOTAL", totalHours, totalSalary);
            System.out.println("  └─────────────────────────────────────────────┘");
        }
    }

    // ===========================================================
    // HELPER: Get hourly rate from employee file
    // ===========================================================
    static double getHourlyRate(String empNo) {
        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replace("\"", "").trim();
                }
                if (data[0].equals(empNo)) {
                    return Double.parseDouble(data[18]); // Hourly Rate column
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting hourly rate: " + e.getMessage());
        }
        return 0;
    }

    // ===========================================================
    // HELPER: Calculate hours worked (from your professor's logic)
    // ===========================================================
    static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime graceTime  = LocalTime.of(8, 10);  // 8:10 AM grace period
        LocalTime cutoffTime = LocalTime.of(17, 0);  // 5:00 PM cutoff

        // Cap logout at 5 PM (no overtime counted)
        if (logout.isAfter(cutoffTime)) {
            logout = cutoffTime;
        }

        long minutesWorked = Duration.between(login, logout).toMinutes();

        // Subtract 1 hour for lunch break
        if (minutesWorked > 60) {
            minutesWorked -= 60;
        } else {
            minutesWorked = 0;
        }

        double hours = minutesWorked / 60.0;

        // If employee arrived on time (within grace period), count as full 8 hours
        if (!login.isAfter(graceTime)) {
            return 8.0;
        }

        // Cap at 8 hours max
        return Math.min(hours, 8.0);
    }

}

