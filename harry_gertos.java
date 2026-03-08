/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

//package com.lhark.mo.it101_milestone2_group20_2t_2025_2026; - "commented" for local testing.

//Import required Classes
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author harrymg
 */

public class harry_gerts {
    
    //-----[Path of CSV files]-----//
    static final String EMPLOYEE_FILE_PATH = "resources/employee_details.csv"; //Employee Details CSV file path.
    static final String ATTENDANCE_FILE_PATH = "resources/attendance_record.csv"; //Attendance Details CSV file path.

    //------[Main Method]------//
    public static void main(String[] args) {
        //Setup scanner that we will be using all throughout the program.
        Scanner scanner = new Scanner(System.in);
        userLogin(scanner);
        scanner.close();
    }

    //------[Method for the User Login]------//
    public static void userLogin(Scanner scanner) {
        //Declare variables needed for login such as employee and payroll staff username, password.
        String employeeUsername = "employee";
        String payrollStaffUsername = "payroll_staff";
        String passwordAll = "12345";

        //------[Displays welcome page and instruction for users]------//
        System.out.println("|---------------------------------------|");
        System.out.println("|   Welcome to MotorPH Payroll System   |");
        System.out.println("|                                       |");
        System.out.println("|   Enter your details below to login   |");
        System.out.println("|---------------------------------------|");
        System.out.println(" ");

        //------[Require users to enter their respective username and password]------//
        System.out.print("Enter your Username: "); //Asks employee and payroll staff to input their username.
        String usernameInput = scanner.nextLine();
        System.out.print("Enter your Password: "); //Asks employee and payroll staff to input their password.
        String passwordInput = scanner.nextLine(); 

        //------[This checks which kind of user logs in]------//
        if (usernameInput.equals(employeeUsername) && passwordInput.equals(passwordAll)) {
            System.out.println("Login Successful. " + "Welcome, " + employeeUsername + "!");
            employeeDashboard(scanner); //If employee login successful, proceed to employee dashboard.
        } else if (usernameInput.equals(payrollStaffUsername) && passwordInput.equals(passwordAll)) {
            System.out.println("Login Successful. " + "Welcome, " + payrollStaffUsername + "!");
            payrollStaffDashboard(scanner); //If payroll_staff user login successful, proceed to payroll staff dashboard.
        } else {
            System.out.println("\nIncorrect username and/or password."); //Displays message if username and/or password is wrong.
        }
    }
    
    //=============================[ "EMPLOYEE" USER ACTIONS ]===================================//
    
    //------[Method for Employee Dashboard]------//
    public static void employeeDashboard(Scanner scanner) {
        //while loop gives the employee a chance to enter correct keys should they enter wrong keys.
        while (true) { 
            //------[Displays Employee User Dashboard]------//
            System.out.println("\n|----------------------------------------|");
            System.out.println("|            Employee Dashboard          |");
            System.out.println("|                                        |");
            System.out.println("|    Press 1 to View Employee Details    |");
            System.out.println("|             Press 2 to Exit            |");
            System.out.println("|----------------------------------------| \n");
            System.out.print("Enter your choice: "); //Asks employee user to input their choice.
            String employeeDashboardChoice = scanner.nextLine();

            if (employeeDashboardChoice.equals("1")) {
                System.out.println(" ");
                displayEmployeeDetails(scanner); //If employee pressed "1", proceed to employee details dashboard.
            } else if (employeeDashboardChoice.equals("2")) {
                System.out.println("Exiting application."); //If employee pressed "2", exit the application.
                return;
            } else {
                System.out.println("Wrong option. Please try again."); //If employee pressed wrong keys, display message.
            }
        }
    }

    //------[Method for fetching Employee Details from CSV file]------//
    public static String[] getEmployeeDetails(String csvPath, String targetEmployeeNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            br.readLine(); //Skip header.
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                //Regex to ignore comma in quotes to avoid potential errors.
                String cleaned = line.replaceAll("\"([^\"]*),([^\"]*)\"", "$1$2");
                String[] column = cleaned.split(","); 

                String employeeNumber = column[0].trim(); //Employee number is 1st cell (A1) in the EMPLOYEE_FILE_PATH CSV.
                if (employeeNumber.equals(targetEmployeeNumber)) {
                    return column;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file: " + e.getMessage()); //Display message if there is error reading CSV file.
        }
        return null;
    }

