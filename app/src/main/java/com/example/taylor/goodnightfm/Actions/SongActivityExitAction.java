package com.example.taylor.goodnightfm.Actions;

import com.example.taylor.goodnightfm.DoActions.Action;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;

/**
 * Created by taylor on 2017/10/10.
 */

public class SongActivityExitAction extends Action {
    public static final String SongActivityExitAction = "歌曲页退出";
    @Override
    public String getType() {
        return SongActivityExitAction;
    }

    @Override
    public Action dispatch(Dispatcher dispatcher) {
        return super.dispatch(dispatcher);
    }
}
