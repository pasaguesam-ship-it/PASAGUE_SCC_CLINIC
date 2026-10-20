package com.scc.clinic.ui;

import com.scc.clinic.model.User;

import java.util.Scanner;

public class InputHelper {
    private Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readText(String prompt) {
        String input = "";
        while (input.isEmpty()) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("  This field is required.");
            }
        }
        return input;
    }

    public int readNumber(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int number = Integer.parseInt(input);
                if (number >= min && number <= max) {
                    return number;
                }
                System.out.println("  Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Numbers only.");
            }
        }
    }

    public void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printHeader(String title) {
        System.out.println();
        System.out.println("=================================================");
        System.out.println("  " + title);
        System.out.println("=================================================");
    }

    public void printProfile(User user) {
        printHeader("MY PROFILE");
        System.out.println("  Account ID : " + user.id());
        System.out.println("  Full name  : " + user.fullName());
        System.out.println("  Username   : " + user.username());
        System.out.println("  Role       : " + user.role());
        System.out.println("  Status     : " + user.status());
        System.out.println("  Created at : " + user.createdAt());
    }
}