    //------[Method for displaying Employee Details from CSV file]------//
    public static void displayEmployeeDetails(Scanner scanner) {
        //Asks employee to enter employee number to display.
        System.out.print("Enter Employee Number: "); //Asks employee to input employee number.
        String employeeNumberInput = scanner.nextLine().trim();

        //Invokes method "getEmployeeDetails" to get employee information from CSV file.
        String[] cell = getEmployeeDetails(EMPLOYEE_FILE_PATH, employeeNumberInput);
        
        //Display message if wrong or non-existent employee number is inputted.
        if (cell == null) {
        System.out.println("\n|-----------------------------------------|");
        System.out.println("|     Employee number does not exist.     |");
        System.out.println("|-----------------------------------------| \n");
        return;
        }
        
        //Variables for employee details such number, name and birthday.
        String employeeNumber = cell[0].trim(); //Employee number is 1st cell (A1) in the EMPLOYEE_FILE_PATH CSV.
        String employeeLastName = cell[1].trim(); //Employee Last Name is 2nd cell (B1) in the EMPLOYEE_FILE_PATH CSV.
        String employeeFirstName = cell[2].trim(); //Employee First Name is 3rd cell (C1) in the EMPLOYEE_FILE_PATH CSV.
        String employeeBirthday = cell[3].trim();  //Employee Birthday is 4th cell (D1) in the EMPLOYEE_FILE_PATH CSV.

        //Prints employee details once employee number has been inputted.
        System.out.println("|----------------------------------------|");
        System.out.println("|            Employee Details:           |");
        System.out.println("|----------------------------------------| \n");
        System.out.println("Employee Number: " + employeeNumber);
        System.out.println("Employee Name: " + employeeLastName + ", " + employeeFirstName);
        System.out.println("Employee Birthday: " + employeeBirthday);
    }
    
    //=============================[ "PAYROLL STAFF" USER ACTIONS ]===================================//
    
    //------[Method for Payroll Staff Dashboard]------//
    public static void payrollStaffDashboard(Scanner scanner) {
        //while loop gives the payroll staff a chance to enter correct keys should they enter wrong keys.
        while (true) { 
            //------[Displays Payroll Staff Dashboard]------//
            System.out.println("|---------------------------------------------|");
            System.out.println("|           Payroll Staff Dashboard           |");
            System.out.println("|                                             |");
            System.out.println("|          Press 1 to Process Payroll         |");
            System.out.println("|               Press 2 to Exit               |");
            System.out.println("|---------------------------------------------| \n");
            System.out.print("Enter your choice: "); //Asks payroll staff to input their choice.
            String employeeDashboardChoice = scanner.nextLine();

            if (employeeDashboardChoice.equals("1")) {
                System.out.println(" ");
                processPayrollDashboard(scanner); //If payroll staff pressed "1", proceed to process payroll dashboard.
            } else if (employeeDashboardChoice.equals("2")) {
                System.out.println("Exiting application."); //If payroll staff pressed "2", exit the application.
                return;
            } else {
                System.out.println("Wrong option. Please try again."); //If payroll staff pressed wrong keys, display message.
            }
        } 
    }
    
    //------[Method for Process Payroll Dashboard]------//
    public static void processPayrollDashboard(Scanner scanner) {
        //while loop gives the payroll staff a chance to enter correct keys should they enter wrong keys.
        while (true) { 
            //Displays/print Payroll Dashboard and instructions.
            System.out.println("|---------------------------------------------|");
            System.out.println("|          Process Payroll Dashboard          |");
            System.out.println("|                                             |");
            System.out.println("|          Press 1 to ONE Employee/s          |");
            System.out.println("|          Press 2 to ALL Employee/s          |");
            System.out.println("|               Press 3 to Exit               |");
            System.out.println("|---------------------------------------------| \n");
            System.out.print("Enter your choice: "); //Asks payroll staff to input their choice.
            String employeeDashboardChoice = scanner.nextLine();

            if (employeeDashboardChoice.equals("1")) {
                processOneEmployee(scanner); //If payroll staff pressed "1", proceed to process one employee.
            } else if (employeeDashboardChoice.equals("2")) {
                processAllEmployee(scanner); //If payroll staff pressed "2", proceed to process all employee.
            } else if (employeeDashboardChoice.equals("3")) {
                System.out.println("Returning to previous page."); //If pressed, payroll staff returns to payrollStaffDashboard.
                return;
            } else {
                
            }
        }
    }
    
