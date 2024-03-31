package com.example.seepeaker.model;

public class Word {
    private int wordId;
    private String word;

    public Word() {
    }
    public Word(int wordId, String word) {
        this.wordId = wordId;
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
