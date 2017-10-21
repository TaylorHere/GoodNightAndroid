package com.example.taylor.goodnightfm.Commands;

import com.example.taylor.goodnightfm.DoActions.Command;

/**
 * Created by taylor on 2017/10/9.
 */

public class PlayerSuspendCommand extends Command {

    private final int position;

    public PlayerSuspendCommand(int position) {
        super();
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