    //------[Method for processing ONE Employee]------//
    public static void processOneEmployee(Scanner scanner) {
        System.out.print("Enter Employee Number: "); //Asks employee to input employee number.
        String employeeNumberInput = scanner.nextLine().trim();

        //Invokes method "getEmployeeDetails" to get employee information from CSV file.
        String[] cell = getEmployeeDetails(EMPLOYEE_FILE_PATH, employeeNumberInput);

        //Display message if wrong or non-existent employee number is inputted.
        if (cell == null) {
        System.out.println("\n|-----------------------------------------|");
        System.out.println("|     Employee number does not exist.     |");
        System.out.println("|-----------------------------------------| \n");
        return;
        }
        
        //Variables for employee details such number, name and birthday.
        String employeeNumber = cell[0].trim(); //Employee number is 1st cell (A1) in the EMPLOYEE_FILE_PATH CSV.
        String employeeLastName = cell[1].trim(); //Employee Last Name is 2nd cell (B1) in the EMPLOYEE_FILE_PATH CSV.
        String employeeFirstName = cell[2].trim(); //Employee First Name is 3rd cell (C1) in the EMPLOYEE_FILE_PATH CSV.
        String employeeBirthday = cell[3].trim(); //Employee Birthday is 4th cell (D1) in the EMPLOYEE_FILE_PATH CSV.
        
        //Get/invoke attendance from "getEmployeeAttendance" method.
        java.util.List<String[]> attendance = getEmployeeAttendance(ATTENDANCE_FILE_PATH, employeeNumberInput);
        
        //Display/print basic employee details.
        System.out.println("|----------------------------------------|");
        System.out.println("|            Employee Details:           |");
        System.out.println("|----------------------------------------| \n");
        System.out.println("Employee Number: " + employeeNumber);
        System.out.println("Employee Name: " + employeeLastName + ", " + employeeFirstName);
        System.out.println("Employee Birthday: " + employeeBirthday);
        
        //for loop to display results from June (6) to December (12).
        for (int month = 6; month <= 12; month++){
            int lastDay = YearMonth.of(2024, month).lengthOfMonth(); //Get lastDay since last day varies by month.
            String monthName = YearMonth.of(2024, month).getMonth().name(); //Converts month number (6) to string (June).
            monthName = monthName.substring(0, 1) + monthName.substring(1).toLowerCase(); //Converts to lowercase.
            
            //Declare double variables for calculation for Cutoff 1 and Cuttoff 2.
            double hours1 = calculateTotalHours(attendance, month, 1, 15); //Calculates Total Hours worked for Cutoff 1.
            double gross1 = calculateGrossSalary(cell, hours1); //Calculates Gross Salary for Cutoff 1.
            double hours2 = calculateTotalHours(attendance, month, 16, lastDay); //Calculates Total Hours worked for Cutoff 2.
            double gross2 = calculateGrossSalary(cell, hours2); //Calculates Gross Salary for Cutoff 2.
            double combinedGross = gross1 + gross2; //Calculates combined Gross Salary for Cutoff 1 and Cutoff 2 (whole month).
            
            //Declare double variables for Government deductions. 
            double sss = calculateSSS(combinedGross); //Calculates SSS contribution based on combined gross salary.
            double philhealth = calculatePhilHealth(combinedGross); //Calculates PhilHealth premium based on combined gross salary.
            double pagibig = calculatePagIbig(combinedGross); //Calculates PagIbig contributions based on combined gross salary.
            double tax = calculateTax(combinedGross, sss, philhealth, pagibig); //Calculates tax based on combined gross salary.
            double totalDeductions = sss + philhealth + pagibig + tax; //Calculates overall government deductions.
            
            //Display/print the variables for Cutoff 1.
            System.out.println("\n----- CUTOFF 1: Month of: " + monthName + " 1 - 15 -----");
            System.out.println("Total Hours Worked: " + hours1);
            System.out.println("Gross Salary: " + gross1);
            System.out.println("Net Salary: " + gross1);
            
            double net2 = gross2 - totalDeductions; //Calculates total net salary.
            
            //Display/print the variables for Cutoff 2.
            System.out.println("\n----- CUTOFF 2: Month of: " + monthName + " 16 - " + lastDay + " -----");
            System.out.println("Total Hours Worked: " + hours2);
            System.out.println("Gross Salary: " + gross2);
            System.out.println("Deductions: ");
            System.out.println("    SSS: " + sss);
            System.out.println("    PhilHealth: " + philhealth);
            System.out.println("    PagIbig: " + pagibig);
            System.out.println("    Tax: " + tax);
            System.out.println("Total Deductions: " + totalDeductions);
            System.out.println("Net Salary: " + net2);
            System.out.println("\n=======================================================================");
        }
    }
    
