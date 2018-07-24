package com.gci.pickem.model;

import com.gci.pickem.data.User;

import java.util.Objects;

public class UserView {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    public UserView() {
    }

    public UserView(User user) {
        this.id = user.getUserId();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserView userView = (UserView) o;
        return Objects.equals(id, userView.id) &&
                Objects.equals(username, userView.username) &&
                Objects.equals(firstName, userView.firstName) &&
                Objects.equals(lastName, userView.lastName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, firstName, lastName);
    }
}
