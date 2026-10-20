package com.scc.clinic;

import com.scc.clinic.dao.DatabaseInitializer;
import com.scc.clinic.dao.LogDao;
import com.scc.clinic.dao.UserDao;
import com.scc.clinic.ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        UserDao userDao = new UserDao();
        LogDao logDao = new LogDao();
        Scanner scanner = new Scanner(System.in);

        Authentication auth = new Authentication(userDao, logDao, scanner);
        auth.start();

        scanner.close();
    }
}
