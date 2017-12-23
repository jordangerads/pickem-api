package com.qci.pickem.data;

import javax.persistence.*;

@Entity
@Table(name = "user_pool")
public class UserPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pool_id", nullable = false)
    private Long userPoolId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pool_id", nullable = false)
    private Long poolId;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @OneToOne
    @JoinColumn(referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToOne
    @JoinColumn(referencedColumnName = "pool_id", insertable = false, updatable = false)
    private Pool pool;

    public UserPool() {
    }

    public Long getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(Long userPoolId) {
        this.userPoolId = userPoolId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPoolId() {
        return poolId;
    }

    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
