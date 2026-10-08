package ui;

import dao.LogDao;
import model.User;

public class DashboardMenu {
    private User user;
    private LogDao logDao = new LogDao();

    public DashboardMenu(User user) {
        this.user = user;
    }

    public void show() {
        boolean loggedIn = true;

        while (loggedIn) {
            InputHelper.printHeader(user.role() + " MENU - " + user.fullName());

            int maxChoice;
            if (user.role().equals("ADMIN")) {
                System.out.println("  [1] Manage Accounts");
                System.out.println("  [2] Manage Medicines");
                System.out.println("  [3] View Reports");
                System.out.println("  [4] View Activity Logs");
                maxChoice = 4;
            } else if (user.role().equals("NURSE")) {
                System.out.println("  [1] Record Clinic Visit");
                System.out.println("  [2] Search Student Visit History");
                System.out.println("  [3] Medicine Stock List");
                System.out.println("  [4] View Reports");
                maxChoice = 4;
            } else {
                System.out.println("  [1] View My Profile");
                System.out.println("  [2] View My Visit History");
                System.out.println("  [3] Update My Contact Info");
                maxChoice = 3;
            }
            System.out.println("  [0] Logout");

            int choice = InputHelper.readNumber("\nEnter choice: ", 0, maxChoice);

            if (choice == 0) {
                logDao.addLog(user.id(), "LOGOUT", user.username(), "User logged out");
                System.out.println("\nLogged out successfully.");
                loggedIn = false;
            } else {
                System.out.println("\nThis feature is not available yet.");
                InputHelper.pause();
            }
        }
    }
}
