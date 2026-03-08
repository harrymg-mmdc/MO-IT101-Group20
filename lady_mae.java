/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
//package motorph_milestone2; - "Commented" for local testing.

/*
 * MotorPH Milesone 2: Payroll System
 * This program processes employee attendance and calculates monthly
 * groww and net wages
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;



public class lady_mae {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter Employee Number: ");
        String inputEmployeeNumber = scanner.nextLine();
        scanner.close();
        
        String employeeNumber = "";
        String firstName = "";
        String lastName = "";
        String birthday = "";
        double hourlyRate = 0.0;
        boolean found = false;
        
        //-----------------------
        // READ EMPLOYEE DETAILS
        //-----------------------
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/employee_details.csv"))) {
            
                reader.readLine(); // Skip Header
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] data = line.split(",");

                    if (data[0].equals(inputEmployeeNumber)) {
                        employeeNumber = data[0];
                        lastName = data[1];
                        firstName = data[2];
                        birthday = data[3];

                        // data.length - 1 to get the last column from the csv file (Hourly Rate)
                        hourlyRate = Double.parseDouble(data[data.length - 1].replace("\"", "").trim());

                        found = true;
                        break;
                    }
                }
            }catch (IOException e){
                System.out.println("Error reading employee file.");
                return;
            }

            if (!found) {
                System.out.println("Employee does not exist.");
                return;
            }
            
            // PRINT EMPLOYEE INFORMATION
            System.out.println("Employee Number: " + employeeNumber);
            System.out.println("Employee Name: " + lastName + ", " + firstName);
            System.out.println("Birthday : " + birthday);          
            
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

            //-----------------------
            // ATTENDANCE PARSING
            //-----------------------
            for (int month = 6; month <= 12; month++) {
                double firstCutoff = 0;
                double secondCutoff = 0;

                int daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

                try (BufferedReader reader = new BufferedReader ( new FileReader("resources/attendance_record.csv"))) {

                    reader.readLine(); //Skip header
                    String line;

                        while ((line = reader.readLine()) != null){
                            if (line.trim().isEmpty()) continue;

                            String[] data = line.split(",");
                            if (!data[0].equals(employeeNumber)) continue;

                            String[] dateParts = data[3].split("/");
                            int recordMonth = Integer.parseInt(dateParts[0]);
                            int day = Integer.parseInt(dateParts[1]);
                            int year = Integer.parseInt(dateParts[2]);

                            if (year != 2024 || recordMonth != month) continue;

                            // Converts time record from Attendance CSV to H:mm format
                            LocalTime login = LocalTime.parse(data[4].trim(), timeFormat);
                            LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);

                            // Call Method: COMPUTE DAILY HOURS
                            double hours = computeHours(login, logout);
                            
                            //Categorize hours by cutoff
                            if (day <= 15) firstCutoff += hours;
                            else secondCutoff += hours;
                        }

                }catch (IOException e) {
                    System.out.println("Error reading attendance file for month " + month);
                    e.printStackTrace();
                    continue;
                    }

                // Converts the month integer (6-12) into a String name for payslip display.
                String monthName = switch (month) {
                    case 6 -> "June";
                    case 7 -> "July";
                    case 8 -> "August";
                    case 9 -> "September";
                    case 10 -> "October";
                    case 11 -> "November";
                    case 12 -> "December";
                    default -> "Month " + month;
                    };


                //-----------------------
                // GROSS CALCULATION
                //-----------------------
                double grossFirstHalf = calculateGross(firstCutoff, hourlyRate);
                double grossSecondHalf = calculateGross(secondCutoff,hourlyRate);
                double monthlyGross = grossFirstHalf + grossSecondHalf;

                //-----------------------
                // DEDUCTIONS CALCULATION
                //-----------------------   
                double sssContribution = calculateSSS(monthlyGross);
                double philHealthContribution = calculatePhilHealth(monthlyGross);
                double pagIbigContribution = calculatePagIbig(monthlyGross);

                double totalBenefits = sssContribution + philHealthContribution + pagIbigContribution;
                double taxAmount = calculateTax(monthlyGross, totalBenefits);
                double totalDeductions = totalBenefits + taxAmount;

                //-----------------------
                // NET CALCULATION
                //-----------------------  
                double netSalaryFirstHalf = calculateNet(grossFirstHalf, 0);
                double netSalarySecondHalf = calculateNet(grossSecondHalf, totalDeductions);


                //-----------------------
                // PRINTING EMPLOYEE INFORMATION AND SALARY SUMMARY             
                //-----------------------
                System.out.println("\nCutoff 1: " + monthName + " 1 to 15");
                System.out.println("Total Hours Worked: " + firstCutoff);
                System.out.printf("Gross Salary: Php %,.2f%n", grossFirstHalf);
                System.out.printf("Net Salary: Php %,.2f%n", netSalaryFirstHalf);

                System.out.println("\nCutoff 2: " + monthName + " 16 to " + daysInMonth);
                System.out.println("Total Hours Worked: " + secondCutoff);
                System.out.printf("Gross Salary: Php %,.2f%n ", grossSecondHalf);
                System.out.println("Deductions: ");
                System.out.printf("  SSS: Php %,.2f%n", sssContribution);
                System.out.printf("  PhilHealth: Php %,.2f%n ", philHealthContribution);
                System.out.printf(" Pag-IBIG: Php %,.2f%n", pagIbigContribution);
                System.out.printf("  Tax: Php %,.2f%n", taxAmount);
                System.out.printf("Total Deductions: Php %,.2f%n", totalDeductions);
                System.out.printf("Net Salary: Php %,.2f%n", netSalarySecondHalf);
                //System.out.printf("%nTotal Gross Salary: Php %,.2f%n", monthlyGross);
                //System.out.printf("Total Net Salary: Php %,.2f%n", netSalaryFirstHalf + netSalarySecondHalf);
                System.out.println("===================================");
                    
            }   
        }     
    
        //-----------------------
        // CALCULATE HOURS WORKED
        //-----------------------
        static double computeHours(LocalTime login, LocalTime logout){
            LocalTime workStart = LocalTime.of(8, 0);
            LocalTime workEnd = LocalTime.of(17, 0);
            LocalTime gracePeriod = LocalTime.of(8, 10);
            
            // Clip time to the 8:00 - 5:00 window
            if (login.isBefore(workStart)) login = workStart;
            if (logout.isAfter(workEnd)) logout = workEnd;
            
            // If Login is not after 8:10, set it to 8;00
            // If it is 8:11 and beyond, this condition is false and it keep the 8:11 time
            if (!login.isAfter(gracePeriod)) login = workStart;
            
            // Calculate total elapsed minutes
            Long minutesElapsed = Duration.between(login, logout).toMinutes();
            
            // Only subject lunch if they were present for more than an hour
            long minutesWorked = minutesElapsed;
            if (minutesElapsed > 60) minutesWorked = minutesElapsed - 60; // Subtract 1 hours lunch if their total work hours exceed 1 hour
            
            // Check for negative or zero time
            if (minutesWorked < 0) {
                System.out.println("Negative hours. Please check attendance record.");
                return 0.0;
            }
            
            // Convert hours without rounding
            double hours = minutesWorked / 60.0;
            
            // Ensure it never exceeds 8 hours
            if (hours > 8.0) return 8.0;
            return hours;
            
        }           
     
        static double calculatePagIbig (double monthlyGross) {
            double pagIbigContribution = 0.0;
            if (monthlyGross >= 1000 && monthlyGross <= 1500){
                // 1% for lower bracket
                pagIbigContribution = monthlyGross * 0.01;
            } else if (monthlyGross > 1500) {
                // 2% for higher bracket (capped at 100)
                pagIbigContribution = monthlyGross * 0.02;
                        
                //Applying the maximum cap of 100 pesos
            if (pagIbigContribution > 100) pagIbigContribution = 100;
            }            
            return pagIbigContribution;
        }
        
        static double calculatePhilHealth (double monthlyGross) {
            double totalPhilHealthPremium = 0.0;
            
            if (monthlyGross <= 10000) totalPhilHealthPremium = 300.00;         // Fixed premium of 10k and below
            else if (monthlyGross >= 60000) totalPhilHealthPremium = 1800.00;   // capped premium for 60k and above
            else totalPhilHealthPremium = monthlyGross * 0.03;                  // 3% for salaries in between
                    
            return totalPhilHealthPremium / 2;            
        }
        
        
        static double calculateSSS(double monthlyGross) {
            double sssContribution = 0.0;

            // Deduction for 3,250 and below && 20250 and above
            if (monthlyGross <= 3250)sssContribution = 135.00; //Value from the table.
            else if (monthlyGross >= 20250) sssContribution = 922.50;

            // READING FROM SSS CONTRIBUTION CSV FILE
            try (BufferedReader reader = new BufferedReader(new FileReader("resources/sss_contribution.csv"))) {

                reader.readLine(); //Skip header
                String line; 

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    // Skip the lines containing the texts "Below" and "Over"
                    if (line.contains("Below") || line.contains("Over")) continue;

                    line = line.replace("\"", "");
                    String[] data = line.split(",");

                        // Remove quotes and commas
                        double lowerAmount = Double.parseDouble(data[0] + data[1].replace("\"", "").trim());
                        double higherAmount = Double.parseDouble(data[3] + data [4].replace("\"", "").trim());
                        double contribution = Double.parseDouble(data[5].replace("\"", "").trim());

                        //Check if the salary amount fits to any of the salary brackets
                        if (monthlyGross >= lowerAmount && monthlyGross <= higherAmount){
                            sssContribution = contribution;
                            break;
                        }
                }
            } catch (IOException e) {
                System.out.println("Error loading SSS schedule file.");
                }
            return sssContribution;
        }
        
        
        static double calculateTax(double monthlyGross, double totalBenefits) {
            double taxableIncome = monthlyGross - totalBenefits;
            double taxAmount = 0.0;

            if (taxableIncome <= 20832) taxAmount = 0;
            else if (taxableIncome < 33333) {
                taxAmount = (taxableIncome - 20833) * 0.20;
            }else if (taxableIncome < 66667) {
                taxAmount = 2500 + (taxableIncome - 33333) * 0.25;
            }else if (taxableIncome < 166667) {
                taxAmount = 10833 + (taxableIncome - 66667) * 0.30;
            }else if (taxableIncome < 666667) {
                taxAmount = 40833.33 + (taxableIncome - 166667) * 0.32;
            } else {
                taxAmount = 200833.33 + (taxableIncome - 666667) * 0.35;
            }
            return taxAmount;
        }
        
        static double calculateGross (double hours, double rate) {
            return hours * rate;
        }
        
        static double calculateNet (double gross, double deductions){
            double net = gross - deductions;
            if (net < 0) net = 0;
            return net;
        }
}
 
       
  


    

  

