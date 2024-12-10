package org.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class FreelancerPlatformSystem {

    private static final FreelancerPlatformController controller = new FreelancerPlatformController();
    private static final FreelancerPlatformViewer viewer = new FreelancerPlatformViewer();
    private static Accounts loggedInAccount;

    public Accounts getLoggedInAccount() {
        return loggedInAccount;
    }

    public void initializeSystem() {
        System.out.println("Initializing system...");
    }

    public void showLoginMenu(Scanner scanner) {
        System.out.println("Please login or create a new account:");
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.print("Please enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> login(scanner);
            case 2 -> createAccount(scanner);
            default -> System.out.println("Invalid choice, please try again.");
        }
    }

    public void login(Scanner scanner) {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        loggedInAccount = controller.fetchAccountByEmailAndPassword(email, password);

        if (loggedInAccount == null) {
            System.out.println("Invalid credentials. Please try again.");
        } else {
            System.out.println("Logged in successfully as " + loggedInAccount.getUsername());
        }
    }

    public void createAccount(Scanner scanner) {
        System.out.println("What type of account would you like to create?");
        System.out.println("1. Freelancer");
        System.out.println("2. Client");
        System.out.print("Please enter your choice: ");
        int accountType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter the ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        // Email validation before account creation
        if (isEmailValid(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        System.out.print("Enter your bank info: ");
        String bankInfo = scanner.nextLine();

        if (accountType == 1) { // Freelancer
            System.out.print("Enter your specialty: ");
            String specialty = scanner.nextLine();
            System.out.print("Enter your years of experience: ");
            int experience = scanner.nextInt();
            scanner.nextLine();

            loggedInAccount = controller.createFreelancer(id, username, email, password, specialty, experience, bankInfo);
        } else if (accountType == 2) { // Client
            loggedInAccount = controller.createClient(id, username, email, password, bankInfo);
        } else {
            System.out.println("Invalid choice. Please try again.");
        }

        System.out.println("Account created successfully! You are now logged in as " + loggedInAccount.getUsername());
        viewer.displayAccountDetails(loggedInAccount);
    }

    public void freelancerMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("Freelancer Menu:");
            System.out.println("1. View available projects");
            System.out.println("2. View your projects");
            System.out.println("3. Take on a project");
            System.out.println("4. Mark a project as completed");
            System.out.println("5. Leave a review");
            System.out.println("0. Logout");
            System.out.print("Please enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewAvailableProjects();
                case 2 -> viewProjects();
                case 3 -> takeOnProject(scanner);
                case 4 -> markProjectAsCompleted(scanner);
                case 5 -> leaveReview(scanner);
                case 0 -> {
                    loggedInAccount = null;
                    System.out.println("Logged out.");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    public void clientMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("Client Menu:");
            System.out.println("1. Create a project");
            System.out.println("2. View your projects");
            System.out.println("3. Leave a review for a freelancer");
            System.out.println("0. Logout");
            System.out.print("Please enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> createProject(scanner);
                case 2 -> viewProjects();
                case 3 -> leaveReviewForFreelancer(scanner);
                case 0 -> {
                    loggedInAccount = null;
                    System.out.println("Logged out.");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void createProject(Scanner scanner) {
        if (!(loggedInAccount instanceof Client)) {
            System.out.println("Only clients can create projects.");
            return;
        }

        try {
            System.out.print("Enter the project ID: ");
            int projectID = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter the project name: ");
            String projectName = scanner.nextLine();

            System.out.print("Enter project description: ");
            String description = scanner.nextLine();

            System.out.print("Enter project budget: ");
            long budget = scanner.nextLong();
            scanner.nextLine();

            System.out.print("Enter project deadline (yyyy-MM-dd): ");
            String deadlineString = scanner.nextLine();
            Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(deadlineString);

            Client client = (Client) loggedInAccount;
            Projects project = controller.createProject(projectID, projectName, client, null, description, budget, deadline);

            System.out.println("Project created successfully: " + project.getProjectName());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        } catch (Exception e) {
            System.out.println("Error while creating project: " + e.getMessage());
        }
    }

    private void viewProjects() {
        System.out.println("Viewing projects for " + loggedInAccount.getUsername() + "...");
        controller.viewProjects(loggedInAccount);
    }

    private void viewAvailableProjects() {
        if (!(loggedInAccount instanceof Freelancer)) {
            System.out.println("Only freelancers can view available projects.");
            return;
        }

        System.out.println("Fetching available projects...");
        controller.viewAvailableProjects();
    }

    private void markProjectAsCompleted(Scanner scanner) {
        if (!(loggedInAccount instanceof Freelancer freelancer)) {
            System.out.println("Only freelancers can mark projects as completed.");
            return;
        }

        System.out.print("Enter the project ID to mark as completed: ");
        int projectID = scanner.nextInt();
        scanner.nextLine();

        Projects project = controller.findProjectById(projectID);
        if (project != null) {
            controller.markProjectAsFinished(project);
            System.out.println("Project marked as completed.");
        } else {
            System.out.println("Project not found.");
        }
    }

    private void leaveReview(Scanner scanner) {
        if (!(loggedInAccount instanceof Freelancer freelancer)) {
            System.out.println("Only freelancers can leave reviews.");
            return;
        }

        System.out.print("Enter the project ID for which you want to leave a review: ");
        int projectID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Projects project = controller.findProjectById(projectID);
        if (project == null || project.getFreelancer() == null || !project.getFreelancer().equals(freelancer)) {
            System.out.println("Invalid project ID or you are not assigned to this project.");
            return;
        }

        System.out.print("Enter your review comment (max 500 characters): ");
        String comment = scanner.nextLine();
        if (comment.length() > 500) {
            System.out.println("Comment exceeds the maximum allowed length of 500 characters.");
            return;
        }

        System.out.print("Enter a rating (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }

        int reviewID = generateUniqueReviewID(); // Example ID generation method
        Reviews review = new Reviews(reviewID, rating, comment, loggedInAccount.getAccountID());
        controller.addReviewToProject(project, review);

        System.out.println("Review submitted successfully for project: " + project.getProjectName());
    }

    private void leaveReviewForFreelancer(Scanner scanner) {
        if (!(loggedInAccount instanceof Client client)) {
            System.out.println("Only clients can leave reviews for freelancers.");
            return;
        }

        System.out.print("Enter the project ID for which you want to leave a review: ");
        int projectID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Projects project = controller.findProjectById(projectID);
        if (project == null || project.getClient() == null || !project.getClient().equals(client) || project.getFreelancer() == null) {
            System.out.println("Invalid project ID, you are not the owner of this project, or no freelancer is assigned.");
            return;
        }

        System.out.print("Enter your review for the freelancer (max 500 characters): ");
        String comment = scanner.nextLine();
        if (comment.length() > 500) {
            System.out.println("Comment exceeds the maximum allowed length of 500 characters.");
            return;
        }

        System.out.print("Enter a rating for the freelancer (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }

        int reviewID = generateUniqueReviewID(); // Example ID generation method
        Reviews review = new Reviews(reviewID, rating, comment, loggedInAccount.getAccountID());
        controller.addReviewToFreelancer(project.getFreelancer(), review);

        System.out.println("Review submitted successfully for freelancer: " + project.getFreelancer().getUsername());
    }

    // Example helper method for generating unique review IDs
    private int generateUniqueReviewID() {
        return (int) (Math.random() * 100000); // Replace with your logic if needed
    }


    // Email validation method
    private boolean isEmailValid(String email) {
        return email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private void takeOnProject(Scanner scanner) {
        if (!(loggedInAccount instanceof Freelancer freelancer)) {
            System.out.println("Only freelancers can take on projects.");
            return;
        }

        System.out.println("Available Projects:");
        controller.viewAvailableProjects();

        System.out.print("Enter the project ID you want to take on: ");
        int projectID = scanner.nextInt();
        scanner.nextLine();

        Projects project = controller.findProjectById(projectID);
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        if (project.getFreelancer() != null) {
            System.out.println("Project is already assigned to a freelancer.");
            return;
        }

        boolean success = controller.assignProjectToFreelancer(freelancer, project);
        if (success) {
            System.out.println("You have successfully taken on the project: " + project.getProjectName());
        }
    }
}