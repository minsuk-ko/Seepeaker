package com.example.seepeaker.model;

public class WordResult {
    private String word;
    private boolean isCorrect;

    public WordResult(String word, boolean isCorrect) {
        this.word = word;
        this.isCorrect = isCorrect;
    }

    public String getWord() {
        return word;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