    //------[Method for processing ALL Employees]------//
    public static void processAllEmployee(Scanner scanner) {
        //Straightly displays all employees. No input/asking for employee ID.
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE_PATH))) {
            br.readLine(); //Skip header.
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                //Regex to ignore comma in quotes to avoid potential errors.
                String cleaned = line.replaceAll("\"([^\"]*),([^\"]*)\"", "$1$2");
                String[] cell = cleaned.split(",");

                String employeeNumber = cell[0].trim(); //Employee number is 1st cell (A1) in the EMPLOYEE_FILE_PATH CSV.
                String employeeLastName = cell[1].trim(); //Employee last name is 2nd cell (B1) in the EMPLOYEE_FILE_PATH CSV.
                String employeeFirstName = cell[2].trim(); //Employee first name is 3rd cell (C1) in the EMPLOYEE_FILE_PATH CSV.
                String employeeBirthday = cell[3].trim(); //Employee birthday is 4th cell (D1) in the EMPLOYEE_FILE_PATH CSV.

                //Get/invoke attendance from "getEmployeeAttendance" method.
                java.util.List<String[]> attendance = getEmployeeAttendance(ATTENDANCE_FILE_PATH, employeeNumber);

                //Display/print basic employee details.
                System.out.println("\n|----------------------------------------|");
                System.out.println("|         Employee Payroll Details         |");
                System.out.println("|----------------------------------------|\n");
                System.out.println("Employee Number: " + employeeNumber);
                System.out.println("Employee Name: " + employeeLastName + ", " + employeeFirstName);
                System.out.println("Employee Birthday: " + employeeBirthday);

                //for loop to display results from June (6) to December (12).
                for (int month = 6; month <= 12; month++) {
                    int lastDay = YearMonth.of(2024, month).lengthOfMonth(); //Get lastDay since last day varies by month.
                    String monthName = YearMonth.of(2024, month).getMonth().name(); //Converts month number (6) to string (June).
                    monthName = monthName.substring(0, 1) + monthName.substring(1).toLowerCase(); //Converts to lowercase.

                    //Declare double variables for calculation for Cutoff 1 and Cuttoff 2.
                    double hours1 = calculateTotalHours(attendance, month, 1, 15); //Calculates Total Hours worked for Cutoff 1.
                    double gross1 = calculateGrossSalary(cell, hours1); //Calculates Gross Salary for Cutoff 1.
                    double hours2 = calculateTotalHours(attendance, month, 16, lastDay); //Calculates Total Hours worked for Cutoff 2.
                    double gross2 = calculateGrossSalary(cell, hours2); //Calculates Gross Salary for Cutoff 2.
                    double combinedGross = gross1 + gross2; //Calculates combined Gross Salary for Cutoff 1 and Cutoff 2 (whole month).

                    //Declare double variables for Government deductions. 
                    double sss = calculateSSS(combinedGross); //Calculates SSS contribution based on combined gross salary.
                    double philhealth = calculatePhilHealth(combinedGross); //Calculates PhilHealth premium based on combined gross salary.
                    double pagibig = calculatePagIbig(combinedGross); //Calculates PagIbig contributions based on combined gross salary.
                    double tax = calculateTax(combinedGross, sss, philhealth, pagibig); //Calculates tax based on combined gross salary.
                    double totalDeductions = sss + philhealth + pagibig + tax; //Calculates overall government deductions.

                    //Display/print the variables for Cutoff 1.
                    System.out.println("\n----- CUTOFF 1: Month of: " + monthName + " 1 - 15 -----");
                    System.out.println("Total Hours Worked: " + hours1);
                    System.out.println("Gross Salary: " + gross1);
                    System.out.println("Net Salary: " + gross1);

                    double net2 = gross2 - totalDeductions; //Calculates total net salary.

                    //Display/print the variables for Cutoff 2.
                    System.out.println("\n----- CUTOFF 2: Month of: " + monthName + " 16 - " + lastDay + " -----");
                    System.out.println("Total Hours Worked: " + hours2);
                    System.out.println("Gross Salary: " + gross2);
                    System.out.println("Deductions: ");
                    System.out.println("    SSS: " + sss);
                    System.out.println("    PhilHealth: " + philhealth);
                    System.out.println("    PagIbig: " + pagibig);
                    System.out.println("    Tax: " + tax);
                    System.out.println("Total Deductions: " + totalDeductions);
                    System.out.println("Net Salary: " + net2);
                    System.out.println("\n=======================================================================");
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file: " + e.getMessage());
        }
}
    
    //------[Method for fetching Employee Attendance from CSV file]------//
    public static java.util.List<String[]> getEmployeeAttendance(String csvPath, String targetEmployeeNumber) {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            br.readLine(); //Skip header.
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                //Regex to ignore comma in quotes to avoid potential errors.
                String cleaned = line.replaceAll("\"([^\"]*),([^\"]*)\"", "$1$2");
                String[] column = cleaned.split(",");
                
                if (column[0].trim().equals(targetEmployeeNumber)) {
                    rows.add(column);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading attendance file: " + e.getMessage());
        }
        return rows;
    }
    
    //------[Method for calculating Total Work Hours an employee had based on attendance in CSV file]------//
    public static double calculateTotalHours(java.util.List<String[]>  attendanceRows, int targetMonth, int startDay, int endDay) {
       
        double totalHours = 0;
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        LocalTime workStart = LocalTime.of(8, 0); //Starts time at 8:00 AM.
        LocalTime workEnd = LocalTime.of(17,0); //Ends time at 17:00 / 5:00 PM.
        LocalTime graceEnd = LocalTime.of(8, 10); //Grace period ends at 8:10 AM.
        double lunchBreak = 1.0; //1 hour unpaid lunch break.
            
     
        for (String[] row: attendanceRows) {
            String date = row[3].trim(); //Attendance record date is 4th cell (D1) in the ATTENDANCE_FILE_PATH CSV.
            String[] dateParts = date.split("/");
            int month = Integer.parseInt(dateParts[0]);
            int day = Integer.parseInt(dateParts[1]);
            
            //Parse login and logout time.
            if (month == targetMonth && day >= startDay && day <= endDay) {
                LocalTime login = LocalTime.parse(row[4].trim(), timeFormat); //Login time is 5th cell (E1) in the ATTENDANCE_FILE_PATH CSV.
                LocalTime logout = LocalTime.parse(row[5].trim(), timeFormat); //Logout time is 6th cell (F1) in the ATTENDANCE_FILE_PATH CSV.
                
                //Apply 10-minute grace period based on MotorPH's policy.
                if (!login.isBefore(workStart) && !login.isAfter(graceEnd)) {
                    login = workStart;
                }
                
                //If login is before 8:00 AM, cap to 8:00 AM.
                if (login.isBefore(workStart)) {
                    login = workStart;
                }
                
                //If logout is after 5:00 PM, cap to 5:00 PM.
                if (logout.isAfter(workEnd)) {
                    logout = workEnd;
                }
               
                double hoursWorked = Duration.between(login, logout).toMinutes() / 60.0;
                
                //Deduct 1 hour lunch break if worked enough ours.
                if (hoursWorked > lunchBreak) {
                    hoursWorked -= lunchBreak;
                    
                }
                if (hoursWorked > 0) {
                    totalHours += hoursWorked;
                }
            }
        }
        return totalHours;
    }
    
    //=============================[ DEDUCTIONS CALCULATION ]===================================//
    
    //------[Method for calculating Gross Salary based on Total Hours Worked x Hourly Rate (S1)]------//
    public static double calculateGrossSalary(String[] employeeRow, double totalHoursWorked) {
        
        double hourlyRate = Double.parseDouble(employeeRow[18].trim()); //Hourly rate is 19th cell (S1) in the EMPLOYEE_FILE_PATH CSV.
        return totalHoursWorked * hourlyRate; 
    }
    
    //------[Method for SSS deduction calculation]------//
    public static double calculateSSS(double grossSalary) {
       
        //Declare double variable "contrib".
        double contrib = 0;
        
        //Based on the SSS matrix from MotorPH website.
        if (grossSalary < 22750) {
            contrib = 1012.50;
        } else if (grossSalary < 23250) {
            contrib = 1035;
        } else if (grossSalary < 23750) {
            contrib = 1057.50;
        } else if (grossSalary < 24250) {
            contrib = 1080;
        } else if (grossSalary < 24750) {
            contrib = 1102.50;
        } else {
            contrib = 1125;
        }
        return contrib;
    }
    
    //------[Method for PhilHealth deduction calculation]------//
    public static double calculatePhilHealth (double grossSalary) {
        
        //Declare double variable "premium".
        double premium = 0;
        
        //Based on the PhilHealth matrix from MotorPH website.
        if (grossSalary <= 10000) {
            premium = 300;
        } else if (grossSalary <= 90000) {
            premium = grossSalary * 0.03; //3% premium.
        } else {
            premium = 4500;
        }
    
        return premium / 2.0;
    }
    
    //------[Method for PagIbig deduction calculation]------//
    public static double calculatePagIbig(double grossSalary) {
        
        //Declare double variable "contrib".
        double contrib = grossSalary * 0.02; //2% contribution.
        
        //Based on the PagIbig matrix from MotorPH website.
        if (contrib > 100) {
            contrib = 100;
        }
        return contrib;
    }
    
    //------[Method for ITR deduction calculation]------//
    public static double calculateTax(double grossSalary, double sss, double philhealth, double pagibig ) {
        
        //Declare double variables "taxableIncome" and "tax".
        double taxableIncome = grossSalary - sss - philhealth - pagibig;
        double tax = 0;
        
        //Based on the Withholding tax matrix from MotorPH website.
        if (taxableIncome <= 20832) { 
            tax = 0; //No withholding tax for 20,832 and below.
        } else if (taxableIncome <= 33332) {
            tax = 0 + (taxableIncome - 20833) * 0.20; //20% in excess of 20,833.
        } else if (taxableIncome <= 66666) {
            tax = 2500 + (taxableIncome - 33333) * 0.25; //25% in excess of 33,333 plus 2,500.
        } else if (taxableIncome <= 166666) {
            tax = 10833 + (taxableIncome - 66667) * 0.30; //30% in execess of 66,667 plus 10,833.
        } else if (taxableIncome <= 666666) {
            tax = 40833.33 + (taxableIncome - 166667) * 0.32; //32% in excess of 166,667 plus 40,833.33.
        } else {
            tax = 200833.33 + (taxableIncome - 666667) * 0.35; //35% in excess of 666,667 plus 200,833.33.
        }
        return tax;
    }
    
}
    
    /*
    //----------[References]----------//
    
    > Employee Details and Attendance Spreadsheet: https://docs.google.com/spreadsheets/d/189fvPCZS7JKMBYos00ZEoApqnBB05jAjy2vak6lD4g0/view
    > SSS computation matrix for method "calculateSSS": https://docs.google.com/spreadsheets/d/1g2gPgfqy1VNBAtnoJHLvdEA7m_GM9JOwZ22_uA-hpcU/view
    > PhilHealth computation matrix for method "calculatePhilHealth": https://docs.google.com/spreadsheets/d/16qXxY8K-DG8sm_QHtM4gHfHCNjhliNdSvUzF2yOz-bg/view
    > PagIbig computation matrix for method "calculatePagIbig": https://docs.google.com/spreadsheets/d/1cHwE0j3xplJcubD57hZf7R0Cih-n7urqO92KFVsciPs/view
    > Tax computation matrix for method "calculateTax": https://docs.google.com/spreadsheets/d/1mWxdCuYCmTd8n3DrNVxIb912xT8dWFCsQTUc2owv2UQ/view
    > Regular Expression/Regex for ignoring comma in quotes (java split csv): https://codemia.io/knowledge-hub/path/java_splitting_a_comma-separated_string_but_ignoring_commas_in_quotes
                                                                              https://www.baeldung.com/java-split-string-commas
    > Java File Handling reading multiple records CSV files: https://www.youtube.com/watch?v=KI_a39BeCLU
                                                             https://www.youtube.com/watch?v=-Aud0cDh-J8
    > Java Array List: https://www.youtube.com/watch?v=wsTSREgCE5E
    > Coursera (scanner, operator, loops, file handling): https://www.coursera.org/learn/mo-it101/home/module/8
                                                          https://www.coursera.org/learn/mo-it101/home/module/9
                                                          https://www.coursera.org/learn/mo-it101/home/module/10
     */ 
