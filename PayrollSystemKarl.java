import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PayrollSystem {

        // using scanner for Login Input of username and password
    static Scanner scanner = new Scanner(System.in);

        // mapping to map same values from employee and attendance record
    static Map<String, String[]> employees = new HashMap<>();
    static Map<String, double[]> monthlyHours = new HashMap<>();
        // double[7][2] → months June(0) to December(6), 0=1st cutoff, 1=2nd cutoff

    public static void main(String[] args) {

        // CSV files being imported and assigned to a variable
        loadEmployees();
        loadAttendance();

        // ================= LOGIN SYSTEM =================
        System.out.println("=== Payroll System Login ===");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        // Validate login credentials
        if (!(username.equals("employee") || username.equals("payroll_staff")) || !password.equals("12345")) {
            System.out.println("Incorrect username and/or password.");
            return;
        }
        // Redirect based on user type
        if (username.equals("employee")) {
            employeeMenu();
        } else {
            payrollMenu();
        }
    }

    // ================= LOAD EMPLOYEE DATA =================
    static void loadEmployees() {

        try (BufferedReader br = new BufferedReader(new FileReader("MotorPH_Employee Data (IT101 - H1101 - Group 20)  - Employee Details.csv"))) {

            br.readLine();// Skip header row
            String line;

            while ((line = br.readLine()) != null) {

                // Split CSV correctly (handles quoted commas)
                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // Assign Employee number
                String empNo = f[0].trim();

                // Combine last name + first name
                String name = f[2].trim() + " " + f[1].trim();
                String birthday = f[3].trim();
                String hourly = f[18].replace("\"", "").replace(",", "").trim();

                // Store employee data
                employees.put(empNo, new String[]{name, birthday, hourly});
                // Initialize 14 slots (7 months × 2 cutoffs)
                monthlyHours.put(empNo, new double[14]);
            }

        } catch (Exception e) {
            System.out.println("Error loading employees.");
        }
    }

    // ================= LOAD ATTENDANCE =================
    static void loadAttendance() {

        try (BufferedReader br = new BufferedReader(new FileReader("MotorPH_Employee Data (IT101 - H1101 - Group 20)  - Attendance Record.csv"))) {

            br.readLine();// Skip header row
            String line;

            while ((line = br.readLine()) != null) {

                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1 );

                // Skip if employee does not exist
                String empNo = f[0].trim();
                if (!employees.containsKey(empNo)) continue;

                // Extract month and day from date
                String[] date = f[3].split("/");
                int month = Integer.parseInt(date[0]);
                int day = Integer.parseInt(date[1]);

                // Only process June (6) to December (12)
                if (month < 6 || month > 12) continue;

                double hours = computeHours(f[4], f[5]); // Compute worked hours
                int monthIndex = month - 6; // Start the index of months from 0 = June , 1 = July ...
                int cutoff = (day <= 15) ? 0 : 1; // 0 = first cutoff (1–15), 1 = second cutoff (16–30/31)

                monthlyHours.get(empNo)[monthIndex * 2 + cutoff] += hours; // Add hours to correct month and cutoff slot
            }
        } catch (Exception e) {
            System.out.println("Error loading attendance.");
        }
    }

    // ================= COMPUTE HOURS =================
    static double computeHours(String in, String out) {
        try {
            String[] inP = in.split(":");
            String[] outP = out.split(":");

            int inHour = Integer.parseInt(inP[0]);
            int inMin = Integer.parseInt(inP[1]);

            int outHour = Integer.parseInt(outP[0]);
            int outMin = Integer.parseInt(outP[1]);

            // Convert to total minutes
            int inTotalMin = inHour * 60 + inMin;
            int outTotalMin = outHour * 60 + outMin;

            // Working hours limits
            int workStart = 8 * 60;   // 8:00 AM
            int workEnd = 17 * 60;    // 5:00 PM

            // If time-in earlier than 8:00 → set to 8:00
            if (inTotalMin < workStart) {
                inTotalMin = workStart;
            }

            // If time-out later than 5:00 → set to 5:00
            if (outTotalMin > workEnd) {
                outTotalMin = workEnd;
            }

            // If invalid or no work
            if (inTotalMin >= outTotalMin) {
                return 0;
            }

            // ===== TARDINESS RULE =====
            // Grace period until 8:10 AM
            int graceLimit = (8 * 60) + 10; // 8:10 AM

            if (inTotalMin > graceLimit) {
                // Every minute after 8:11 AM is deducted automatically
                // No rounding — actual minutes late
                // So we just compute normally using actual inTotalMin
            } else {
                // Within grace period → treat as 8:00 AM
                inTotalMin = workStart;
            }

            // Compute worked minutes
            int workedMinutes = outTotalMin - inTotalMin;

            // Deduct 1 hour lunch (60 minutes)
            workedMinutes -= 60;

            // Prevent negative hours
            if (workedMinutes < 0) {
                return 0;
            }

            // Convert to hours (decimal)
            return workedMinutes / 60.0;

        } catch (Exception e) {
            return 0;
        }
    }

    // ================= EMPLOYEE MENU =================
    static void employeeMenu() {
        while (true) {
            System.out.println("\n1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.print("Choice: ");

            String c = scanner.nextLine();

            if (c.equals("1")) {
                System.out.print("Enter employee number: ");
                String id = scanner.nextLine();

                if (!employees.containsKey(id)) {
                    System.out.println("Employee number does not exist.");
                } else {
                    String[] data = employees.get(id);
                    System.out.println("Employee Number: " + id);
                    System.out.println("Employee Name: " + data[0]);
                    System.out.println("Birthday: " + data[1]);
                }
            } else {
                return;
            }
        }
    }

    // ================= PAYROLL MENU =================
    public static void payrollMenu() {
        while (true) {
            System.out.println("\n1. Process Payroll");
            System.out.println("2. Exit the program");
            System.out.print("Choice: ");

            String c = scanner.nextLine();

            if (c.equals("1")) {
                processPayroll();
            } else {
                return;
            }
        }
    }

    public static void processPayroll() {
        while (true) {
            System.out.println("\n1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            String c = scanner.nextLine();

            if (c.equals("1")) {
                System.out.print("Enter employee number: ");
                String id = scanner.nextLine();

                if (!employees.containsKey(id)) {
                    System.out.println("Employee number does not exist.");
                } else {
                    displayPayroll(id);
                }
            } else if (c.equals("2")) {
                for (String id : employees.keySet()) {
                    displayPayroll(id);
                    System.out.println("------------------------------------");
                }
            } else {
                return;
            }
        }
    }

    public static double computeSSSContribution(double monthlyGross) {

        if (monthlyGross < 3250) return 135.00;
        else if (monthlyGross < 3750) return 157.50;
        else if (monthlyGross < 4250) return 180.00;
        else if (monthlyGross < 4750) return 202.50;
        else if (monthlyGross < 5250) return 225.00;
        else if (monthlyGross < 5750) return 247.50;
        else if (monthlyGross < 6250) return 270.00;
        else if (monthlyGross < 6750) return 292.50;
        else if (monthlyGross < 7250) return 315.00;
        else if (monthlyGross < 7750) return 337.50;
        else if (monthlyGross < 8250) return 360.00;
        else if (monthlyGross < 8750) return 382.50;
        else if (monthlyGross < 9250) return 405.00;
        else if (monthlyGross < 9750) return 427.50;
        else if (monthlyGross < 10250) return 450.00;
        else if (monthlyGross < 10750) return 472.50;
        else if (monthlyGross < 11250) return 495.00;
        else if (monthlyGross < 11750) return 517.50;
        else if (monthlyGross < 12250) return 540.00;
        else if (monthlyGross < 12750) return 562.50;
        else if (monthlyGross < 13250) return 585.00;
        else if (monthlyGross < 13750) return 607.50;
        else if (monthlyGross < 14250) return 630.00;
        else if (monthlyGross < 14750) return 652.50;
        else if (monthlyGross < 15250) return 675.00;
        else if (monthlyGross < 15750) return 697.50;
        else if (monthlyGross < 16250) return 720.00;
        else if (monthlyGross < 16750) return 742.50;
        else if (monthlyGross < 17250) return 765.00;
        else if (monthlyGross < 17750) return 787.50;
        else if (monthlyGross < 18250) return 810.00;
        else if (monthlyGross < 18750) return 832.50;
        else if (monthlyGross < 19250) return 855.00;
        else if (monthlyGross < 19750) return 877.50;
        else if (monthlyGross < 20250) return 900.00;
        else if (monthlyGross < 20750) return 922.50;
        else if (monthlyGross < 21250) return 945.00;
        else if (monthlyGross < 21750) return 967.50;
        else if (monthlyGross < 22250) return 990.00;
        else if (monthlyGross < 22750) return 1012.50;
        else if (monthlyGross < 23250) return 1035.00;
        else if (monthlyGross < 23750) return 1057.50;
        else if (monthlyGross < 24250) return 1080.00;
        else if (monthlyGross < 24750) return 1102.50;
        else return 1125.00; // 24,750 and over
    }

    public static double computePagibigContribution(double monthlyGross) {

        double employeeRate = (monthlyGross <= 1500) ? 0.01 : 0.02;
        double employeeShare = monthlyGross * employeeRate;

        // Employee maximum share is 100 (because total max is 200 split equally at 2%+2%)
        if (employeeShare > 100) {
            employeeShare = 100;
        }

        return employeeShare;
    }

    public static double computePhilHealthContribution(double monthlyGross) {

        double premiumRate = 0;
        double minSalary = 10000;
        double maxSalary = 60000;

        // Apply salary floor and ceiling
        if (monthlyGross < minSalary) premiumRate = .03;

        else if (monthlyGross < maxSalary && monthlyGross > minSalary  ) {
            premiumRate = .03;
        } else if (monthlyGross > maxSalary) {
            premiumRate = .03;
        }

        double totalPremium = monthlyGross * premiumRate;

        return totalPremium / 2;
    }

    public static double computeWithholdingTax(double taxableIncome) {

        double tax;

        if (taxableIncome <= 20832) {
            tax = 0;

        } else if (taxableIncome <= 33332) {
            tax = (taxableIncome - 20833) * 0.20;

        } else if (taxableIncome <= 66666) {
            tax = 2500 + (taxableIncome - 33333) * 0.25;

        } else if (taxableIncome <= 166666) {
            tax = 10833 + (taxableIncome - 66667) * 0.30;

        } else if (taxableIncome <= 666666) {
            tax = 40833.33 + (taxableIncome - 166667) * 0.32;

        } else {
            tax = 200833.33 + (taxableIncome - 666667) * 0.35;
        }

        return tax;
    }


    // ================= DISPLAY PAYROLL =================
    static void displayPayroll(String id) {

        String[] emp = employees.get(id);
        double hourly = Double.parseDouble(emp[2]);

        System.out.println("\n=================================================");
        System.out.println("                 PAYROLL SUMMARY                 ");
        System.out.println("=================================================");
        System.out.printf("Employee ID   : %s\n", id);
        System.out.printf("Employee Name : %s\n", emp[0]);
        System.out.printf("Birthday      : %s\n", emp[1]);
        System.out.println("=================================================");

        String[] months = {"June","July","August","September","October","November","December"};

        for (int m = 0; m < 7; m++) {

            double first = monthlyHours.get(id)[m * 2];
            double second = monthlyHours.get(id)[m * 2 + 1];
            double gross1 = first * hourly;
            double gross2 = second * hourly;
            double monthlyGross = gross1 + gross2;
            double sss = computeSSSContribution(monthlyGross);
            double pagibig = computePagibigContribution(monthlyGross);
            double phil = computePhilHealthContribution(monthlyGross);
            double taxableIncome = monthlyGross - sss - phil - pagibig;
            double tax = computeWithholdingTax(taxableIncome);
            double totalDeduction = sss + phil + pagibig + tax;
            double net2 = gross2 - totalDeduction;
            double totalNet = gross1 + net2;

            System.out.printf("\n%-10s Payroll\n", months[m]);
            System.out.println("---------------------------------------------");

            System.out.println("                1st CutOff");
            System.out.printf("1-15 Worked Hours      : %8.2f hrs\n", first);
            System.out.printf("1-15 Gross             : %12.2f\n", gross1);

            System.out.println("                2nd CutOff");
            System.out.printf("16-30 Worked Hours     : %8.2f hrs\n", second);
            System.out.printf("16-30 Gross            : %12.2f\n", gross2);

            System.out.println("                Deductions");
            System.out.printf("SSS                    : %12.2f\n", sss);
            System.out.printf("PhilHealth             : %12.2f\n", phil);
            System.out.printf("Pag-IBIG               : %12.2f\n", pagibig);
            System.out.printf("Withholding Tax        : %12.2f\n", tax);

            System.out.println("---------------------------------------------");
            System.out.printf("Monthly Gross          : %12.2f\n", monthlyGross);
            System.out.printf("Total Deduction        : %12.2f\n", totalDeduction);
            System.out.printf("Net Salary             : %12.2f\n", totalNet);
            System.out.println("=================================================");
        }
    }
}
