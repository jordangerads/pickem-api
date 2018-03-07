package com.gci.pickem.data;

import com.gci.pickem.model.UserView;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private Set<UserPool> userPools;

    public User() {
    }

    public User(UserView userView) {
        this.firstName = userView.getFirstName();
        this.lastName = userView.getLastName();
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

    public Set<UserPool> getUserPools() {
        return userPools;
    }

    public void setUserPools(Set<UserPool> userPools) {
        this.userPools = userPools;
    }
}
