package com.crowdcoin.security;

public class Credentials {

    // Variable declaration
    private String username;
    private String password;

    // Constructor
    public Credentials(String username, String password) {
        // Set username and password
        this.username = username;
        this.password = password;

    }

    // Getters

    // Get Username
    public String getUsername(){
        return(this.username);
    }

    // Get Password
    public String getPassword(){
        return(this.password);
    }

}
