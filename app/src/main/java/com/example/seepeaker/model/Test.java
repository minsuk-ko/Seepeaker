package com.example.seepeaker.model;

public class Test {

    private String userId;
    private int testId;
    private int questionNum;
    private int rightNum;
    private int testScore;

    public Test() {
    }
    public Test(String userId, int testId, int questionNum, int rightNum, int testScore) {
        this.userId = userId;
        this.testId = testId;
        this.questionNum = questionNum;
        this.rightNum = rightNum;
        this.testScore = testScore;

    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRightNum() {
        return rightNum;
    }

    public void setRightNum(int rightNum) {
        this.rightNum = rightNum;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public int getTestScore() {
        return testScore;
    }

    public void setTestScore(int testScore) {
        this.testScore = testScore;
    }
}
