package com.example.taylor.goodnightfm.Commands;

import com.example.taylor.goodnightfm.DoActions.Command;

/**
 * Created by taylor on 2017/10/10.
 */

public class SongActivitySetCoverAndTitles extends Command {
    public String getCover_uri() {
        return cover_uri;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    private final String cover_uri;
    private final String title;
    private final String subtitle;

    public SongActivitySetCoverAndTitles(String cover_uri, String title, String subtitle) {
        this.cover_uri = cover_uri;
        this.title = title;
        this.subtitle = subtitle;
    }
}
