package com.scc.clinic.ui;

import com.scc.clinic.dao.LogDao;
import com.scc.clinic.dao.UserDao;
import com.scc.clinic.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class Authentication {
    private UserDao userDao;
    private LogDao logDao;
    private Scanner scanner;
    private InputHelper input;

    public Authentication(UserDao userDao, LogDao logDao, Scanner scanner) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.scanner = scanner;
        this.input = new InputHelper(scanner);
    }

    public void start() {
        boolean running = true;

        while (running) {
            input.printHeader("SCC CLINIC VISIT RECORDING AND MEDICINE STOCK TRACKING");
            System.out.println("  [1] Register Account");
            System.out.println("  [2] Login");
            System.out.println("  [0] Exit");

            int choice = input.readNumber("\nEnter choice: ", 0, 2);

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
        input.printHeader("REGISTER ACCOUNT");

        try {
            String fullName = input.readText("Full name: ");

            String username = input.readText("Username: ");
            while (username.length() < 4 || username.length() > 15 || username.contains(" ")
                    || userDao.usernameExists(username)) {
                if (username.length() < 4 || username.length() > 15 || username.contains(" ")) {
                    System.out.println("  Username must be 4 to 15 characters with no spaces.");
                } else {
                    System.out.println("  Username is already taken.");
                }
                username = input.readText("Username: ");
            }

            String password = input.readText("Password: ");
            while (password.length() < 6) {
                System.out.println("  Password must be at least 6 characters.");
                password = input.readText("Password: ");
            }

            String confirm = input.readText("Confirm password: ");
            while (!confirm.equals(password)) {
                System.out.println("  Passwords do not match.");
                confirm = input.readText("Confirm password: ");
            }

            System.out.println("\nSelect role:");
            System.out.println("  [1] ADMIN");
            System.out.println("  [2] NURSE");
            System.out.println("  [3] STUDENT");
            int roleChoice = input.readNumber("Enter choice: ", 1, 3);

            String role;
            if (roleChoice == 1) {
                role = "ADMIN";
            } else if (roleChoice == 2) {
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

        input.pause();
    }

    private void login() {
        input.printHeader("LOGIN");

        String username = input.readText("Username: ");
        String password = input.readText("Password: ");

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
                input.pause();
            } else if (user.status().equals("PENDING")) {
                logDao.addLog(user.id(), "LOGIN", username, "Blocked: account is PENDING");
                System.out.println("\nYour account is still PENDING. Please wait for the Admin to approve it.");
                input.pause();
            } else if (user.status().equals("INACTIVE")) {
                logDao.addLog(user.id(), "LOGIN", username, "Blocked: account is INACTIVE");
                System.out.println("\nYour account is deactivated. Please contact the Admin.");
                input.pause();
            } else {
                logDao.addLog(user.id(), "LOGIN", username, "Login successful");
                System.out.println("\nLogin successful! Welcome, " + user.fullName() + "!");
                openMenu(user);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            input.pause();
        }
    }

    private void openMenu(User user) {
        if (user.role().equals("ADMIN")) {
            new AdminMenu(user, userDao, logDao, scanner).show();
        } else if (user.role().equals("NURSE")) {
            new NurseMenu(user, userDao, logDao, scanner).show();
        } else {
            new StudentMenu(user, userDao, logDao, scanner).show();
        }
    }
}
