package com.gci.pickem.data;

import com.gci.pickem.model.PoolInviteStatus;

import javax.persistence.*;

@Entity
@Table(name = "pool_invites")
public class PoolInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pool_invite_id")
    private long poolInviteId;

    @Column(name = "inviting_user_id")
    private Long invitingUserId;

    @Column(name = "pool_id")
    private Long poolId;

    @Column(name = "invitee_email")
    private String inviteeEmail;

    @Column(name = "invite_status")
    private String inviteStatus = PoolInviteStatus.PENDING.name();

    @ManyToOne
    @JoinColumn(name = "inviting_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User invitingUser;

    @ManyToOne
    @JoinColumn(name = "invitee_email", referencedColumnName = "email", insertable = false, updatable = false)
    private User invitedUser;

    @ManyToOne
    @JoinColumn(name = "pool_id", referencedColumnName = "pool_id", insertable = false, updatable = false)
    private Pool pool;

    public long getPoolInviteId() {
        return poolInviteId;
    }

    public void setPoolInviteId(long poolInviteId) {
        this.poolInviteId = poolInviteId;
    }

    public Long getInvitingUserId() {
        return invitingUserId;
    }

    public void setInvitingUserId(Long invitingUserId) {
        this.invitingUserId = invitingUserId;
    }

    public Long getPoolId() {
        return poolId;
    }

    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public void setInviteeEmail(String inviteeEmail) {
        this.inviteeEmail = inviteeEmail;
    }

    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public User getInvitingUser() {
        return invitingUser;
    }

    public void setInvitingUser(User invitingUser) {
        this.invitingUser = invitingUser;
    }

    public User getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(User invitedUser) {
        this.invitedUser = invitedUser;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}
