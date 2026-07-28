package edu.ucdavis.rosetta;

import java.util.List;
import java.lang.reflect.Field;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main 
{
    public static void main(String[] args) 
    {

        //Submitted Argument Check
        if(args.length >= 2 && args[0].isEmpty() == false && args[1].isEmpty() == false)
        {

            //Determine Action to Take
            switch(args[0].toString().trim().toLowerCase())
            {
                case "people-login":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.loginid,args[1]);
                    break;
                case "people-iam":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.iamid,args[1]);
                    break;
                case "people-employee":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.employeeid,args[1]);
                    break;
                case "people-email":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.email,args[1]);
                    break;
                case "people-student":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.studentid,args[1]);
                    break;
                case "people-department":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.department,args[1]);
                    break;
                case "student-iam":
                    StudentSearching(RosettaAPIWorker.StudentSearchBy.iamid, args[1]);
                    break;
                case "student-pidm":
                    StudentSearching(RosettaAPIWorker.StudentSearchBy.pidm, args[1]);
                    break;
                case "student-studentid":
                    StudentSearching(RosettaAPIWorker.StudentSearchBy.studentid, args[1]);
                    break;
                case "student-majorcode":
                    StudentSearching(RosettaAPIWorker.StudentSearchBy.majorcode, args[1]);
                    break;
                case "student-collegecode":
                    StudentSearching(RosettaAPIWorker.StudentSearchBy.collegecode, args[1]);
                    break;
                case "employee-iam":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.iamid,args[1]);
                    break;
                case "employee-department":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.departmentid,args[1]);
                    break;
                case "employee-division":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.divisionid,args[1]);
                    break;
                case "employee-organization":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.organizationid,args[1]);
                    break;
                case "employee-subdivision":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.subdivisionid,args[1]);
                    break;
                case "employee-subdivisionl4":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.subdivisionl4id,args[1]);
                    break;
                case "departments":
                    ExportOrShowDepartments(args[1].toString());
                    break;
                case "jobtypeids":
                    ExportOrShowJobTypeIDs(args[1].toString());
                    break;
                default:
                    ShowArgumentOptions();
                    break;

            }//end of Action Switch
        }
        else
        {
            ShowArgumentOptions();
        }

    }

    //###############################
    // People Searching
    //###############################

    static void PeopleSearching(RosettaAPIWorker.PeopleSearchBy peopleSearchBy,String searchTerm)
    {
        //Initiate Rosetta API Worker
        RosettaAPIWorker rosettaAPIWrkr = new RosettaAPIWorker();

        //Query People by Search Parameters
        List<RosettaPerson> lRosettaPeople = rosettaAPIWrkr.GetPeopleBySearchTerm(peopleSearchBy,searchTerm.trim());

        //Loop Through Returned UCD Peeps
        for (RosettaPerson rosettaPrsn : lRosettaPeople) 
        {
            //For Readability
            System.out.println(" ");
            System.out.println("=========== " + rosettaPrsn.displayName + " =============");
            System.out.println(" ");

            //Pull Rosetta Person Class
            Class<?> clazz = rosettaPrsn.getClass();

            //Loop Through Fields and Display Values
            for (Field field : clazz.getDeclaredFields()) 
            {

                //Set Accessability
                field.setAccessible(true);

                try
                {
                    if(field.getName() != "lEmployeeAssociations" && field.getName() != "lStudentAssociations")
                    {
                        System.out.println(field.getName() + " = " + field.get(rosettaPrsn));
                    }
                    
                }
                catch(IllegalAccessException e)
                {
                    System.out.println("error occured");
                }
            
            }

            //For Readability
            System.out.println(" ");

            //Loop Through Employee Associations
            if(rosettaPrsn.lEmployeeAssociations.size() > 0)
            {
                System.out.println("Employee Associations:");    
                System.out.println(" ");

                for(RosettaEmployeeAssociation rea: rosettaPrsn.lEmployeeAssociations)
                {
                    //Pull Rosetta Employee Association Class
                    Class<?> classemp = rea.getClass();

                    //Loop Through Fields and Display Values
                    for (Field field : classemp.getDeclaredFields())
                    {
                        //Set Accessability
                        field.setAccessible(true);

                        try
                        {
                            System.out.println(field.getName() + " = " + field.get(rea));
                        }
                        catch(IllegalAccessException e)
                        {
                            System.out.println("error occured");
                        }
                    }

                    //For Readability
                    System.out.println(" ");
                }
            }

        
            //Loop Through Student Associations
            if(rosettaPrsn.lStudentAssociations.size() > 0)
            {

                System.out.println("Student Associations:");    
                System.out.println(" ");

                for(RosettaStudentAssociationShort rsas: rosettaPrsn.lStudentAssociations)
                {
                    //Pull Rosetta Student Association Class
                    Class<?> classstd = rsas.getClass();

                    //Loop Through Fields and Display Values
                    for (Field field : classstd.getDeclaredFields())
                    {
                        //Set Accessability
                        field.setAccessible(true);

                        try
                        {
                            System.out.println(field.getName() + " = " + field.get(rsas));
                        }
                        catch(IllegalAccessException e)
                        {
                            System.out.println("error occured");
                        }
                    }

                    //For Readability
                    System.out.println(" ");

                }//End of Student Associations Foreach

            }//End of Student Associations Size Check

            
        }//End lRosettaPeople For Loop
        
        System.out.println("==============================================");
        System.out.println(" ");
        System.out.println("Total Record Count: " + lRosettaPeople.size());
        System.out.println(" ");
    }

    //##############################
    // Student-Associations
    //##############################
    static void StudentSearching(RosettaAPIWorker.StudentSearchBy studentSearchBy,String searchTerm)
    {
        //Initiate Rosetta API Worker
        RosettaAPIWorker rosettaAPIWrkr = new RosettaAPIWorker();

        //Query Student Associations by Search Parameters
        List<RosettaStudentAssociation> lRosettaStudentAssocs = rosettaAPIWrkr.GetStudentAssociationsBySearchTeam(studentSearchBy,searchTerm.trim());

        //Loop Through Returned Rosetta Student Associations
        for(RosettaStudentAssociation rsa : lRosettaStudentAssocs)
        {
            //For Readability
            System.out.println(" ");
            System.out.println("=========== " + rsa.iamID + " =============");
            System.out.println(" ");

            //Pull Rosetta Employee Association Class
            Class<?> clazz = rsa.getClass();

            //Loop Through Fields and Display Values
            for (Field field : clazz.getDeclaredFields()) 
            {

                //Set Accessability
                field.setAccessible(true);

                try
                {
                    System.out.println(field.getName() + " = " + field.get(rsa));
                }
                catch(IllegalAccessException e)
                {
                    System.out.println("error occured");
                }
            
            }

            //For Readability
            System.out.println(" ");
        }


        System.out.println("Total Record Count: " + lRosettaStudentAssocs.size());
    }



    //###############################
    // Employee-Associations
    //###############################

    static void EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy employeeSearchBy,String searchTerm)
    {

        //Initiate Rosetta API Worker
        RosettaAPIWorker rosettaAPIWrkr = new RosettaAPIWorker();

        //Query Employee Associations by Search Parameters
        List<RosettaEmployeeAssociation> lRosettaEmplAssocs = rosettaAPIWrkr.GetEmployeeAssociationsBySearchTerm(employeeSearchBy,searchTerm.trim());

        //Loop Through Returned Rosetta Employee Associations
        for(RosettaEmployeeAssociation rea : lRosettaEmplAssocs)
        {
            //For Readability
            System.out.println(" ");
            System.out.println("=========== " + rea.employeeID + " =============");
            System.out.println(" ");

            //Pull Rosetta Employee Association Class
            Class<?> clazz = rea.getClass();

            //Loop Through Fields and Display Values
            for (Field field : clazz.getDeclaredFields()) 
            {

                //Set Accessability
                field.setAccessible(true);

                try
                {
                    System.out.println(field.getName() + " = " + field.get(rea));
                }
                catch(IllegalAccessException e)
                {
                    System.out.println("error occured");
                }
            
            }

            //For Readability
            System.out.println(" ");
        }


        System.out.println("Total Record Count: " + lRosettaEmplAssocs.size());

    }

    //#####################################
    // Export or Show Rosetta Departments
    //#####################################
    
    static void ExportOrShowDepartments(String esAction)
    {
        //Initiate Rosetta API Worker
        RosettaAPIWorker rosettaAPIWrkr = new RosettaAPIWorker();

        //Pull Departments Information for Employee Associations
        List<RosettaDepartment> lRosettaDepartments = rosettaAPIWrkr.GetRosettaDepartments();

        //Check for Action
        if(esAction.trim().equalsIgnoreCase("export"))
        {
            
            //Var for Local Date Time
            LocalDateTime ldt = LocalDateTime.now();

            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            String rptFileName = "Departments-" + ldt.format(dtFormatter) + ".csv";

            //Initiate Writer for Export File
            try(BufferedWriter bfWriter = new BufferedWriter(new FileWriter(rosettaAPIWrkr.exportLocation + rptFileName)))
            {   
                //Write Header Row
                bfWriter.write("Department_ID,Department_Title,Department_Short_Title,Subdivision_ID,Subdivision_Title,Subdivision_L4_ID,Subdivision_L4_Title,Division_ID,Division_Title,Organization_ID,Organization_Title,");
                bfWriter.newLine();

                //Loop Through Returned Rosetta Departments
                for(RosettaDepartment rdept : lRosettaDepartments)
                {
                    //Write Out Values to Reporting File
                    bfWriter.write(escapeCsv(rdept.departmentID));
                    bfWriter.write(escapeCsv(rdept.departmentTitle));
                    bfWriter.write(escapeCsv(rdept.departmentShortTitle));
                    bfWriter.write(escapeCsv(rdept.subdivisionID));
                    bfWriter.write(escapeCsv(rdept.subdivisionTitle));
                    bfWriter.write(escapeCsv(rdept.subdivisionL4ID));
                    bfWriter.write(escapeCsv(rdept.subdivisionL4Title));
                    bfWriter.write(escapeCsv(rdept.divisionID));
                    bfWriter.write(escapeCsv(rdept.divisionTitle));
                    bfWriter.write(escapeCsv(rdept.organizationID));
                    bfWriter.write(escapeCsv(rdept.organizationTitle));
                    bfWriter.newLine();
                }

                bfWriter.close();
            }
            catch(Exception eio)
            {
                System.out.println(eio.toString());
            }

        }
        else
        {

            //Display Departments Information
            for(RosettaDepartment rsd : lRosettaDepartments)
            {
                //For Readability
                System.out.println(" ");
                System.out.println("=========== " + rsd.departmentID + " =============");
                System.out.println(" ");

                //Pull Rosetta Department Class
                Class<?> clazz = rsd.getClass();

                //Loop Through Fields and Display Values
                for (Field field : clazz.getDeclaredFields()) 
                {

                    //Set Accessability
                    field.setAccessible(true);

                    try
                    {
                        System.out.println(field.getName() + " = " + field.get(rsd));
                    }
                    catch(IllegalAccessException e)
                    {
                        System.out.println("error occured");
                    }
                
                }

                //For Readability
                System.out.println(" ");
            }

        }

    }

    //#####################################
    // Export or Show Rosetta JobTypeIDs
    //#####################################

    static void ExportOrShowJobTypeIDs(String esAction)
    {
        //Initiate Rosetta API Worker
        RosettaAPIWorker rosettaAPIWrkr = new RosettaAPIWorker();

        //Pull JobTypeIDs Information for Employee Associations
        List<RosettaJobTypeID> lRosettaJobTypeIDs = rosettaAPIWrkr.GetRosettaJobTypeIDs();

        //Check for Action
        if(esAction.trim().equalsIgnoreCase("export"))
        {
            //Var for Local Date Time
            LocalDateTime ldt = LocalDateTime.now();

            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            String rptFileName = "JobTypeIDs-" + ldt.format(dtFormatter) + ".csv";

            //Initiate Writer for Export File
            try(BufferedWriter bfWriter = new BufferedWriter(new FileWriter(rosettaAPIWrkr.exportLocation + rptFileName)))
            {   
                //Write Header Row
                bfWriter.write("Job_Type_ID,Job_Type_Description,");
                bfWriter.newLine();

                //Loop Through Returned Rosetta JobTypeIDs
                for(RosettaJobTypeID rjtid : lRosettaJobTypeIDs)
                {
                    //Write Out Values to Reporting File
                    bfWriter.write(escapeCsv(rjtid.jobTypeID));
                    bfWriter.write(escapeCsv(rjtid.jobTypeDescription));
                    bfWriter.newLine();
                }

                bfWriter.close();
            }
            catch(Exception eio)
            {
                System.out.println(eio.toString());
            }

        }
        else
        {
            //Display JobTypeID Information
            for(RosettaJobTypeID rjti : lRosettaJobTypeIDs)
            {
                //For Readability
                System.out.println(" ");

                //Pull Rosetta Job Type ID Class
                Class<?> clazz = rjti.getClass();

                //Loop Through Fields and Display Values
                for (Field field : clazz.getDeclaredFields()) 
                {

                    //Set Accessability
                    field.setAccessible(true);

                    try
                    {
                        System.out.println(field.getName() + " = " + field.get(rjti));
                    }
                    catch(IllegalAccessException e)
                    {
                        System.out.println("error occured");
                    }
                
                }

            }
        }

        
    }

    //###############################
    // Display Agrument Options
    //###############################
    static void ShowArgumentOptions()
    {
        System.out.println(" ");
        System.out.println("Argument Options:");
        System.out.println("=======================");
        System.out.println("people-login <userid>");
        System.out.println("people-iam <iamid>");
        System.out.println("people-employee <employeeid>");
        System.out.println("people-email <emailaddress>");
        System.out.println("people-student <studentid>");
        System.out.println("people-department <departmentcode>");
        System.out.println("student-iam <iamid>");
        System.out.println("student-pidm <pidm>");
        System.out.println("student-studentid <studentid>");
        System.out.println("student-majorcode <majorcode>");
        System.out.println("student-collegecode <collegecode>");
        System.out.println("employee-iam <iamid>");
        System.out.println("employee-department <departmentcode>");
        System.out.println("employee-division <divisionid>");
        System.out.println("employee-organization <organizationid>");
        System.out.println("employee-subdivision <subdivisionid>");
        System.out.println("employee-subdivisionl4 <subdivisionl4id>");
        System.out.println("departments <export or show>");
        System.out.println("jobtypeids  <export or show>");
        System.out.println(" ");
        System.out.println(" ");
    }

    //###############################
    // Escape CSV Cell Values
    //###############################

    public static String escapeCsv(String input)
    {

        if (input == null || input.isEmpty()) {
            return ",";
        }

        String result = input;

        if (result.startsWith("0") || result.startsWith("-")) {
            result = "'" + result;
        }

        boolean needsQuotes = result.contains(",") ||
                            result.contains("\"") ||
                            result.contains("\n") ||
                            result.contains("\r");

        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }

        if (needsQuotes) {
            result = "\"" + result + "\"";
        }

        return result + ",";
    }
}