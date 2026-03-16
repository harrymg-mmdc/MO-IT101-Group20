# MotorPH Payroll System - MO-IT101 | H1101 | Group 20
**Milestone 2** for IT101 - Computer Programming 1

<br>

## [ TEAM DETAILS ]

| Member Name | Contributions |
| :--- | :--- | 
| Karl Aparece | • Create intial project<br>• Create methods of `main`, `computeSSS`, `computePhilhealth`<br>• Updated Use Case Diagram based on Mentor/IT Coach feedback<br>• Update Project Plan   | 
| Rina Mae Falculan | • Create intial project<br>• Create methods of `loadEmployees`, `loadAttendance`, `displayPayroll`<br>• Provided external resources/references |
| Harry Gertos | • Created & Updated Github repository<br>• Create intial project<br>• Create methods of `loginSystem`, `employeeMenu`, `payrollMenu`<br>• Updated README.md |
| Lady Mae Tapere | • Create intial project<br>• Create methods of `computeHours`, `computeTax`, `computePagibig`<br>• Updated Wireframe based on Mentor/IT Coach feedback |


<br>


## [ PROGRAM DETAILS ]

The program is a simple payroll system that is designed for 2 users: An **Employee** and a **Payroll Staff**. Both users will require authentication in order to use the program. Employee User can view their details by entering their Employee Number while Payroll Staff User can either process a payroll of an Employee or process all Employees by entering the Employee Number. The program is easy to use and instructions are provided for each use case. The table below shows how the program works:

| User | Credentials (username/password) | Use Case | Outcome |
| :--- | :--- | :--- | :--- |
| Employee | employee/12345  | View Employee Details | Displays Employee Details: <br>• Employee Number <br>• Employee Name<br>• Employee Birthday. |
|          |                 | Exit Program          | Closes or Exits the Program. |
| Payroll Staff | payroll_staff/12345 | Process Payroll of ONE Employee | Displays Employee Payroll Details in Semi-Monthly format (2 Cutoffs). Displays the following information in order: <br>• Employee Number<br>• Employee Name<br>• Employee Birthday<br>• Cutoff 1 - Total Hours Worked<br>• Cutoff 1 - Gross Salary<br>• Cutoff 1 - Net Salary<br>• Cutoff 2 - Total Hours Worked<br>• Cutoff 2 - Gross Salary<br>• Cutoff 2 - SSS, PhilHealth, Pag-IBIG and Tax deductions<br>• Cutoff 2 - Total Deductions<br>• Cutoff 2 - Net Salary |
|               |                     | Procees Payroll of ALL Employees | Displays ALL Employees Payroll Details in Semi-Monthly format (2 Cutoffs). Displays the following information in order:<br>• Employee Number<br>• Employee Name<br>• Employee Birthday<br>• Cutoff 1 - Total Hours Worked<br>• Cutoff 1 - Gross Salary<br>• Cutoff 1 - Net Salary<br>• Cutoff 2 - Total Hours Worked<br>• Cutoff 2 - Gross Salary<br>• Cutoff 2 - SSS, PhilHealth, Pag-IBIG and Tax deductions<br>• Cutoff 2 - Total Deductions<br>• Cutoff 2 - Net Salary |
|               |                     | Exit Program | Closes or Exits the Program. |

<br>

## [HOW TO RUN THE PROGRAM]

To run the program, you can either choose any of the following:

**A. Running via NETBEANS IDE:**
1. Go to https://github.com/harrymg-mmdc/MO-IT101-Group20 and click `Code` -> `Download Zip`. Extract the Project Folder to your desired location (e.g. Desktop or Documents).
2. Alternatively, you can run this command on your Command Prompt: `git clone https://github.com/harrymg-mmdc/MO-IT101-Group20.git`
3. Open Netbeans IDE and click `Open Project`. Under `Look in` dropdown, choose the path/location/folder where you extracted or downloaded the Project.
4. Click `Open Project` button then afterwards, click the `Run Project` button (F6).
5. The program should run. Look at the console and you can input your login crdentials to proceed.

&nbsp;

**B. Running via COMMAND PROMPT/TERMINAL:**
1. Make sure you have git and java installed in your device. Open your Command Prompt and enter `git clone https://github.com/harrymg-mmdc/MO-IT101-Group20.git`
2. The project will be downloaded on your device. Input this in the Command Prompt: `cd MO-IT101-Group20/` hit enter. 
3. Afterwards, input this in the Command Prompt: `javac -d . src/main/java/com/milestone2group20/Milestone2group20.java` and hit enter.
4. Finally to run the program, enter this in the Command Prompt: `java com.milestone2group20.Milestone2group20`
5. The program should run. Look at the terminal and you can input your login credentials to proceed.


<br>

## [ [PROJECT PLAN LINK](https://docs.google.com/spreadsheets/d/1NJKSsgbyVyR2Pja8zfwnQdsonT4iYbKpyGxvoQpk0oM/edit?usp=sharing) ]
