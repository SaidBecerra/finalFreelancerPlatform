package org.example;

import java.util.ArrayList;

public class Freelancer extends Accounts {
    private final String specialty;
    private final int yearsOfExperience;
    private final ArrayList<Projects> workedOn;
    private final ArrayList<Projects> workingOn;

    public Freelancer(int id, String username, String email, String password, String specialty, int yearsOfExperience, String bankInfo) {
        super(id, username, email, password, bankInfo);
        this.specialty = specialty;
        this.yearsOfExperience = yearsOfExperience;
        this.workedOn = new ArrayList<>();
        this.workingOn = new ArrayList<>();
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public ArrayList<Projects> getWorkedOn() {
        return workedOn;
    }

    public ArrayList<Projects> getWorkingOn() {
        return workingOn;
    }

    public void finishProject(Projects project) {
        if (workingOn.remove(project)) {
            workedOn.add(project);
            System.out.println("Project moved to WorkedOn list for Freelancer: " + getUsername());
        } else {
            System.out.println("Project not found in WorkingOn list for Freelancer: " + getUsername());
        }
    }
}
