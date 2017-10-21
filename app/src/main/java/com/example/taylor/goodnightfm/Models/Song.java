package com.example.taylor.goodnightfm.Models;

/**
 * Created by taylor on 2017/10/8.
 */

public class Song {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    private String file_path;
}
