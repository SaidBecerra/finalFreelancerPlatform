package org.example;

import java.util.Date;

public class Projects {
    private final String projectName;
    private final int projectID;
    private final Client client;
    private Freelancer freelancer; // Made this mutable to allow assigning later
    private final String projectDescription;
    private final long budget;
    private final Date deadline;
    private boolean isFinished;

    public Projects(int projectID, String projectName, Client client, Freelancer freelancer, String projectDescription, long budget, Date deadline) {
        this.projectName = projectName;
        this.client = client;
        this.freelancer = freelancer;
        this.projectDescription = projectDescription;
        this.budget = budget;
        this.deadline = deadline;
        this.isFinished = false;
        this.projectID = projectID;

        // Add project to the Client's need-to-do list
        client.getNeedToDo().add(this);

        // Add to Freelancer's working list only if a Freelancer is provided
        if (freelancer != null) {
            freelancer.getWorkingOn().add(this);
        }
    }

    public String getProjectName() {
        return projectName;
    }

    public int getProjectID() {
        return projectID;
    }

    public Client getClient() {
        return client;
    }

    public Freelancer getFreelancer() {
        return freelancer;
    }

    public void assignFreelancer(Freelancer freelancer) {
        if (this.freelancer != null) {
            System.out.println("This project already has a freelancer assigned.");
        } else {
            this.freelancer = freelancer;
            freelancer.getWorkingOn().add(this);
            System.out.println("Freelancer " + freelancer.getUsername() + " assigned to project: " + projectName);
        }
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public long getBudget() {
        return budget;
    }

    public Date getDeadline() {
        return deadline;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void markAsFinished() {
        if (!isFinished) {
            isFinished = true;

            // Update Freelancer and Client lists if necessary
            if (freelancer != null) {
                freelancer.finishProject(this);
            }
            client.finishProject(this);

            System.out.println("Project marked as finished: " + projectName);
        } else {
            System.out.println("Project is already marked as finished: " + projectName);
        }
    }
}
