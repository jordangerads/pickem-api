package com.qci.pickem.data;

import javax.persistence.*;

@Entity
@Table(name = "pick")
public class Pick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pick_id", nullable = false)
    private Long pickId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pool_id", nullable = false)
    private Long poolId;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "chosen_team_id")
    private Long chosenTeamId;

    @Column(name = "confidence")
    private Integer confidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "game_id", insertable = false, updatable = false)
    private Game game;

    public Pick() {
    }

    public Long getPickId() {
        return pickId;
    }

    public void setPickId(Long pickId) {
        this.pickId = pickId;
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

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getChosenTeamId() {
        return chosenTeamId;
    }

    public void setChosenTeamId(Long chosenTeamId) {
        this.chosenTeamId = chosenTeamId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }
}
