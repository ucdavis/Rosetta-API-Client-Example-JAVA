package edu.ucdavis.rosetta;

import java.util.ArrayList;
import java.util.List;

public class RosettaPerson 
{
    public String iamID = "";
    public String loginID = "";
    public String mothraID = "";
    public String employeeID = "";
    public String mailIDCampus = "";
    public String mailIDHealth = "";
    public String emailAddressCampus = "";
    public String emailAddressHealth = "";
    public String livedFirstName = "";
    public String livedLastName = "";
    public String displayName = "";
    public String provisioningStatusPrimary = "";
    public String provisioningStatusEmployee = "";
    public String provisioningStatusFaculty = "";
    public String provisioningStatusStudent = "";
    public boolean affiliationEmployee = false;
    public boolean affiliationFaculty = false;
    public boolean affiliationTemporaryAffiliate = false;
    public boolean affiliationStudent = false;
    public boolean affiliationStudentApplicant = false;
    public boolean affiliationHealthAffiliate = false;
    public boolean employmentIsAcademic = false;
    public boolean employmentIsAcademicSenate = false;
    public boolean employmentIsAcademicFederation = false;
    public boolean employmentIsFaculty = false;
    public boolean employmentIsTeachingFaculty = false;
    public boolean employmentIsLadderRank = false;
    public boolean employmentIsWithoutSalary = false;
    public boolean employmentIsMSP = false;
    public boolean employmentIsSSP = false;
    public boolean employmentIsManager = false;
    public boolean employmentIsCampusEmployee = false;
    public boolean employmentIsHealthEmployee = false;
    public List<RosettaEmployeeAssociation> lEmployeeAssociations = new ArrayList<>();
    public List<RosettaStudentAssociationShort> lStudentAssociations = new ArrayList<>();

    public RosettaPerson()
    {

    }

}
