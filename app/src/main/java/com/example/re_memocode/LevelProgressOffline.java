package com.example.re_memocode;

public class LevelProgressOffline {

    String playerId;
    String playerName;
    double score;
    double stars;
    double completedTime;

    public LevelProgressOffline(String playerId, String playerName, double score, double stars,
                                double completedTime) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = score;
        this.stars = stars;
        this.completedTime = completedTime;
    }

    // Getters and Setters
    public double getCompletedTime() {
        return completedTime;
    }

    public double getScore() {
        return score;
    }

    public String getPlayerName() {
        return playerName;
    }

}
