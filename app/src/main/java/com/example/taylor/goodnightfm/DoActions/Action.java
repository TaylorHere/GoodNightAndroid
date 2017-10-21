package com.example.taylor.goodnightfm.DoActions;

/**
 * Created by taylor on 2017/10/8.
 */

public abstract class Action<T> {
    private String type;
    private Object data;

    public void setType(String type) {
        this.type = type;
    }

    public Action() {
    }

    public String getType() {
        return type;
    }

    public Action dispatch(Dispatcher dispatcher){
        if (dispatcher != null){
            postOn(dispatcher);
        }
        return this;
    }

    public void postOn(Dispatcher dispatcher){
        if (dispatcher != null){
            dispatcher.dispatch(this);
        }
    }

    public void done(Done done){
        done.with(this);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
