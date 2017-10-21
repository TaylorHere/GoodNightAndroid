package com.example.taylor.goodnightfm.DoActions;

import android.util.Log;

/**
 * Created by taylor on 2017/10/8.
 */

public class Do {

    private static Do instance;
    private Dispatcher dispatcher;

    Do(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static Do with(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new Do(dispatcher);
        }
        instance.dispatcher = dispatcher;
        return instance;
    }

    public Action action(Action action){
        Log.v("action","【"+action.getType()+"】 action created");
        return action.dispatch(this.dispatcher);
    }

}
