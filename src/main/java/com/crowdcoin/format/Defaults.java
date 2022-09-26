package com.crowdcoin.format;

public class Defaults {

    // Default font size for all text
    public static int fontSize = 16;
    public static String databaseDriver = "com.mysql.cj.jdbc.Driver";

    public static int maxConnectionAttempts = 3;

    // Messages
    public static String invalidCredentials = "Invalid Username and/or Password, please check your input and try again.";
    public static String noConnection = "No connection to database services, please check your internet connection then try again.";
    public static String abstractLoginError = "The system encountered an issue while trying to connect, please check with your administrator that the database connection information is correct.";
    public static String invalidServer = "Failed to establish connection to server. The server may be down, please contact an administrator";
    public static String goodLogin = "Login passed, please wait...";

}
