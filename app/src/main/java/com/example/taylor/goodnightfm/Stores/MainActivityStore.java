package com.example.taylor.goodnightfm.Stores;

import com.example.taylor.goodnightfm.Commands.SongInfoChangeCommand;
import com.example.taylor.goodnightfm.DoActions.Action;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;
import com.example.taylor.goodnightfm.DoActions.Store;
import com.example.taylor.goodnightfm.Models.SongInfo;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.example.taylor.goodnightfm.Actions.SongInfoUpdateAction.SongInfoUpdateAction;

/**
 * Created by taylor on 2017/10/8.
 */

public class MainActivityStore extends Store {

    private String day = "10";

    public static MainActivityStore get(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new MainActivityStore(dispatcher);
        }
        return instance;
    }

    private MainActivityStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    private static MainActivityStore instance;
    public List<SongInfo> SongInfos = new ArrayList<>();
    public int position=0;


    @Override
    @Subscribe
    public void onAction(Action action) {
        switch (action.getType()){
            case SongInfoUpdateAction:
                emitStoreChange(new SongInfoChangeCommand(SongInfos));
                break;
        }
    }

    @Override
    public String getType() {
        return "主界面";
    }

    public String getDay() {
        return day;
    }
}
