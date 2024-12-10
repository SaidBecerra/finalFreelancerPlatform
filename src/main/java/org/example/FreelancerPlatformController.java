package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FreelancerPlatformController {
    // Create and manage accounts
    public Freelancer createFreelancer(int id, String username, String email, String password, String specialty, int experience, String bankInfo) {
        if (isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        // Create freelancer account
        Freelancer freelancer = new Freelancer(id, username, email, password, specialty, experience, bankInfo);
        // Insert freelancer into the database
        DatabaseManager.getInstance().insertAccount(freelancer, "Freelancer");
        return freelancer;
    }

    public Client createClient(int id, String username, String email, String password, String bankInfo) {
        if (isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        // Create client account
        Client client = new Client(id, username, email, password, bankInfo);
        // Insert client into the database
        DatabaseManager.getInstance().insertAccount(client, "Client");
        return client;
    }

    // Fetch an account by email and password (for login)
    public Accounts fetchAccountByEmailAndPassword(String email, String password) {
        // Return the account if found, else return null
        return DatabaseManager.getInstance().selectAccountByEmailAndPassword(email, password);
    }

    // Create a project
    public Projects createProject(int id, String projectName, Client client, Freelancer freelancer, String description, long budget, Date deadline) {
        Projects project = new Projects(id, projectName, client, freelancer, description, budget, deadline);
        DatabaseManager.getInstance().insertProject(project);
        return project;
    }

    // Mark a project as finished
    public void markProjectAsFinished(Projects project) {
        project.markAsFinished();
        DatabaseManager.getInstance().finishProject(project);
    }

    // Find project by ID
    public Projects findProjectById(int projectId) {
        return DatabaseManager.getInstance().selectProjectById(projectId);
    }

    // View projects for a specific account
    public void viewProjects(Accounts account) {
        List<Projects> projects;
        if (account instanceof Freelancer) {
            projects = DatabaseManager.getInstance().selectProjectsByFreelancer((Freelancer) account);
        } else if (account instanceof Client) {
            projects = DatabaseManager.getInstance().selectProjectsByClient((Client) account);
        } else {
            System.out.println("Invalid account type.");
            return;
        }

        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println("Projects for " + account.getUsername() + ":");
        for (Projects project : projects) {
            System.out.println("Project ID: " + project.getProjectID() +
                    ", Name: " + project.getProjectName());
        }
    }

    // View available projects (for freelancers)
    public void viewAvailableProjects() {
        ArrayList<Projects> availableProjects = DatabaseManager.getInstance().selectAvailableProjects();

        if (availableProjects.isEmpty()) {
            System.out.println("No available projects.");
            return;
        }

        System.out.println("Available Projects:");
        for (Projects project : availableProjects) {
            System.out.println("Project ID: " + project.getProjectID() +
                    ", Name: " + project.getProjectName() +
                    ", Budget: $" + project.getBudget() +
                    ", Deadline: " + project.getDeadline());
        }
    }

    // Add a review to a project
    public void addReviewToProject(Projects project, Reviews review) {
        if (project == null || review == null) {
            throw new IllegalArgumentException("Project and review cannot be null.");
        }

        DatabaseManager.getInstance().insertReview(review);
        System.out.println("Review added to the project successfully.");
    }

    // Add a review for a freelancer
    public void addReviewToFreelancer(Freelancer freelancer, Reviews review) {
        if (freelancer == null || review == null) {
            throw new IllegalArgumentException("Freelancer and review cannot be null.");
        }

        DatabaseManager.getInstance().insertReview(review);
        System.out.println("Review added to the freelancer successfully.");
    }

    public boolean assignProjectToFreelancer(Freelancer freelancer, Projects project) {
        if (project == null || project.getFreelancer() != null) {
            System.out.println("Project is already assigned to a freelancer or does not exist.");
            return false;
        }

        // Update the project with the freelancer
        project.assignFreelancer(freelancer);
        DatabaseManager.getInstance().assignFreelancerToProject(project, freelancer);

        System.out.println("Successfully assigned project: " + project.getProjectName());
        return true;
    }


    // Utility: Validate email format
    private boolean isEmailValid(String email) {
        return email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}