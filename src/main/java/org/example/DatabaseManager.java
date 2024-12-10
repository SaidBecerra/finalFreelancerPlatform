package org.example;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseManager {

    // Singleton instance
    private static volatile DatabaseManager instance;

    // Private constructor to restrict instantiation
    private DatabaseManager() {
        createAllTables();
    }

    // Public method to get the Singleton instance
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    // SQLite's connection method
    private static Connection connect() {
        String url = "jdbc:sqlite:freelancer_platform.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Create Accounts table
    public void createAccountsTable() {
        String query = """
                CREATE TABLE IF NOT EXISTS Accounts (
                accountID INTEGER PRIMARY KEY,
                username TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                bankInfo TEXT NOT NULL,
                type TEXT NOT NULL
                );
                """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("Accounts table created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Create Projects table
    public void createProjectsTable() {
        String query = """
                CREATE TABLE IF NOT EXISTS Projects (
                projectID INTEGER PRIMARY KEY,
                projectName TEXT NOT NULL,
                clientID INTEGER NOT NULL,
                freelancerID INTEGER,
                description TEXT NOT NULL,
                budget REAL NOT NULL,
                deadline TEXT NOT NULL,
                isFinished BOOLEAN DEFAULT false,
                FOREIGN KEY (clientID) REFERENCES Accounts(accountID) ON DELETE CASCADE,
                FOREIGN KEY (freelancerID) REFERENCES Accounts(accountID) ON DELETE CASCADE
                );
                """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("Projects table created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Create Reviews table
    public void createReviewsTable() {
        String query = """
                CREATE TABLE IF NOT EXISTS Reviews (
                reviewID INTEGER PRIMARY KEY,
                accountID INTEGER NOT NULL,
                rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5),
                comment TEXT,
                FOREIGN KEY (accountID) REFERENCES Accounts(accountID) ON DELETE CASCADE
                );
                """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("Reviews table created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Fetch account by email and password for login
    public Accounts selectAccountByEmailAndPassword(String email, String password) {
        String query = "SELECT * FROM Accounts WHERE email = ? AND password = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int accountID = rs.getInt("accountID");
                String username = rs.getString("username");
                String bankInfo = rs.getString("bankInfo");
                String type = rs.getString("type");

                if ("Freelancer".equals(type)) {
                    return new Freelancer(accountID, username, email, password, null, 0, bankInfo);
                } else if ("Client".equals(type)) {
                    return new Client(accountID, username, email, password, bankInfo);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Insert an account into the database
    public void insertAccount(Accounts account, String type) {
        String query = """
                INSERT INTO Accounts(accountID, username, email, password, bankInfo, type)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, account.getAccountID());
            pstmt.setString(2, account.getUsername());
            pstmt.setString(3, account.getEmail());
            pstmt.setString(4, account.getPassword());
            pstmt.setString(5, account.getBankInfo());
            pstmt.setString(6, type);
            pstmt.executeUpdate();
            System.out.println("Account inserted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Insert a project into the database
    public void insertProject(Projects project) {
        String query = """
            INSERT INTO Projects(projectName, clientID, freelancerID, description, budget, deadline, isFinished)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setInt(2, project.getClient().getAccountID());

            // Handle nullable freelancerID
            if (project.getFreelancer() == null) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, project.getFreelancer().getAccountID());
            }

            pstmt.setString(4, project.getProjectDescription());
            pstmt.setDouble(5, project.getBudget());
            pstmt.setString(6, new SimpleDateFormat("yyyy-MM-dd").format(project.getDeadline()));
            pstmt.setBoolean(7, false); // Set isFinished to false by default
            pstmt.executeUpdate();
            System.out.println("Project inserted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Update an existing project
    public void updateProject(Projects project) {
        String query = """
            UPDATE Projects
            SET projectName = ?, clientID = ?, freelancerID = ?, description = ?, budget = ?, deadline = ?
            WHERE projectID = ?;
            """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setInt(2, project.getClient().getAccountID());

            // Handle nullable freelancerID
            if (project.getFreelancer() == null) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, project.getFreelancer().getAccountID());
            }

            pstmt.setString(4, project.getProjectDescription());
            pstmt.setDouble(5, project.getBudget());
            pstmt.setString(6, new SimpleDateFormat("yyyy-MM-dd").format(project.getDeadline()));
            pstmt.setInt(7, project.getProjectID());
            pstmt.executeUpdate();
            System.out.println("Project updated successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void finishProject(Projects project) {
        String query = """
            UPDATE Projects
            SET isFinished = 'true'
            WHERE projectID = ?;
            """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, project.getProjectID());
            pstmt.executeUpdate();
            System.out.println("Project finished successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Fetch all projects for a specific account
// Example in getAllProjectsForAccount method
    public ArrayList<Projects> getAllProjectsForAccount(int accountID) {
        ArrayList<Projects> projectsList = new ArrayList<>();
        String query = "SELECT * FROM Projects WHERE clientID = ? OR freelancerID = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountID);
            pstmt.setInt(2, accountID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int projectID = rs.getInt("projectID");
                String projectName = rs.getString("projectName");
                int clientID = rs.getInt("clientID");
                Integer freelancerID = rs.getObject("freelancerID", Integer.class);
                String description = rs.getString("description");
                long budget = rs.getLong("budget");
                Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("deadline"));

                Client client = (Client) selectAccount(clientID);
                Freelancer freelancer = freelancerID != null ? (Freelancer) selectAccount(freelancerID) : null;

                if (client != null) {
                    Projects project = new Projects(projectID, projectName, client, freelancer, description, budget, deadline);
                    projectsList.add(project);
                }
            }
        } catch (SQLException | java.text.ParseException e) {
            System.out.println(e.getMessage());
        }
        return projectsList;
    }

    // Fetch an account by ID
    public Accounts selectAccount(int accountID) {
        String query = "SELECT * FROM Accounts WHERE accountID = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String bankInfo = rs.getString("bankInfo");
                String type = rs.getString("type");

                if ("Freelancer".equals(type)) {
                    return new Freelancer(accountID, username, email, password, null, 0, bankInfo);
                } else if ("Client".equals(type)) {
                    return new Client(accountID, username, email, password, bankInfo);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Insert a review into the database
    public void insertReview(Reviews review) {
        String query = """
                INSERT INTO Reviews(accountID, rating, comment)
                VALUES (?, ?, ?);
                """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, review.getAccountID());
            pstmt.setInt(2, review.getRating());
            pstmt.setString(3, review.getComment());
            pstmt.executeUpdate();
            System.out.println("Review inserted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Fetch all reviews for a specific account
    public ArrayList<Reviews> getAllReviews(int accountID) {
        ArrayList<Reviews> reviewsList = new ArrayList<>();
        String query = "SELECT * FROM Reviews WHERE accountID = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int reviewID = rs.getInt("reviewID");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                Reviews review = new Reviews(reviewID, rating, comment, accountID);
                reviewsList.add(review);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reviewsList;
    }

    // Delete an account by ID
    public void deleteAccount(int accountID) {
        String query = "DELETE FROM Accounts WHERE accountID = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountID);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully.");
            } else {
                System.out.println("No account found with ID: " + accountID);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Projects> selectProjectsByFreelancer(Freelancer freelancer) {
        ArrayList<Projects> projectsList = new ArrayList<>();
        String query = "SELECT * FROM Projects WHERE freelancerID = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, freelancer.getAccountID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int projectID = rs.getInt("projectID");
                String projectName = rs.getString("projectName");
                int clientID = rs.getInt("clientID");
                String description = rs.getString("description");
                long budget = rs.getLong("budget");
                Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("deadline"));

                Client client = (Client) selectAccount(clientID);

                Projects project = new Projects(projectID, projectName, client, freelancer, description, budget, deadline);
                projectsList.add(project);
            }
        } catch (SQLException | java.text.ParseException e) {
            System.out.println(e.getMessage());
        }
        return projectsList;
    }

    public ArrayList<Projects> selectProjectsByClient(Client client) {
        ArrayList<Projects> projectsList = new ArrayList<>();
        String query = "SELECT * FROM Projects WHERE clientID = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, client.getAccountID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int projectID = rs.getInt("projectID");
                String projectName = rs.getString("projectName");
                int freelancerID = rs.getInt("freelancerID");
                String description = rs.getString("description");
                long budget = rs.getLong("budget");
                Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("deadline"));

                Freelancer freelancer = (Freelancer) selectAccount(freelancerID);

                Projects project;

                if (freelancer != null) {
                    project = new Projects(projectID, projectName, client, freelancer, description, budget, deadline);
                }
                else{
                    project = new Projects(projectID, projectName, client, null, description, budget, deadline);
                }
                projectsList.add(project);
            }
        } catch (SQLException | java.text.ParseException e) {
            System.out.println(e.getMessage());
        }
        return projectsList;
    }

    public ArrayList<Projects> selectAvailableProjects() {
        ArrayList<Projects> projectsList = new ArrayList<>();
        String query = "SELECT * FROM Projects WHERE isFinished = 'false' OR isFinished = 0";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int projectID = rs.getInt("projectID");
                String projectName = rs.getString("projectName");
                int clientID = rs.getInt("clientID");

                // More robust handling of freelancerID
                int freelancerID = 0;
                Object freelancerObj = rs.getObject("freelancerID");
                if (freelancerObj != null) {
                    if (freelancerObj instanceof Integer) {
                        freelancerID = (Integer) freelancerObj;
                    } else {
                        try {
                            freelancerID = Integer.parseInt(freelancerObj.toString());
                        } catch (NumberFormatException ex) {
                            System.out.println("Could not parse freelancerID: " + freelancerObj);
                            continue; // Skip this project
                        }
                    }
                }

                String description = rs.getString("description");
                long budget = rs.getLong("budget");
                Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("deadline"));

                Client client = (Client) selectAccount(clientID);
                Freelancer freelancer = freelancerID > 0 ? (Freelancer) selectAccount(freelancerID) : null;

                Projects project;
                if (client != null) {
                    project = new Projects(projectID, projectName, client, freelancer, description, budget, deadline);
                    projectsList.add(project);
                } else {
                    System.out.println("Skipping project due to null client: " + projectID);
                }
            }
        } catch (SQLException | java.text.ParseException e) {
            System.out.println("Error in selectAvailableProjects:");
            e.printStackTrace();
        }

        System.out.println("Number of available projects: " + projectsList.size());
        return projectsList;
    }

    public Projects selectProjectById(int projectId) {
        String query = "SELECT * FROM Projects WHERE projectID = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String projectName = rs.getString("projectName");
                int clientID = rs.getInt("clientID");
                int freelancerID = rs.getInt("freelancerID");
                String description = rs.getString("description");
                long budget = rs.getLong("budget");
                Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("deadline"));

                Client client = (Client) selectAccount(clientID);
                Freelancer freelancer = (Freelancer) selectAccount(freelancerID);

                if (freelancer != null) {
                    return new Projects(projectId, projectName, client, freelancer, description, budget, deadline);
                }
                else {
                    return new Projects(projectId, projectName, client, null, description, budget, deadline);
                }
            }
        } catch (SQLException | java.text.ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void assignFreelancerToProject(Projects project, Freelancer freelancer) {
        String query = """
        UPDATE Projects
        SET freelancerID = ?
        WHERE projectID = ?;
        """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, freelancer.getAccountID());
            pstmt.setInt(2, project.getProjectID());
            pstmt.executeUpdate();
            System.out.println("Freelancer assigned to project successfully.");
        } catch (SQLException e) {
            System.out.println("Error assigning freelancer to project: " + e.getMessage());
        }
    }

    // Create all tables
    public void createAllTables() {
        createAccountsTable();
        createProjectsTable();
        createReviewsTable();
    }
}