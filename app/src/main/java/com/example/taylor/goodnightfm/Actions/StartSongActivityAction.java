package com.example.taylor.goodnightfm.Actions;

import com.example.taylor.goodnightfm.DoActions.Action;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;

/**
 * Created by taylor on 2017/10/10.
 */

public class StartSongActivityAction extends Action {
    public final static String StartSongActivityAction = "进入播放页";

    @Override
    public String getType() {
        return StartSongActivityAction;
    }

    @Override
    public Action dispatch(Dispatcher dispatcher) {

        return super.dispatch(dispatcher);
    }
}
