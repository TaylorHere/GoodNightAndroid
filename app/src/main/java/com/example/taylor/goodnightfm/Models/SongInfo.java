package com.example.taylor.goodnightfm.Models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by taylor on 2017/10/8.
 */

public class SongInfo {
    private int id;
    private int cover_id;
    private int song_id;
    private String title;
    private String subtitle;
    private Song songs;
    private Cover covers;

    public interface SongInfoInstance {
        @GET("/songinfos/{id}")
        Call<SongInfo> repo(@Path("id") String id);
    }

    public interface SongInfoList {
        @GET("/songinfos/")
        Call<List<SongInfo>> repo(@Query("day") String day);
    }


    public Song getSongs() {
        return songs;
    }

    public void setSongs(Song songs) {
        this.songs = songs;
    }

    public Cover getCovers() {
        return covers;
    }

    public void setCovers(Cover covers) {
        this.covers = covers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCover_id() {
        return cover_id;
    }

    public void setCover_id(int cover_id) {
        this.cover_id = cover_id;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
