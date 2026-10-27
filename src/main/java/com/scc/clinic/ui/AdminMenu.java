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
    private Scanner scanner;
    private InputHelper input;

    public AdminMenu(User user, UserDao userDao, LogDao logDao, Scanner scanner) {
        this.user = user;
        this.userDao = userDao;
        this.logDao = logDao;
        this.scanner = scanner;
        this.input = new InputHelper(scanner);
    }

    public void show() {
        boolean loggedIn = true;

        while (loggedIn) {
            input.printHeader("ADMIN MENU - " + user.fullName());
            System.out.println("  [1] View All System Users");
            System.out.println("  [2] View Pending Registrations");
            System.out.println("  [3] Approve User Account");
            System.out.println("  [4] Update User Role and Status");
            System.out.println("  [5] Manage Medicines");
            System.out.println("  [6] View Reports");
            System.out.println("  [7] View Activity Logs");
            System.out.println("  [8] View My Profile");
            System.out.println("  [0] Logout");

            int choice = input.readNumber("\nEnter choice: ", 0, 8);

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
                case 4:
                    updateUser();
                    break;
                case 8:
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

            System.out.println();
            int userId = readIntInput("Enter User ID to approve (0 to cancel): ");
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

    private void updateUser() {
        input.printHeader("UPDATE USER ROLE AND STATUS");
        try {
            List<User> users = userDao.getAllUsers();
            printUserTable(users);

            System.out.println();
            int userId = readIntInput("Enter User ID to update (0 to cancel): ");
            if (userId == 0) {
                System.out.println("\nUpdate cancelled.");
                input.pause();
                return;
            }

            if (userId == user.id()) {
                System.out.println("\nWARNING: Modifying the currently active admin account is restricted.");
                input.pause();
                return;
            }

            User target = null;
            for (User u : users) {
                if (u.id() == userId) {
                    target = u;
                }
            }

            if (target == null) {
                System.out.println("\nUser ID " + userId + " does not exist.");
                input.pause();
                return;
            }

            System.out.println("\nSelected: " + target.username() + " (" + target.role() + ", " + target.status() + ")");

            String newRole = readValidRole();
            if (newRole == null) {
                System.out.println("\nUpdate cancelled.");
                input.pause();
                return;
            }

            String newStatus = readValidStatus();
            if (newStatus == null) {
                System.out.println("\nUpdate cancelled.");
                input.pause();
                return;
            }

            if (userDao.updateUserRoleAndStatus(userId, newRole, newStatus)) {
                logDao.addLog(user.id(), "UPDATE", user.username(), userId,
                        "Updated " + target.username() + ": " + target.role() + "/" + target.status()
                        + " to " + newRole + "/" + newStatus);
                System.out.println("\nSuccess! " + target.username() + " (ID " + userId + ") is now "
                        + newRole + " and " + newStatus + ".");
            } else {
                System.out.println("\nUpdate failed. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        input.pause();
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter numbers only.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (ADMIN, NURSE, STUDENT) or 0 to cancel: ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("0")) {
                return null;
            }
            if (role.equals("ADMIN") || role.equals("NURSE") || role.equals("STUDENT")) {
                return role;
            }
            System.out.println("  Invalid role. Please enter ADMIN, NURSE, or STUDENT.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING) or 0 to cancel: ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("0")) {
                return null;
            }
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("  Invalid status. Please enter APPROVED or PENDING.");
        }
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
