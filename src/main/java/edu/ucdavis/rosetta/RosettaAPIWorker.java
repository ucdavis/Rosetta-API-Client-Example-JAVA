package edu.ucdavis.rosetta;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class RosettaAPIWorker {
    
    public String baseUrl;
    public String tokenUrl;
    private String _clientID;
    private String _clientSecret;
    private String _oauthToken;
    private String _oauthScopes;
    public String testID;
    public String exportLocation;
    public LocalDateTime expiresIn;

    public RosettaAPIWorker()
    {
        //Load the .env File
        Dotenv dotenv = Dotenv.load();

        //Load API Information
        _clientID = dotenv.get("ROSETTA_CLIENT_ID");
        _clientSecret = dotenv.get("ROSETTA_CLIENT_SECRET");
        _oauthScopes = dotenv.get("ROSETTA_SCOPES");
        baseUrl = dotenv.get("ROSETTA_BASE_URL");
        tokenUrl = dotenv.get("ROSETTA_OAUTH_URL");
        testID = dotenv.get("ROSETTA_TEST_ID");
        exportLocation = dotenv.get("ROSETTA_EXPORT_LOCATION");

        //Configure Intitial Expires In Value
        expiresIn = LocalDateTime.now().minusHours(1);
        
    }

    public enum PeopleSearchBy
    {
        iamid,
        loginid,
        email,
        employeeid,
        studentid,
        mailid,
        department
    }

    public enum EmployeeSearchBy
    {
        iamid,
        departmentid,
        divisionid,
        subdivisionid,
        subdivisionl4id,
        organizationid
    }

    public enum StudentSearchBy
    {
        iamid,
        pidm,
        studentid,
        majorcode,
        collegecode
    }

    public boolean CheckOAuthToken()
    {
        //Var for Return Status
        boolean bTokenStatus = true;

        //Check Token Expiration
        if(LocalDateTime.now().plusMinutes(1).isAfter(expiresIn))
        {

            //HttpClient for API Call to Rosetta API
            try(HttpClient raHttpClient  = HttpClient.newHttpClient())
            {

                //Initiate Object Mapper to Parse Returned Json
                ObjectMapper joMapper = new ObjectMapper();

                //Build Request with Custom Header for OAuth Call
                HttpRequest raHttpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(tokenUrl))
                        .header("client_id", _clientID)
                        .header("client_secret", _clientSecret)
                        .header("grant_type","CLIENT_CREDENTIALS")
                        .header("scope",_oauthScopes)
                        .POST(BodyPublishers.noBody())
                        .build();

                
                //Send Request via HTTP Client
                HttpResponse<String> raHttpResponse = raHttpClient.send(raHttpRequest, HttpResponse.BodyHandlers.ofString());

                //Check Return Status Code
                if(raHttpResponse.statusCode() == 200)
                {
                    //Create Json Object of Returned Json
                    JsonNode jnOAuthToken = joMapper.readTree(raHttpResponse.body());

                    //Check for Required Fields
                    if(jnOAuthToken.hasNonNull("access_token") && jnOAuthToken.hasNonNull("expires_in"))
                    {
                        //Load OAuth Access Token
                        _oauthToken = jnOAuthToken.get("access_token").asText();

                        //Update Expires In Value
                        expiresIn = LocalDateTime.now().plusSeconds(Long.parseLong(jnOAuthToken.get("expires_in").asText()));
                    }
                    else
                    {
                        bTokenStatus = false;
                    }
                    
                }
                else
                {
                    bTokenStatus = false;
                }

            }
            catch (Exception e) {
                bTokenStatus = false;
            }


        }//End of Expires In Check

        return bTokenStatus;
    }

    public RosettaJobTypeID ParseRosettaJobTypeIDJson(JsonNode jeJobTypeID)
    {
        //Initialize JobTypeID Object to Return
        RosettaJobTypeID rosettaJobTypeID = new RosettaJobTypeID();

        //Retrieve Job Type ID
        if(jeJobTypeID.hasNonNull("job_type_id"))
        {
            rosettaJobTypeID.jobTypeID = jeJobTypeID.get("job_type_id").asText();
        }

        //Retrieve Job Type Description
        if(jeJobTypeID.hasNonNull("job_type_description"))
        {
            rosettaJobTypeID.jobTypeDescription = jeJobTypeID.get("job_type_description").asText();
        }

        return rosettaJobTypeID;
    }

    public RosettaDepartment ParseRosettaDepartmentJson(JsonNode jeDepartment)
    {
        //Initialize Department to Return
        RosettaDepartment rosettaDepartment = new RosettaDepartment();

        //Retrieve Department ID
        if(jeDepartment.hasNonNull("department_id"))
        {
            rosettaDepartment.departmentID = jeDepartment.get("department_id").asText();
        }

        //Retrieve Department Title
        if(jeDepartment.hasNonNull("department_title"))
        {
            rosettaDepartment.departmentTitle = jeDepartment.get("department_title").asText();
        }

        //Retrieve Department Short Tiele
        if(jeDepartment.hasNonNull("department_short_title"))
        {
            rosettaDepartment.departmentShortTitle = jeDepartment.get("department_short_title").asText();
        }

        //Retrieve Subdivision ID
        if(jeDepartment.hasNonNull("subdivision_id"))
        {
            rosettaDepartment.subdivisionID = jeDepartment.get("subdivision_id").asText();
        }

        //Retrieve Subdivision Title
        if(jeDepartment.hasNonNull("subdivision_title"))
        {
            rosettaDepartment.subdivisionTitle = jeDepartment.get("subdivision_title").asText();
        }

        //Retrieve Subdivision L4 ID
        if(jeDepartment.hasNonNull("subdivision_l4_id"))
        {
            rosettaDepartment.subdivisionL4ID = jeDepartment.get("subdivision_l4_id").asText();
        }

        //Retrieve Subdivision L4 Title
        if(jeDepartment.hasNonNull("subdivision_l4_title"))
        {
            rosettaDepartment.subdivisionL4Title = jeDepartment.get("subdivision_l4_title").asText();
        }

        //Retrieve Division ID
        if(jeDepartment.hasNonNull("division_id"))
        {
            rosettaDepartment.divisionID = jeDepartment.get("division_id").asText();
        }

        //Retrieve Division Title
        if(jeDepartment.hasNonNull("division_title"))
        {
            rosettaDepartment.divisionTitle = jeDepartment.get("division_title").asText();
        }

        //Retrieve Organization ID
        if(jeDepartment.hasNonNull("organization_id"))
        {
            rosettaDepartment.organizationID = jeDepartment.get("organization_id").asText();
        }

        //Retrieve Organization Title
        if(jeDepartment.hasNonNull("organization_title"))
        {
            rosettaDepartment.organizationTitle = jeDepartment.get("organization_title").asText();
        }

        return rosettaDepartment;
    }

    public RosettaStudentAssociationShort ParseRosettaStudentAssocShortJson(JsonNode jeStudentAssocShrt)
    {
        //Initialize Student Association to Return
        RosettaStudentAssociationShort rosettaStudentAssoc = new RosettaStudentAssociationShort();

        //Retrieve College Code
        if(jeStudentAssocShrt.hasNonNull("college_code"))
        {
            rosettaStudentAssoc.collegeCode = jeStudentAssocShrt.get("college_code").asText();
        }

        //Retrieve College Title 
        if(jeStudentAssocShrt.hasNonNull("college_title"))
        {
            rosettaStudentAssoc.collegeTitle = jeStudentAssocShrt.get("college_title").asText();
        }

        //Retrieve Major Code
        if(jeStudentAssocShrt.hasNonNull("major_code"))
        {
            rosettaStudentAssoc.majorCode = jeStudentAssocShrt.get("major_code").asText();
        }

        //Retrieve Major Title
        if(jeStudentAssocShrt.hasNonNull("major_title"))
        {
            rosettaStudentAssoc.majorTitle = jeStudentAssocShrt.get("major_title").asText();
        }

        //Retrieve Academic Level
        if(jeStudentAssocShrt.hasNonNull("academic_level"))
        {
            rosettaStudentAssoc.academicLevel = jeStudentAssocShrt.get("academic_level").asText();
        }

        //Retrieve Class Level
        if(jeStudentAssocShrt.hasNonNull("class_level"))
        {
            rosettaStudentAssoc.classLevel = jeStudentAssocShrt.get("class_level").asText();
        }

        return rosettaStudentAssoc;
    }

    public RosettaStudentAssociation ParseRosettaStudentAssocJson(JsonNode jeStudentAssoc)
    {
        //Initialize Student Association to Return
        RosettaStudentAssociation rosettaStudentAssoc = new RosettaStudentAssociation();

        //Retrieve IAM ID
        if(jeStudentAssoc.hasNonNull("iam_id"))
        {
            rosettaStudentAssoc.iamID = jeStudentAssoc.get("iam_id").asText();
        }

        //Retrieve Student ID
        if(jeStudentAssoc.hasNonNull("student_id"))
        {
            rosettaStudentAssoc.studentID = jeStudentAssoc.get("student_id").asText();
        }

        //Retrieve PIDM
        if(jeStudentAssoc.hasNonNull("pidm"))
        {
            rosettaStudentAssoc.pidm = jeStudentAssoc.get("pidm").asText();
        }

        //Retrieve College Code
        if(jeStudentAssoc.hasNonNull("college_code"))
        {
            rosettaStudentAssoc.collegeCode = jeStudentAssoc.get("college_code").asText();
        }

        //Retrieve College Title 
        if(jeStudentAssoc.hasNonNull("college_title"))
        {
            rosettaStudentAssoc.collegeTitle = jeStudentAssoc.get("college_title").asText();
        }

        //Retrieve Major Code
        if(jeStudentAssoc.hasNonNull("major_code"))
        {
            rosettaStudentAssoc.majorCode = jeStudentAssoc.get("major_code").asText();
        }

        //Retrieve Major Title
        if(jeStudentAssoc.hasNonNull("major_title"))
        {
            rosettaStudentAssoc.majorTitle = jeStudentAssoc.get("major_title").asText();
        }

        //Retrieve Level Affiliation Code
        if(jeStudentAssoc.hasNonNull("lvl_affiliation_code"))
        {
            rosettaStudentAssoc.levelAffiliationCode = jeStudentAssoc.get("lvl_affiliation_code").asText();
        }

        //Retrieve Class Affiliation Code
        if(jeStudentAssoc.hasNonNull("cls_affiliation_code"))
        {
            rosettaStudentAssoc.classAffiliationCode = jeStudentAssoc.get("cls_affiliation_code").asText();
        }

        //Retrieve Rank
        if(jeStudentAssoc.hasNonNull("rank"))
        {
            rosettaStudentAssoc.rank = jeStudentAssoc.get("rank").asText();
        }

        return rosettaStudentAssoc;
    }

    public RosettaEmployeeAssociation ParseRosettaEmployeeAssocJson(JsonNode jeEmploymentAssoc)
    {
        //Initialize Employee Association to Return
        RosettaEmployeeAssociation rosettaEmplAssoc = new RosettaEmployeeAssociation();

        //Retrieve IAM ID
        if(jeEmploymentAssoc.hasNonNull("iam_id"))
        {
            rosettaEmplAssoc.iamID = jeEmploymentAssoc.get("iam_id").asText();
        }

        //Retrieve Employee Record
        if(jeEmploymentAssoc.hasNonNull("employee_record"))
        {
            rosettaEmplAssoc.employeeRecord = jeEmploymentAssoc.get("employee_record").asText();
        }

        //Retrieve Employee ID
        if(jeEmploymentAssoc.hasNonNull("employee_id"))
        {
            rosettaEmplAssoc.employeeID = jeEmploymentAssoc.get("employee_id").asText();
        }

        //Retrieve Position Number
        if(jeEmploymentAssoc.hasNonNull("position_number"))
        {
            rosettaEmplAssoc.positionNumber = jeEmploymentAssoc.get("position_number").asText();
        }

        //Retrieve Position Title
        if(jeEmploymentAssoc.hasNonNull("position_title"))
        {
            rosettaEmplAssoc.positionTitle = jeEmploymentAssoc.get("position_title").asText();
        }

        //Retrieve Relationship to Organization
        if(jeEmploymentAssoc.hasNonNull("relationship_to_organization"))
        {
            rosettaEmplAssoc.relationshipToOrganization = jeEmploymentAssoc.get("relationship_to_organization").asText();
        }

        //Retrieve Employee Classification
        if(jeEmploymentAssoc.hasNonNull("employee_classification"))
        {
            rosettaEmplAssoc.employeeClassification = jeEmploymentAssoc.get("employee_classification").asText();
        }

        //Retrieve Employee Classification Description
        if(jeEmploymentAssoc.hasNonNull("employee_classification_description"))
        {
            rosettaEmplAssoc.employeeClassificationDescription = jeEmploymentAssoc.get("employee_classification_description").asText();
        }

        //Retrieve Status
        if(jeEmploymentAssoc.hasNonNull("status"))
        {
            rosettaEmplAssoc.status = jeEmploymentAssoc.get("status").asText();
        }

        //Retrieve Hire Date
        if(jeEmploymentAssoc.hasNonNull("hire_date"))
        {
            rosettaEmplAssoc.hireDate = jeEmploymentAssoc.get("hire_date").asText();
        }

        //Retrieve Start Date
        if(jeEmploymentAssoc.hasNonNull("start_date"))
        {
            rosettaEmplAssoc.startDate = jeEmploymentAssoc.get("start_date").asText();
        }

        //Retrieve FTE Percentage
        if(jeEmploymentAssoc.hasNonNull("fte_percentage"))
        {
            rosettaEmplAssoc.ftePercentage = jeEmploymentAssoc.get("fte_percentage").asText();
        }

        //Retrieve Joy Type ID
        if(jeEmploymentAssoc.hasNonNull("job_type_id"))
        {
            rosettaEmplAssoc.jobTypeID = jeEmploymentAssoc.get("job_type_id").asText();
        }

        //Retrieve Job Type Description
        if(jeEmploymentAssoc.hasNonNull("job_type_description"))
        {
            rosettaEmplAssoc.jobTypeDescription = jeEmploymentAssoc.get("job_type_description").asText();
        }

        //Retrieve Organization ID
        if(jeEmploymentAssoc.hasNonNull("organization_id"))
        {
            rosettaEmplAssoc.organizationID = jeEmploymentAssoc.get("organization_id").asText();
        }

        //Retrieve Organization Title
        if(jeEmploymentAssoc.hasNonNull("organization_title"))
        {
            rosettaEmplAssoc.organizationTitle = jeEmploymentAssoc.get("organization_title").asText();
        }

        //Retrieve Division ID
        if(jeEmploymentAssoc.hasNonNull("division_id"))
        {
            rosettaEmplAssoc.divisionID = jeEmploymentAssoc.get("division_id").asText();
        }

        //Retrieve Division Title
        if(jeEmploymentAssoc.hasNonNull("division_title"))
        {
            rosettaEmplAssoc.divisionTitle = jeEmploymentAssoc.get("division_title").asText();
        }


        //Retrieve Subdivision ID
        if(jeEmploymentAssoc.hasNonNull("subdivision_id"))
        {
            rosettaEmplAssoc.subdivisionID = jeEmploymentAssoc.get("subdivision_id").asText();
        }

        //Retrieve Subdivision Title
        if(jeEmploymentAssoc.hasNonNull("subdivision_title"))
        {
            rosettaEmplAssoc.subdivisionTitle = jeEmploymentAssoc.get("subdivision_title").asText();
        }

        //Retrieve Subdivision L4 ID
        if(jeEmploymentAssoc.hasNonNull("subdivision_l4_id"))
        {
            rosettaEmplAssoc.subdivisionL4ID = jeEmploymentAssoc.get("subdivision_l4_id").asText();
        }

        //Retrieve Subdivision L4 Title
        if(jeEmploymentAssoc.hasNonNull("subdivision_l4_title"))
        {
            rosettaEmplAssoc.subdivisionL4Title = jeEmploymentAssoc.get("subdivision_l4_title").asText();
        }

        //Retrieve Business Unit ID
        if(jeEmploymentAssoc.hasNonNull("business_unit_id"))
        {
            rosettaEmplAssoc.businessUnitID = jeEmploymentAssoc.get("business_unit_id").asText();
        }

        //Retrieve Business Unit Title
        if(jeEmploymentAssoc.hasNonNull("business_unit_title"))
        {
            rosettaEmplAssoc.businessUnitTitle = jeEmploymentAssoc.get("business_unit_title").asText();
        }

        //Retrieve Department ID
        if(jeEmploymentAssoc.hasNonNull("department_id"))
        {
            rosettaEmplAssoc.departmentID = jeEmploymentAssoc.get("department_id").asText();
        }

        //Retrieve Department Title
        if(jeEmploymentAssoc.hasNonNull("department_title"))
        {
            rosettaEmplAssoc.departmentTitle = jeEmploymentAssoc.get("department_title").asText();
        }

        //Retrieve Department Short Title
        if(jeEmploymentAssoc.hasNonNull("department_short_title"))
        {
            rosettaEmplAssoc.departmentShortTitle = jeEmploymentAssoc.get("department_short_title").asText();
        }

        //Retrieve Reports to Position
        if(jeEmploymentAssoc.hasNonNull("reports_to_position"))
        {
            rosettaEmplAssoc.reportsToPosition = jeEmploymentAssoc.get("reports_to_position").asText();
        }

        //Retrieve Reports To IAM ID
        if(jeEmploymentAssoc.hasNonNull("reports_to_iam_id"))
        {
            rosettaEmplAssoc.reportsToIAMID = jeEmploymentAssoc.get("reports_to_iam_id").asText();
        }

        //Retrieve Reports to Employee ID
        if(jeEmploymentAssoc.hasNonNull("reports_to_employee_id"))
        {
            rosettaEmplAssoc.reportsToEmployeeID = jeEmploymentAssoc.get("reports_to_employee_id").asText();
        }

        //Retrieve Is Health Position
        if(jeEmploymentAssoc.hasNonNull("is_health_position"))
        {
            rosettaEmplAssoc.isHealthPosition = jeEmploymentAssoc.get("is_health_position").asText();
        }

        //Retrieve Is Campus Position
        if(jeEmploymentAssoc.hasNonNull("is_campus_position"))
        {
            rosettaEmplAssoc.isCampusPosition = jeEmploymentAssoc.get("is_campus_position").asText();
        }


        return rosettaEmplAssoc;
    }

    public RosettaPerson ParseRosettaPersonJson(JsonNode jePeople)
    {
        //Initialize Person to Return
        RosettaPerson rosettaPerson = new RosettaPerson();

        //Retrieve Display Name
        if(jePeople.hasNonNull("displayname"))
        {
            rosettaPerson.displayName = jePeople.get("displayname").asText();
        }

        //Retrieve IAM ID
        if(jePeople.hasNonNull("iam_id"))
        {
            rosettaPerson.iamID = jePeople.get("iam_id").asText();
        }

        //Retrieve IDs
        if(jePeople.hasNonNull("id"))
        {

            //Pull ID Node
            JsonNode jeIDs = jePeople.get("id");

            //Retrieve IAM ID
            if(jeIDs.hasNonNull("iam_id"))
            {
                rosettaPerson.iamID = jeIDs.get("iam_id").asText();
            }

            //Retrieve Login ID
            if(jeIDs.hasNonNull("login_id"))
            {
                rosettaPerson.loginID = jeIDs.get("login_id").asText();
            }

            //Retrieve Mothra ID
            if(jeIDs.hasNonNull("mothra_id"))
            {
                rosettaPerson.mothraID = jeIDs.get("mothra_id").asText();
            }

            //Retrieve Employee ID
            if(jeIDs.hasNonNull("employee_id"))
            {
                rosettaPerson.employeeID = jeIDs.get("employee_id").asText();
            }

            //Retrieve Mail IDs
            if(jeIDs.hasNonNull("mail_id"))
            {
                //Pull Mail ID Node
                JsonNode jeIDsMail = jeIDs.get("mail_id");

                //Check for Campus Mail ID
                if(jeIDsMail.hasNonNull("campus"))
                {
                    rosettaPerson.mailIDCampus = jeIDsMail.get("campus").asText();
                }

                //Check for Health Mail ID
                if(jeIDsMail.hasNonNull("health"))
                {
                    rosettaPerson.mailIDHealth = jeIDsMail.get("health").asText();
                }

            }

        }//End of IDs

        //Retrieve Names
        if(jePeople.hasNonNull("name"))
        {
            //Retrieve Name Node
            JsonNode jeNames = jePeople.get("name");

            //Check for Lived First Name
            if(jeNames.hasNonNull("lived_first_name"))
            {
                rosettaPerson.livedFirstName = jeNames.get("lived_first_name").asText();
            }

            //Check for Lived Last Name
            if(jeNames.hasNonNull("lived_last_name"))
            {
                rosettaPerson.livedLastName = jeNames.get("lived_last_name").asText();
            }

            //Check for Lived Pronouns
            if(jeNames.hasNonNull("lived_pronouns"))
            {
                rosettaPerson.livedPronouns = jeNames.get("lived_pronouns").asText();
            }

        }//End of Names Checks

        //Retrieve Email Addresses
        if(jePeople.hasNonNull("email"))
        {
            //Retrieve Email Node
            JsonNode jeEmailAddress = jePeople.get("email");

            //Check for Campus Email Address
            if(jeEmailAddress.hasNonNull("campus"))
            {
                rosettaPerson.emailAddressCampus = jeEmailAddress.get("campus").asText();
            }

            //Check for Health Email Address
            if(jeEmailAddress.hasNonNull("health"))
            {
                rosettaPerson.emailAddressHealth = jeEmailAddress.get("health").asText();
            }

        }//End of Email Addresses

        //Retrieve Provisioning Statuses
        if(jePeople.hasNonNull("provisioning_status"))
        {
            //Retrieve Provisioning Statuses 
            JsonNode jeProvisioningStatus = jePeople.get("provisioning_status");

            //Retrieve Primary Provisioning Status
            if(jeProvisioningStatus.hasNonNull("primary"))
            {
                rosettaPerson.provisioningStatusPrimary = jeProvisioningStatus.get("primary").asText();
            }

            //Retrieve Employee Provisioning Status
            if(jeProvisioningStatus.hasNonNull("employee"))
            {
                rosettaPerson.provisioningStatusEmployee = jeProvisioningStatus.get("employee").asText();
            }

            //Retrieve Faculty Provisioning Status
            if(jeProvisioningStatus.hasNonNull("faculty"))
            {
                rosettaPerson.provisioningStatusFaculty = jeProvisioningStatus.get("faculty").asText();
            }

            //Retrieve Student Provisioning Status
            if(jeProvisioningStatus.hasNonNull("student"))
            {
                rosettaPerson.provisioningStatusStudent = jeProvisioningStatus.get("student").asText();
            }


        }//End of Provisioning Status

        //Retrieve Affiliation
        if(jePeople.hasNonNull("affiliation"))
        {

            //Retreive Affiliations
            JsonNode jeAffiliation = jePeople.get("affiliation");

            //Retrieve Employee Affiliation
            if(jeAffiliation.hasNonNull("employee"))
            {
                
                //Confirm Employee Affiliation
                if(jeAffiliation.get("employee").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.affiliationEmployee = true;
                }
                else
                {
                    rosettaPerson.affiliationEmployee = false;
                }
                
            }

            //Retrieve Faculty Affiliation
            if(jeAffiliation.hasNonNull("faculty"))
            {

                //Confirm Faculty Affiliation
                if(jeAffiliation.get("faculty").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.affiliationFaculty = true;
                }
                else
                {
                    rosettaPerson.affiliationFaculty = false;
                }
                
            }

            //Retrieve Temporary Affiliation
            if(jeAffiliation.hasNonNull("temporary_affiliate"))
            {

                //Confirm Temporary Affiliation
                if(jeAffiliation.get("temporary_affiliate").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.affiliationTemporaryAffiliate = true;
                }
                else
                {
                    rosettaPerson.affiliationTemporaryAffiliate = false;
                }
                
            }

            //Retrieve Student Affiliation
            if(jeAffiliation.hasNonNull("student"))
            {

                //Confirm Student Affiliation
                if(jeAffiliation.get("student").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.affiliationStudent = true;
                }
                else
                {
                    rosettaPerson.affiliationStudent = false;
                }
                
            }

            //Retrieve Student Applicant Affiliation
            if(jeAffiliation.hasNonNull("student_applicant"))
            {

                //Confirm Student Affiliation
                if(jeAffiliation.get("student_applicant").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.affiliationStudentApplicant = true;
                }
                else
                {
                    rosettaPerson.affiliationStudentApplicant = false;
                }
                
            }

            //Retrieve Health Affiliation
            if(jeAffiliation.hasNonNull("health_affiliate"))
            {

                //Confirm Health Affiliation
                if(jeAffiliation.get("health_affiliate").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.affiliationHealthAffiliate = true;
                }
                else
                {
                    rosettaPerson.affiliationHealthAffiliate = false;
                }
                
            }
        
        }//End of Affiliations 

        //Retrieve Employment Status
        if(jePeople.hasNonNull("employment_status"))
        {
            //Retrieve Employment Statuses
            JsonNode jeEmploymentStatus = jePeople.get("employment_status");

            //Retrieve Academic Status
            if(jeEmploymentStatus.hasNonNull("is_academic"))
            {

                //Confirm Academic Status
                if(jeEmploymentStatus.get("is_academic").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsAcademic = true;
                }
                else
                {
                    rosettaPerson.employmentIsAcademic = false;
                }
                
            }

            //Retrieve Academic Senate Status
            if(jeEmploymentStatus.hasNonNull("is_academic_senate"))
            {

                //Confirm Academic Senate Status
                if(jeEmploymentStatus.get("is_academic_senate").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsAcademicSenate = true;
                }
                else
                {
                    rosettaPerson.employmentIsAcademicSenate = false;
                }
                
            }

            //Retrieve Academic Federation Status
            if(jeEmploymentStatus.hasNonNull("is_academic_federation"))
            {

                //Confirm Academic Federation Status
                if(jeEmploymentStatus.get("is_academic_federation").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsAcademicFederation = true;
                }
                else
                {
                    rosettaPerson.employmentIsAcademicFederation = false;
                }
                
            }


            //Retrieve Faculty Status
            if(jeEmploymentStatus.hasNonNull("is_faculty"))
            {

                //Confirm Faculty Status
                if(jeEmploymentStatus.get("is_faculty").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsFaculty = true;
                }
                else
                {
                    rosettaPerson.employmentIsFaculty = false;
                }
                
            }

            //Retrieve Teaching Faculty Status
            if(jeEmploymentStatus.hasNonNull("is_teaching_faculty"))
            {

                //Confirm Teaching Faculty Status
                if(jeEmploymentStatus.get("is_teaching_faculty").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsTeachingFaculty = true;
                }
                else
                {
                    rosettaPerson.employmentIsTeachingFaculty = false;
                }
                
            }

            //Retrieve Ladder Rank Status
            if(jeEmploymentStatus.hasNonNull("is_ladder_rank"))
            {

                //Confirm Ladder Rank Status
                if(jeEmploymentStatus.get("is_ladder_rank").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsLadderRank = true;
                }
                else
                {
                    rosettaPerson.employmentIsLadderRank = false;
                }
                
            }

            //Retrieve Without Salary Status
            if(jeEmploymentStatus.hasNonNull("is_without_salary"))
            {

                //Confirm Without Salary Status
                if(jeEmploymentStatus.get("is_without_salary").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsWithoutSalary = true;
                }
                else
                {
                    rosettaPerson.employmentIsWithoutSalary = false;
                }
                
            }

            //Retrieve MSP Status
            if(jeEmploymentStatus.hasNonNull("is_msp"))
            {
                
                //Confirm MSP Status
                if(jeEmploymentStatus.get("is_msp").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsMSP = true;
                }
                else
                {
                    rosettaPerson.employmentIsMSP = false;
                }
                
            }

            //Retrieve SSP Status
            if(jeEmploymentStatus.hasNonNull("is_ssp"))
            {

                //Confirm SSP Status
                if(jeEmploymentStatus.get("is_ssp").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsSSP = true;
                }
                else
                {
                    rosettaPerson.employmentIsSSP = false;
                }
                
            }

            //Retrieve Manager Status
            if(jeEmploymentStatus.hasNonNull("is_manager"))
            {

                //Confirm Manager Status
                if(jeEmploymentStatus.get("is_manager").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsManager = true;
                }
                else
                {
                    rosettaPerson.employmentIsManager = false;
                }
                
            }

            //Retrieve Campus Employee Status
            if(jeEmploymentStatus.hasNonNull("is_campus_employee"))
            {

                //Confirm Campus Employee Status
                if(jeEmploymentStatus.get("is_campus_employee").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsCampusEmployee = true;
                }
                else
                {
                    rosettaPerson.employmentIsCampusEmployee = false;
                }
                
            }

            //Retrieve Health Employee Status
            if(jeEmploymentStatus.hasNonNull("is_health_employee"))
            {

                //Confirm Health Employee Status
                if(jeEmploymentStatus.get("is_health_employee").asText().toUpperCase().equals("Y"))
                {
                    rosettaPerson.employmentIsHealthEmployee = true;
                }
                else
                {
                    rosettaPerson.employmentIsHealthEmployee = false;
                }
                
            }


        }//End of Employment Statuses

        //Check for Employment Associations
        if(jePeople.hasNonNull("employee_association") && jePeople.get("employee_association").isArray())
        {
            //Retrieve Employment Associations Node
            JsonNode jeEmploymentAssociations = jePeople.get("employee_association");

            //Loop Through Each Employment Association
            for(JsonNode jeEmplAssociation : jeEmploymentAssociations)
            {
                rosettaPerson.lEmployeeAssociations.add(ParseRosettaEmployeeAssocJson(jeEmplAssociation));
            }

            //Update Employee Associations with IAM ID
            if(rosettaPerson.iamID.isEmpty() == false && rosettaPerson.lEmployeeAssociations.size() > 0)
            {
                for(RosettaEmployeeAssociation rea : rosettaPerson.lEmployeeAssociations)
                {
                    rea.iamID = rosettaPerson.iamID;
                }
            }

        }//End of Employment Associations

        //Check for Student Associations
        if(jePeople.hasNonNull("student_association") && jePeople.get("student_association").isArray())
        {
            //Retrieve Student Associations Node
            JsonNode jeStudentAssociations = jePeople.get("student_association");

            for(JsonNode jeStdtAssociation : jeStudentAssociations)
            {
                rosettaPerson.lStudentAssociations.add(ParseRosettaStudentAssocShortJson(jeStdtAssociation));
            }

        }//End of Student Associations

        return rosettaPerson;
    }

    public List<RosettaPerson> GetPeopleBySearchTerm(PeopleSearchBy searchBy, String searchTerm)
    {
        //Var for Return List
        List<RosettaPerson> lRosettaPeople = new ArrayList<>();

        //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Var for Search Result Limit
        int nSrchRsltLimit = 100;

        //Var for Search Result Offset
        int nSrchRsltOffset = 0;

        //Var for Retrieve More Search Results
        boolean bRetrMoreSrchRslts = true;

        do
        {
            //Check OAuth Token
            if(CheckOAuthToken() == true)
            {
                //HttpClient for API Call to Rosetta API
                try(HttpClient raHttpClient  = HttpClient.newHttpClient())
                {
                    //Var for Accounts URL
                    String peopleURL =  baseUrl + "people?"+ searchBy.toString() + "=" + searchTerm + "&offset=" + Integer.toString(nSrchRsltOffset) + "&limit=" + Integer.toString(nSrchRsltLimit) + "&count=true"; //&affiliationState=all

                    //Build Request for People Lookup
                    HttpRequest peopleHttpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(peopleURL))
                            .header("Authorization","Bearer " + _oauthToken)
                            .GET()
                            .build();

                    //Send Accounts Request 
                    HttpResponse<String> peopleHttpResponse = raHttpClient.send(peopleHttpRequest, HttpResponse.BodyHandlers.ofString());

                    //Check Return Status Code
                    if(peopleHttpResponse.statusCode() == 200)
                    {

                        //Pull X-Total-Count and X-Response-Count Values
                        if(peopleHttpResponse.headers().firstValue("x-total-count").isPresent() &&
                           peopleHttpResponse.headers().firstValue("x-response-count").isPresent())
                        {
                            //Determine Header Values Counts
                            int nTotalCnt = peopleHttpResponse.headers().firstValue("x-total-count").map(Integer::parseInt).orElse(0);
                            int nRspnCnt = peopleHttpResponse.headers().firstValue("x-response-count").map(Integer::parseInt).orElse(0);

                            //Check Total and Reponse Counts are Not Empty
                            if(nTotalCnt > 0 && nRspnCnt > 0)
                            {
                                //Create Json Object of Accounts Json Data
                                JsonNode jnPeopleData = joMapper.readTree(peopleHttpResponse.body());

                                //Loop Through Accounts Information
                                for(JsonNode jnPerson : jnPeopleData)
                                {
                                    //Add Rosetta Person to Returned People List
                                    lRosettaPeople.add(ParseRosettaPersonJson(jnPerson));
                                }

                                //Increment Offset
                                nSrchRsltOffset += nSrchRsltLimit;

                                //Check Offset to Total Count
                                if(nSrchRsltOffset >= nTotalCnt)
                                {
                                    bRetrMoreSrchRslts = false;
                                }


                            }
                            else
                            {
                                bRetrMoreSrchRslts = false;
                            }//End of nTotalCnt and nRspnCnt Empty Checks
                            
                        }
                        else
                        {
                            bRetrMoreSrchRslts = false;
                        }//End of Return Header Counts Checks
                        
                    }
                    else
                    {
                        bRetrMoreSrchRslts = false;
                    }//End of Status Code Check

                }
                catch (Exception e) {
                    bRetrMoreSrchRslts = false;
                }//End of HttpClient

            }//End of CheckOAuthToken
        }
        while(bRetrMoreSrchRslts == true);

        return lRosettaPeople;
    }

    public List<RosettaStudentAssociation> GetStudentAssociationsBySearchTeam(StudentSearchBy searchBy,String searchTerm)
    {
        //Var for List to Return
        List<RosettaStudentAssociation> lStudentAssociations = new ArrayList<>();

        //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Var for Search Result Limit
        int nSrchRsltLimit = 200;

        //Var for Search Result Offset
        int nSrchRsltOffset = 0;

        //Var for Retrieve More Search Results
        boolean bRetrMoreSrchRslts = true;

        do
        {
            //Check OAuth Token
            if(CheckOAuthToken() == true)
            {

                //HttpClient for API Call to Rosetta API
                try(HttpClient raHttpClient  = HttpClient.newHttpClient())
                {
                    //Var for Student Associations URL
                    String studentURL =  baseUrl + "student-association?"+ searchBy.toString() + "=" + searchTerm + "&offset=" + Integer.toString(nSrchRsltOffset) + "&limit=" + Integer.toString(nSrchRsltLimit) + "&count=true";

                    //Build Request for Student Associations Lookup
                    HttpRequest studentHttpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(studentURL))
                            .header("Authorization","Bearer " + _oauthToken)
                            .GET()
                            .build();

                    //Send Student Associations Request 
                    HttpResponse<String> studentHttpResponse = raHttpClient.send(studentHttpRequest, HttpResponse.BodyHandlers.ofString());

                    //Check Return Status Code
                    if(studentHttpResponse.statusCode() == 200)
                    {

                        //Pull X-Total-Count and X-Response-Count Values
                        if(studentHttpResponse.headers().firstValue("x-total-count").isPresent() &&
                           studentHttpResponse.headers().firstValue("x-response-count").isPresent())
                        {
                            //Determine Header Values Counts
                            int nTotalCnt = studentHttpResponse.headers().firstValue("x-total-count").map(Integer::parseInt).orElse(0);
                            int nRspnCnt = studentHttpResponse.headers().firstValue("x-response-count").map(Integer::parseInt).orElse(0);

                            //Check Total and Reponse Counts are Not Empty
                            if(nTotalCnt > 0 && nRspnCnt > 0)
                            {
                                //Create Json Object of Student Associations Json Data
                                JsonNode jnStudentsData = joMapper.readTree(studentHttpResponse.body());

                                //Loop Through Student Association Information
                                for(JsonNode jnStudent : jnStudentsData)
                                {
                                    //Add Rosetta Student Association to Returned Student List
                                    lStudentAssociations.add(ParseRosettaStudentAssocJson(jnStudent));
                                }

                                //Increment Offset
                                nSrchRsltOffset += nSrchRsltLimit;

                                //Check Offset to Total Count
                                if(nSrchRsltOffset >= nTotalCnt)
                                {
                                    bRetrMoreSrchRslts = false;
                                }


                            }
                            else
                            {
                                bRetrMoreSrchRslts = false;
                            }//End of nTotalCnt and nRspnCnt Empty Checks
                            
                        }
                        else
                        {
                            bRetrMoreSrchRslts = false;
                        }//End of Return Header Counts Checks
                        
                    }
                    else
                    {
                        bRetrMoreSrchRslts = false;
                    }//End of Status Code Check

                }
                catch (Exception e) {
                    bRetrMoreSrchRslts = false;
                }//End of HttpClient

            }//End of CheckOAuthToken
        }
        while(bRetrMoreSrchRslts == true);

        return lStudentAssociations;
    }

    public List<RosettaEmployeeAssociation> GetEmployeeAssociationsBySearchTerm(EmployeeSearchBy searchBy, String searchTerm)
    {
        //Var for List to Return
        List<RosettaEmployeeAssociation> lEmployeeAssociations = new ArrayList<>();

        //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Var for Search Result Limit
        int nSrchRsltLimit = 200;

        //Var for Search Result Offset
        int nSrchRsltOffset = 0;

        //Var for Retrieve More Search Results
        boolean bRetrMoreSrchRslts = true;

        do
        {
            //Check OAuth Token
            if(CheckOAuthToken() == true)
            {

                //HttpClient for API Call to Rosetta API
                try(HttpClient raHttpClient  = HttpClient.newHttpClient())
                {
                    //Var for Employee Associations URL
                    String employeeURL =  baseUrl + "employee-association?"+ searchBy.toString() + "=" + searchTerm + "&offset=" + Integer.toString(nSrchRsltOffset) + "&limit=" + Integer.toString(nSrchRsltLimit) + "&count=true";

                    //Build Request for Employee Associations Lookup
                    HttpRequest employeeHttpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(employeeURL))
                            .header("Authorization","Bearer " + _oauthToken)
                            .GET()
                            .build();

                    //Send Employee Associations Request 
                    HttpResponse<String> employeeHttpResponse = raHttpClient.send(employeeHttpRequest, HttpResponse.BodyHandlers.ofString());

                    //Check Return Status Code
                    if(employeeHttpResponse.statusCode() == 200)
                    {

                        //Pull X-Total-Count and X-Response-Count Values
                        if(employeeHttpResponse.headers().firstValue("x-total-count").isPresent() &&
                           employeeHttpResponse.headers().firstValue("x-response-count").isPresent())
                        {
                            //Determine Header Values Counts
                            int nTotalCnt = employeeHttpResponse.headers().firstValue("x-total-count").map(Integer::parseInt).orElse(0);
                            int nRspnCnt = employeeHttpResponse.headers().firstValue("x-response-count").map(Integer::parseInt).orElse(0);

                            //Check Total and Reponse Counts are Not Empty
                            if(nTotalCnt > 0 && nRspnCnt > 0)
                            {
                                //Create Json Object of Employee Associations Json Data
                                JsonNode jnEmployeeData = joMapper.readTree(employeeHttpResponse.body());

                                //Loop Through Employee Association Information
                                for(JsonNode jnEmployee : jnEmployeeData)
                                {
                                    //Add Rosetta Employee Association to Returned Employee List
                                    lEmployeeAssociations.add(ParseRosettaEmployeeAssocJson(jnEmployee));
                                }

                                //Increment Offset
                                nSrchRsltOffset += nSrchRsltLimit;

                                //Check Offset to Total Count
                                if(nSrchRsltOffset >= nTotalCnt)
                                {
                                    bRetrMoreSrchRslts = false;
                                }


                            }
                            else
                            {
                                bRetrMoreSrchRslts = false;
                            }//End of nTotalCnt and nRspnCnt Empty Checks
                            
                        }
                        else
                        {
                            bRetrMoreSrchRslts = false;
                        }//End of Return Header Counts Checks
                        
                    }
                    else
                    {
                        bRetrMoreSrchRslts = false;
                    }//End of Status Code Check

                }
                catch (Exception e) {
                    bRetrMoreSrchRslts = false;
                }//End of HttpClient

            }//End of CheckOAuthToken
        }
        while(bRetrMoreSrchRslts == true);

        return lEmployeeAssociations;
    }

    
    public List<RosettaDepartment> GetRosettaDepartments()
    {
        //Var for List to Return
        List<RosettaDepartment> lDepartments = new ArrayList<>();

        //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Var for Search Result Limit
        int nSrchRsltLimit = 3000;

        //Check OAuth Token
        if(CheckOAuthToken() == true)
        {

            //HttpClient for API Call to Rosetta API
            try(HttpClient raHttpClient  = HttpClient.newHttpClient())
            {
                //Var for Departments URL
                String departmentsURL =  baseUrl + "employee-association/departments?limit=" + Integer.toString(nSrchRsltLimit);

                //Build Request for Departments Lookup
                HttpRequest departmentsHttpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(departmentsURL))
                        .header("Authorization","Bearer " + _oauthToken)
                        .GET()
                        .build();

                //Send Departments Request 
                HttpResponse<String> departmentsHttpResponse = raHttpClient.send(departmentsHttpRequest, HttpResponse.BodyHandlers.ofString());

                //Check Return Status Code
                if(departmentsHttpResponse.statusCode() == 200)
                {

                    //Create Json Object of Department Json Data
                    JsonNode jnDepartmentsData = joMapper.readTree(departmentsHttpResponse.body());

                    //Loop Through Employee Association Information
                    for(JsonNode jnDepartment : jnDepartmentsData)
                    {
                        //Add Rosetta Department to Returned Department List
                        lDepartments.add(ParseRosettaDepartmentJson(jnDepartment));
                    }

                }

            }
            catch (Exception e) {
                System.out.println(e);
            }//End of HttpClient

        }//End of CheckOAuthToken
        
        return lDepartments;
    }
    
    public List<RosettaJobTypeID> GetRosettaJobTypeIDs()
    {
        //Var for List to Return
        List<RosettaJobTypeID> lJobTypeIDs = new ArrayList<>();

        //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Check OAuth Token
        if(CheckOAuthToken() == true)
        {

            //HttpClient for API Call to Rosetta API
            try(HttpClient raHttpClient  = HttpClient.newHttpClient())
            {
                //Var for JobTypeIDs URL
                String jobtypeidsURL =  baseUrl + "employee-association/jobtypeids?";

                //Build Request for Job Type IDs Lookup
                HttpRequest jobtypeidsHttpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(jobtypeidsURL))
                        .header("Authorization","Bearer " + _oauthToken)
                        .GET()
                        .build();

                //Send Job Type IDs Request 
                HttpResponse<String> jobtypeidsHttpResponse = raHttpClient.send(jobtypeidsHttpRequest, HttpResponse.BodyHandlers.ofString());

                //Check Return Status Code
                if(jobtypeidsHttpResponse.statusCode() == 200)
                {

                    //Create Json Object of JobTypeIDs Json Data
                    JsonNode jnJobTypeIDsData = joMapper.readTree(jobtypeidsHttpResponse.body());

                    //Loop Through JobTypeID Information
                    for(JsonNode jnJobTypeIDInfo : jnJobTypeIDsData)
                    {
                        //Add Rosetta JobTypeID Information to Returned List
                        lJobTypeIDs.add(ParseRosettaJobTypeIDJson(jnJobTypeIDInfo));
                    }

                }

            }
            catch (Exception e) {
                System.out.println(e);
            }//End of HttpClient

        }//End of CheckOAuthToken

        return lJobTypeIDs;
    }

}
