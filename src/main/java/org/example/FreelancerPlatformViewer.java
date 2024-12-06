package org.example;

import java.util.ArrayList;

public class FreelancerPlatformViewer {
    // Display account details
    public void displayAccountDetails(Accounts account) {
        System.out.println("Account ID: " + account.getAccountID());
        System.out.println("Username: " + account.getUsername());
        System.out.println("Email: " + account.getEmail());
        System.out.println("Bank Info: " + account.getBankInfo());
    }

    // Display freelancer details
    public void displayFreelancerDetails(Freelancer freelancer) {
        displayAccountDetails(freelancer);
        System.out.println("Specialty: " + freelancer.getSpecialty());
        System.out.println("Years of Experience: " + freelancer.getYearsOfExperience());
    }

    // Display client details
    public void displayClientDetails(Client client) {
        displayAccountDetails(client);
        System.out.println("Projects To Do: " + client.getNeedToDo().size());
        System.out.println("Completed Projects: " + client.getCompleted().size());
    }

    // Display project details
    public void displayProjectDetails(Projects project) {
        System.out.println("Project ID: " + project.getProjectID());
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Description: " + project.getProjectDescription());
        System.out.println("Budget: " + project.getBudget());
        System.out.println("Deadline: " + project.getDeadline());
        System.out.println("Client: " + project.getClient().getUsername());
        System.out.println("Freelancer: " + project.getFreelancer().getUsername());
    }

    // Display reviews for an account
    public void displayReviews(ArrayList<Reviews> reviews) {
        if (reviews.isEmpty()) {
            System.out.println("No reviews available.");
        } else {
            for (Reviews review : reviews) {
                System.out.println("Review ID: " + review.getReviewID());
                System.out.println("Rating: " + review.getRating());
                System.out.println("Comment: " + review.getComment());
                System.out.println("----");
            }
        }
    }

    // Display list of projects
    public void displayProjects(ArrayList<Projects> projects) {
        if (projects.isEmpty()) {
            System.out.println("No projects available.");
        } else {
            for (Projects project : projects) {
                System.out.println("Project Name: " + project.getProjectName());
                System.out.println("Description: " + project.getProjectDescription());
                System.out.println("Budget: " + project.getBudget());
                System.out.println("Deadline: " + project.getDeadline());
                System.out.println("----");
            }
        }
    }
}
