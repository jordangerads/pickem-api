package com.gci.pickem.model;

import com.gci.pickem.data.User;

import java.util.Objects;

public class UserView {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public UserView() {
    }

    public UserView(User user) {
        this.id = user.getUserId();
        this.password = "[PROTECTED]";
        this.username = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
