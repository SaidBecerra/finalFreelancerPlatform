package org.example;

import java.util.ArrayList;

public class Accounts {
    private final String username;
    private final String email;
    private final String password;
    private final String bankInfo;
    private final int id;
    private final ArrayList<Reviews> reviews;

    public Accounts(int id, String username, String email, String password, String bankInfo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.bankInfo = bankInfo;
        reviews = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBankInfo() {
        return bankInfo;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public int getAccountID() {
        return id;
    }
}
