package com.example.taylor.goodnightfm.DoActions;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by taylor on 2017/10/8.
 */

public abstract class Store {

    private static Store instance;
    private Dispatcher dispatcher;

    protected Store(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        if (dispatcher != null){
            dispatcher.register(this);
        }
    }

    public abstract String getType();

    public void emitStoreChange(Command command) {
        Log.v("action","【"+getType()+"】store emit to view");
        EventBus.getDefault().post(command);
    }

    public abstract void onAction(Action action);

}
