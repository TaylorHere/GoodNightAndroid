package com.example.taylor.goodnightfm.Actions;

import com.example.taylor.goodnightfm.DoActions.Action;

/**
 * Created by taylor on 2017/10/9.
 */

public class PlayStateChangeAction extends Action {
    public static final String PlayStateChangeAction = "播放状态切换";

    public PlayStateChangeAction() {
        setType(PlayStateChangeAction);
    }
}
