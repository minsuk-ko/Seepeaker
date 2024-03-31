package com.example.seepeaker.model;

public class Question {

    private String userId;
    private String word;
    private String wordId;
    private long testId;
    private boolean isCorrect;

    public Question() {

    }

    public Question(String userId, String word, String wordId, long testId, boolean isCorrect) {
        this.userId = userId;
        this.word = word;
        this.wordId = wordId;
        this.testId = testId;
        this.isCorrect = isCorrect;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

}
