package com.example.taylor.goodnightfm.Commands;

import com.example.taylor.goodnightfm.DoActions.Command;

/**
 * Created by taylor on 2017/10/10.
 */

public class PlayerPrepareCommand extends Command{
    private final int position;
    private final String uri;
    public PlayerPrepareCommand(int position, String uri) {
        this.position = position;
        this.uri = uri;
    }

    public int getPosition() {
        return position;
    }

    public String getUri() {
        return uri;
    }
}
