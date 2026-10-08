package ui;

import java.util.Scanner;

public class InputHelper {
    private static Scanner scanner = new Scanner(System.in);

    public static String readText(String prompt) {
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

    public static int readNumber(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int number = Integer.parseInt(input);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
                System.out.println("  Numbers only.");
                continue;
            }
            System.out.println("  Please enter a number from " + min + " to " + max + ".");
        }
    }

    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("=================================================");
        System.out.println("  " + title);
        System.out.println("=================================================");
    }
}
