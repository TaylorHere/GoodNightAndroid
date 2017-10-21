package com.example.taylor.goodnightfm.Commands;

import com.example.taylor.goodnightfm.DoActions.Command;

/**
 * Created by taylor on 2017/10/9.
 */

public class PlayerPlayCommand extends Command {

    private final int position;
    public PlayerPlayCommand(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
