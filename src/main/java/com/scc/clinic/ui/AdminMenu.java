package com.scc.clinic.ui;

import com.scc.clinic.dao.LogDao;
import com.scc.clinic.dao.UserDao;
import com.scc.clinic.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private User user;
    private UserDao userDao;
    private LogDao logDao;
    private InputHelper input;

    public AdminMenu(User user, UserDao userDao, LogDao logDao, Scanner scanner) {
        this.user = user;
        this.userDao = userDao;
        this.logDao = logDao;
        this.input = new InputHelper(scanner);
    }

    public void show() {
        boolean loggedIn = true;

        while (loggedIn) {
            input.printHeader("ADMIN MENU - " + user.fullName());
            System.out.println("  [1] View All System Users");
            System.out.println("  [2] View Pending Registrations");
            System.out.println("  [3] Approve User Account");
            System.out.println("  [4] Manage Medicines");
            System.out.println("  [5] View Reports");
            System.out.println("  [6] View Activity Logs");
            System.out.println("  [7] View My Profile");
            System.out.println("  [0] Logout");

            int choice = input.readNumber("\nEnter choice: ", 0, 7);

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    viewPendingUsers();
                    break;
                case 3:
                    approveUser();
                    break;
                case 7:
                    viewProfile();
                    break;
                case 0:
                    logDao.addLog(user.id(), "LOGOUT", user.username(), "User logged out");
                    System.out.println("\nLogged out successfully.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nThis feature is not available yet.");
                    input.pause();
                    break;
            }
        }
    }

    private void viewAllUsers() {
        input.printHeader("ALL SYSTEM USERS");
        try {
            List<User> users = userDao.getAllUsers();
            printUserTable(users);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        input.pause();
    }

    private void viewPendingUsers() {
        input.printHeader("PENDING REGISTRATIONS");
        try {
            List<User> pending = userDao.getPendingUsers();
            if (pending.isEmpty()) {
                System.out.println("  No pending accounts.");
            } else {
                printUserTable(pending);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        input.pause();
    }

    private void approveUser() {
        input.printHeader("APPROVE USER ACCOUNT");
        try {
            List<User> pending = userDao.getPendingUsers();
            if (pending.isEmpty()) {
                System.out.println("  No pending accounts to approve.");
                input.pause();
                return;
            }

            printUserTable(pending);

            int userId = input.readNumber("\nEnter User ID to approve (0 to cancel): ", 0, 99999);
            if (userId == 0) {
                System.out.println("\nApproval cancelled.");
                input.pause();
                return;
            }

            User target = null;
            for (User u : pending) {
                if (u.id() == userId) {
                    target = u;
                }
            }

            if (target == null) {
                System.out.println("\nUser ID " + userId + " is not in the pending list.");
            } else if (userDao.approveUser(userId)) {
                logDao.addLog(user.id(), "UPDATE", user.username(), userId,
                        "Approved " + target.role() + " account: " + target.username());
                System.out.println("\nSuccess! " + target.username() + " (ID " + userId + ") is now APPROVED.");
            } else {
                System.out.println("\nApproval failed. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        input.pause();
    }

    private void printUserTable(List<User> users) {
        String border = "+-------+-----------------+--------------+------------+";
        System.out.println(border);
        System.out.println(String.format("| %-5s | %-15s | %-12s | %-10s |", "ID", "USERNAME", "ROLE", "STATUS"));
        System.out.println(border);
        for (User u : users) {
            System.out.println(String.format("| %-5s | %-15s | %-12s | %-10s |",
                    u.id(), u.username(), u.role(), u.status()));
        }
        System.out.println(border);
        System.out.println("  Total: " + users.size());
    }

    private void viewProfile() {
        try {
            User current = userDao.findByUsername(user.username());
            input.printProfile(current);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        input.pause();
    }
}
