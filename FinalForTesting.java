import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FinalForTesting {

    static Scanner scanner = new Scanner(System.in);

    // employeeNo -> [name, birthday, hourlyRate]
    static Map<String, String[]> employees = new HashMap<>();

    // employeeNo -> hours per month cutoff
    // June1, June2, July1, July2 ... December1, December2
    static Map<String, double[]> monthlyHours = new HashMap<>();

    static final String EMPLOYEE_FILE =
            "MotorPH_Employee Data (IT101 - H1101 - Group 20)  - Employee Details.csv";

    static final String ATTENDANCE_FILE =
            "MotorPH_Employee Data (IT101 - H1101 - Group 20)  - Attendance Record.csv";


    public static void main(String[] args) {

        loadEmployees();
        loadAttendance();

        loginSystem();
    }

    // ================= LOGIN =================

    static void loginSystem() {

        System.out.println("=== MotorPH Payroll System ===");

        System.out.print("Username: ");
        String user = scanner.nextLine().trim();

        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();

        if (!(user.equals("employee") || user.equals("payroll_staff"))
                || !pass.equals("12345")) {

            System.out.println("Incorrect username/password.");
            return;
        }

        if (user.equals("employee")) {
            employeeMenu();
        } else {
            payrollMenu();
        }
    }

    // ================= LOAD EMPLOYEES =================

    static void loadEmployees() {

        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {

            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {

                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                String id = f[0].trim();
                String name = f[2].trim() + " " + f[1].trim();
                String birthday = f[3].trim();

                String hourly = f[18]
                        .replace("\"", "")
                        .replace(",", "")
                        .trim();

                employees.put(id, new String[]{name, birthday, hourly});

                monthlyHours.put(id, new double[14]);
            }

        } catch (Exception e) {
            System.out.println("Error loading employee data.");
        }
    }

    // ================= LOAD ATTENDANCE =================

    static void loadAttendance() {

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {

            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {

                String[] f = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                String id = f[0].trim();

                if (!employees.containsKey(id)) continue;

                String[] date = f[3].split("/");

                int month = Integer.parseInt(date[0]);
                int day = Integer.parseInt(date[1]);

                if (month < 6 || month > 12) continue;

                double hours = computeHours(f[4], f[5]);

                int monthIndex = month - 6;

                int cutoff = (day <= 15) ? 0 : 1;

                monthlyHours.get(id)[monthIndex * 2 + cutoff] += hours;
            }

        } catch (Exception e) {
            System.out.println("Error loading attendance.");
        }
    }

    // ================= COMPUTE HOURS =================

    static double computeHours(String in, String out) {

        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

            LocalTime timeIn = LocalTime.parse(in, formatter);
            LocalTime timeOut = LocalTime.parse(out, formatter);

            LocalTime start = LocalTime.of(8,0);
            LocalTime end = LocalTime.of(17,0);
            LocalTime grace = LocalTime.of(8,10);

            if (timeIn.isBefore(start)) timeIn = start;
            if (timeOut.isAfter(end)) timeOut = end;

            if (!timeIn.isAfter(grace)) timeIn = start;

            if (!timeOut.isAfter(timeIn)) return 0;

            long minutes = Duration.between(timeIn, timeOut).toMinutes();

            minutes -= 60;

            if (minutes < 0) return 0;

            return minutes / 60.0;

        } catch (Exception e) {
            return 0;
        }
    }

    // ================= EMPLOYEE MENU =================

    static void employeeMenu() {

        while (true) {

            System.out.println("\n1. View Employee Details");
            System.out.println("2. Exit");
            System.out.print("Choice: ");

            String c = scanner.nextLine();

            if (c.equals("1")) {

                System.out.print("Employee Number: ");
                String id = scanner.nextLine();

                if (!employees.containsKey(id)) {

                    System.out.println("Employee not found.");

                } else {

                    String[] e = employees.get(id);

                    System.out.println("Employee Number: " + id);
                    System.out.println("Employee Name: " + e[0]);
                    System.out.println("Birthday: " + e[1]);
                }

            } else {
                return;
            }
        }
    }

    // ================= PAYROLL MENU =================

    static void payrollMenu() {

        while (true) {

            System.out.println("\n1. Process One Employee");
            System.out.println("2. Process All Employees");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            String c = scanner.nextLine();

            if (c.equals("1")) {

                System.out.print("Employee Number: ");
                String id = scanner.nextLine();

                if (!employees.containsKey(id)) {
                    System.out.println("Employee not found.");
                } else {
                    displayPayroll(id);
                }

            } else if (c.equals("2")) {

                for (String id : employees.keySet()) {

                    displayPayroll(id);

                    System.out.println("----------------------------------");
                }

            } else {
                return;
            }
        }
    }

    // ================= DISPLAY PAYROLL =================

    static void displayPayroll(String id) {

        String[] emp = employees.get(id);

        double hourly = Double.parseDouble(emp[2]);

        String[] months =
                {"June","July","August","September","October","November","December"};

        System.out.println("\n==============================");
        System.out.println("Employee: " + emp[0]);
        System.out.println("==============================");

        for (int m = 0; m < 7; m++) {

            YearMonth ym = YearMonth.of(2024, m + 6);
            int lastDay = ym.lengthOfMonth();

            double first = monthlyHours.get(id)[m*2];
            double second = monthlyHours.get(id)[m*2+1];

            double gross1 = first * hourly;
            double gross2 = second * hourly;

            double monthlyGross = gross1 + gross2;

            double sss = computeSSS(monthlyGross);
            double pagibig = computePagibig(monthlyGross);
            double phil = computePhilHealth(monthlyGross);

            double taxable = monthlyGross - sss - pagibig - phil;

            double tax = computeTax(taxable);

            double totalDeduction = sss + pagibig + phil + tax;

            double net2 = gross2 - totalDeduction;

            double totalNet = gross1 + net2;

            System.out.println("\n" + months[m] + " Payroll");

            System.out.println("1-15 Hours : " + first);
            System.out.println("1-15 Gross : " + gross1);

            System.out.println("16-"+lastDay+" Hours : " + second);
            System.out.println("16-"+lastDay+" Gross : " + gross2);

            System.out.println("SSS : " + sss);
            System.out.println("PhilHealth : " + phil);
            System.out.println("PagIbig : " + pagibig);
            System.out.println("Tax : " + tax);

            System.out.println("Net Salary : " + totalNet);
        }
    }

    // ================= DEDUCTIONS =================

    static double computeSSS(double gross) {


        // Check which salary bracket the employee falls under
        // and return the corresponding SSS employee contribution

        if (gross < 3250) return 135.00;        // Salary below 3,250
        else if (gross < 3750) return 157.50;   // 3,250 – 3,749.99
        else if (gross < 4250) return 180.00;   // 3,750 – 4,249.99
        else if (gross < 4750) return 202.50;   // 4,250 – 4,749.99
        else if (gross < 5250) return 225.00;   // 4,750 – 5,249.99
        else if (gross < 5750) return 247.50;   // 5,250 – 5,749.99
        else if (gross < 6250) return 270.00;   // 5,750 – 6,249.99
        else if (gross < 6750) return 292.50;   // 6,250 – 6,749.99
        else if (gross < 7250) return 315.00;   // 6,750 – 7,249.99
        else if (gross < 7750) return 337.50;   // 7,250 – 7,749.99
        else if (gross < 8250) return 360.00;   // 7,750 – 8,249.99
        else if (gross < 8750) return 382.50;   // 8,250 – 8,749.99
        else if (gross < 9250) return 405.00;   // 8,750 – 9,249.99
        else if (gross < 9750) return 427.50;   // 9,250 – 9,749.99
        else if (gross < 10250) return 450.00;  // 9,750 – 10,249.99
        else if (gross < 10750) return 472.50;  // 10,250 – 10,749.99
        else if (gross < 11250) return 495.00;  // 10,750 – 11,249.99
        else if (gross < 11750) return 517.50;  // 11,250 – 11,749.99
        else if (gross < 12250) return 540.00;  // 11,750 – 12,249.99
        else if (gross < 12750) return 562.50;  // 12,250 – 12,749.99
        else if (gross < 13250) return 585.00;  // 12,750 – 13,249.99
        else if (gross < 13750) return 607.50;  // 13,250 – 13,749.99
        else if (gross < 14250) return 630.00;  // 13,750 – 14,249.99
        else if (gross < 14750) return 652.50;  // 14,250 – 14,749.99
        else if (gross < 15250) return 675.00;  // 14,750 – 15,249.99
        else if (gross < 15750) return 697.50;  // 15,250 – 15,749.99
        else if (gross < 16250) return 720.00;  // 15,750 – 16,249.99
        else if (gross < 16750) return 742.50;  // 16,250 – 16,749.99
        else if (gross < 17250) return 765.00;  // 16,750 – 17,249.99
        else if (gross < 17750) return 787.50;  // 17,250 – 17,749.99
        else if (gross < 18250) return 810.00;  // 17,750 – 18,249.99
        else if (gross < 18750) return 832.50;  // 18,250 – 18,749.99
        else if (gross < 19250) return 855.00;  // 18,750 – 19,249.99
        else if (gross < 19750) return 877.50;  // 19,250 – 19,749.99
        else if (gross < 20250) return 900.00;  // 19,750 – 20,249.99
        else if (gross < 20750) return 922.50;  // 20,250 – 20,749.99
        else if (gross < 21250) return 945.00;  // 20,750 – 21,249.99
        else if (gross < 21750) return 967.50;  // 21,250 – 21,749.99
        else if (gross < 22250) return 990.00;  // 21,750 – 22,249.99
        else if (gross < 22750) return 1012.50; // 22,250 – 22,749.99
        else if (gross < 23250) return 1035.00; // 22,750 – 23,249.99
        else if (gross < 23750) return 1057.50; // 23,250 – 23,749.99
        else if (gross < 24250) return 1080.00; // 23,750 – 24,249.99
        else if (gross < 24750) return 1102.50; // 24,250 – 24,749.99

            // If salary is 24,750 or above, the maximum employee contribution applies
        else return 1125.00;
    }

    static double computePagibig(double gross) {

        double contrib = gross * 0.02;

        if (contrib > 100) contrib = 100;

        return contrib;
    }

    static double computePhilHealth(double gross) {

        if (gross <= 10000) return 150;

        if (gross <= 90000) return (gross * 0.03)/2;

        return 2250;
    }

    static double computeTax(double taxable) {

        if (taxable <= 20832) return 0;

        else if (taxable <= 33332)
            return (taxable - 20833) * 0.20;

        else if (taxable <= 66666)
            return 2500 + (taxable - 33333) * 0.25;

        else if (taxable <= 166666)
            return 10833 + (taxable - 66667) * 0.30;

        else if (taxable <= 666666)
            return 40833.33 + (taxable - 166667) * 0.32;

        else
            return 200833.33 + (taxable - 666667) * 0.35;
    }
}