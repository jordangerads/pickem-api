package com.gci.pickem.data;

import com.gci.pickem.model.UserView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/*
 * This shouldn't have to implement Serializable!! https://hibernate.atlassian.net/browse/HHH-7668
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "registration_code")
    private String registrationCode;

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private Set<UserPool> userPools;

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private Set<UserRole> userRoles;

    public User() {
    }

    public User(UserView userView) {
        this.firstName = userView.getFirstName();
        this.lastName = userView.getLastName();
        this.email = userView.getUsername();
        this.isActive = userView.getActive();
        this.registrationCode = userView.getRegistrationCode();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public Set<UserPool> getUserPools() {
        return userPools;
    }

    public void setUserPools(Set<UserPool> userPools) {
        this.userPools = userPools;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
