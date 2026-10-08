package ui;

import dao.LogDao;
import dao.UserDao;
import model.User;

import java.sql.SQLException;

public class MainMenu {
    private UserDao userDao = new UserDao();
    private LogDao logDao = new LogDao();

    public void show() {
        boolean running = true;

        while (running) {
            InputHelper.printHeader("SCC CLINIC VISIT RECORDING AND MEDICINE STOCK TRACKING");
            System.out.println("  [1] Register Account");
            System.out.println("  [2] Login");
            System.out.println("  [0] Exit");

            int choice = InputHelper.readNumber("\nEnter choice: ", 0, 2);

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 0:
                    System.out.println("\nGoodbye!");
                    running = false;
                    break;
            }
        }
    }

    private void register() {
        InputHelper.printHeader("REGISTER ACCOUNT");

        try {
            String fullName = InputHelper.readText("Full name: ");

            String username = InputHelper.readText("Username: ");
            while (username.length() < 4 || username.contains(" ") || userDao.usernameExists(username)) {
                if (username.length() < 4 || username.contains(" ")) {
                    System.out.println("  Username must be at least 4 characters with no spaces.");
                } else {
                    System.out.println("  Username is already taken.");
                }
                username = InputHelper.readText("Username: ");
            }

            String password = InputHelper.readText("Password: ");
            while (password.length() < 6) {
                System.out.println("  Password must be at least 6 characters.");
                password = InputHelper.readText("Password: ");
            }

            String confirm = InputHelper.readText("Confirm password: ");
            while (!confirm.equals(password)) {
                System.out.println("  Passwords do not match.");
                confirm = InputHelper.readText("Confirm password: ");
            }

            System.out.println("\nSelect role:");
            System.out.println("  [1] NURSE");
            System.out.println("  [2] STUDENT");
            int roleChoice = InputHelper.readNumber("Enter choice: ", 1, 2);

            String role;
            if (roleChoice == 1) {
                role = "NURSE";
            } else {
                role = "STUDENT";
            }

            userDao.register(fullName, username, password, role);
            User newUser = userDao.findByUsername(username);
            logDao.addLog(newUser.id(), "ADD", username, "Registered new " + role + " account");

            System.out.println("\nAccount registered! Status: PENDING");
            System.out.println("Please wait for the Admin to approve your account.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        InputHelper.pause();
    }

    private void login() {
        InputHelper.printHeader("LOGIN");

        String username = InputHelper.readText("Username: ");
        String password = InputHelper.readText("Password: ");

        try {
            User user = userDao.login(username, password);

            if (user == null) {
                User existing = userDao.findByUsername(username);
                Integer accountId = null;
                if (existing != null) {
                    accountId = existing.id();
                }
                logDao.addLog(accountId, "LOGIN", username, "Failed login");
                System.out.println("\nInvalid username or password.");
                InputHelper.pause();
            } else if (user.status().equals("PENDING")) {
                logDao.addLog(user.id(), "LOGIN", username, "Blocked: account is PENDING");
                System.out.println("\nYour account is still PENDING. Please wait for the Admin to approve it.");
                InputHelper.pause();
            } else if (user.status().equals("INACTIVE")) {
                logDao.addLog(user.id(), "LOGIN", username, "Blocked: account is INACTIVE");
                System.out.println("\nYour account is deactivated. Please contact the Admin.");
                InputHelper.pause();
            } else {
                logDao.addLog(user.id(), "LOGIN", username, "Login successful");
                System.out.println("\nLogin successful! Welcome, " + user.fullName() + "!");
                DashboardMenu dashboard = new DashboardMenu(user);
                dashboard.show();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            InputHelper.pause();
        }
    }
}
