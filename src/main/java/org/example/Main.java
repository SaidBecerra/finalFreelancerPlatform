package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FreelancerPlatformSystem platformSystem = new FreelancerPlatformSystem();

        while (true) {
            Accounts loggedInAccount = platformSystem.getLoggedInAccount();  // Store the logged-in account

            if (loggedInAccount == null) {
                // User needs to log in or create an account
                platformSystem.showLoginMenu(scanner);
            } else {
                // Based on account type (Freelancer or Client), show specific menus
                if (loggedInAccount instanceof Freelancer) {
                    platformSystem.freelancerMenu(scanner);  // Show freelancer-specific options
                } else if (loggedInAccount instanceof Client) {
                    platformSystem.clientMenu(scanner);  // Show client-specific options
                }
            }

            // After showing the menu, ask if the user wants to continue or exit
            System.out.print("Do you want to continue? (yes/no): ");
            String choice = scanner.nextLine();

            if ("no".equalsIgnoreCase(choice)) {
                System.out.println("Exiting the system. Goodbye!");
                break;  // Exit the loop if the user chooses "no"
            }
        }
    }
}
