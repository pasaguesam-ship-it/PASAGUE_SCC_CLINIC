package com.scc.clinic.ui;

import com.scc.clinic.dao.LogDao;
import com.scc.clinic.dao.UserDao;
import com.scc.clinic.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class StudentMenu {
    private User user;
    private UserDao userDao;
    private LogDao logDao;
    private InputHelper input;

    public StudentMenu(User user, UserDao userDao, LogDao logDao, Scanner scanner) {
        this.user = user;
        this.userDao = userDao;
        this.logDao = logDao;
        this.input = new InputHelper(scanner);
    }

    public void show() {
        boolean loggedIn = true;

        while (loggedIn) {
            input.printHeader("STUDENT MENU - " + user.fullName());
            System.out.println("  [1] View My Visit History");
            System.out.println("  [2] Update My Contact Info");
            System.out.println("  [3] View My Profile");
            System.out.println("  [0] Logout");

            int choice = input.readNumber("\nEnter choice: ", 0, 3);

            if (choice == 0) {
                logDao.addLog(user.id(), "LOGOUT", user.username(), "User logged out");
                System.out.println("\nLogged out successfully.");
                loggedIn = false;
            } else if (choice == 3) {
                viewProfile();
            } else {
                System.out.println("\nThis feature is not available yet.");
                input.pause();
            }
        }
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
