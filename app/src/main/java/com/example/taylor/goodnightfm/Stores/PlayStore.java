package com.example.taylor.goodnightfm.Stores;

import android.util.Log;

import com.example.taylor.goodnightfm.Actions.PlayStateChangeAction;
import com.example.taylor.goodnightfm.Actions.PlayerNoPlayerCommand;
import com.example.taylor.goodnightfm.Actions.SongInfoUpdateAction;
import com.example.taylor.goodnightfm.Actions.SongPositionUpdateAction;
import com.example.taylor.goodnightfm.Commands.PlayerDataSetStateCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPlayCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPrepareCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPrepareErrCommand;
import com.example.taylor.goodnightfm.Commands.PlayerReleaseCommand;
import com.example.taylor.goodnightfm.Commands.PlayingSongChangeCommand;
import com.example.taylor.goodnightfm.Commands.PlayerSuspendCommand;
import com.example.taylor.goodnightfm.DoActions.Action;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;
import com.example.taylor.goodnightfm.DoActions.Do;
import com.example.taylor.goodnightfm.DoActions.Store;

import java.util.ArrayList;
import java.util.List;

import static com.example.taylor.goodnightfm.Actions.PlayStateUpdateAction.PlayStateUpdateAction;
import static com.example.taylor.goodnightfm.Actions.SongActivityExitAction.SongActivityExitAction;
import static com.example.taylor.goodnightfm.Actions.StartSongActivityAction.StartSongActivityAction;


/**
 * Created by taylor on 2017/10/8.
 */

public class PlayStore extends Store {

    private static PlayStore instance;

    private String dataSource;
    public final int noPlayState = 0;
    public final int dataSetState = 1;
    public final int prepareState = 2;
    public final int startedState = 3;
    public final int suspendState = 4;
    public final int stopState = 5;
    public final int releaseState = 6;
    public final int prepareErrState = 7;
    public int playState = noPlayState;

    public List<PlayStore> songPlayStore = new ArrayList<>();
    public int position;
    public boolean keepState = false;


    protected PlayStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    public static PlayStore get(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new PlayStore(dispatcher);
        }
        return instance;
    }

    @Override
    public void onAction(Action action) {
        switch (action.getType()){
            case SongInfoUpdateAction.SongInfoUpdateAction:
//                songPlayStore
//                和 SongInfos 同步大小
                Log.v("action","SongInfoUpdateAction acted on PlayStore");
                songPlayStore.clear();
                for (int i = 0; i < MainActivityStore.get(Dispatcher.get()).SongInfos.size(); i++) {
                    songPlayStore.add(new PlayStore(null));
                }
                break;
            case SongPositionUpdateAction.SongPositionUpdateAction:
                if (this.position != MainActivityStore.get(Dispatcher.get()).position){
                    emitStoreChange(new PlayingSongChangeCommand());
                }
                this.position = MainActivityStore.get(Dispatcher.get()).position;
                break;
            case PlayStateChangeAction.PlayStateChangeAction:
                Log.v("action","PlayStateChangeAction acted on PlayStore");
                if (songPlayStore.isEmpty()){
                    return;
                }
                PlayStore playStore = songPlayStore.get(this.position);
                switch (playStore.playState){
                    case noPlayState:
                        playStore.setDataSource(MainActivityStore.get(Dispatcher.get())
                                .SongInfos.get(position).getSongs().getFile_path());
                        playStore.playState = this.dataSetState;
                        songPlayStore.set(position,playStore);
                        Log.v("action","change to data set state");
                        emitStoreChange(new PlayerDataSetStateCommand());
                        break;
                    case dataSetState:
                        playStore.playState = this.prepareState;
                        songPlayStore.set(position,playStore);
                        Log.v("action","change to prepare state");
                        emitStoreChange(new PlayerPrepareCommand(position,playStore.getDataSource()));
                        break;
                    case prepareState:
//                        the state change will happen on the reallyPlay called.
                        playStore.playState = this.noPlayState;
                        songPlayStore.set(position,playStore);
                        Log.v("action","change to release state");
                        emitStoreChange(new PlayerReleaseCommand(position));
                        break;
                    case prepareErrState:
//                        the state change will happen on the reallyPlay called.
                        Log.v("action","change to prepare error state");
                        emitStoreChange(new PlayerPrepareErrCommand());
                        break;
                    case startedState:
                        playStore.playState = this.suspendState;
                        songPlayStore.set(position,playStore);
                        Log.v("action","change to suspend state");
                        emitStoreChange(new PlayerSuspendCommand(position));
                        break;
                    case suspendState:
                        playStore.playState = this.startedState;
                        songPlayStore.set(position,playStore);
                        Log.v("action","change to play state");
                        emitStoreChange(new PlayerPlayCommand(position));
                        break;
                    case stopState:
                        break;
                    case releaseState:
                        break;

                }
                Log.v("state postion",position+"");
                break;
            case StartSongActivityAction:
                break;
            case SongActivityExitAction:
                break;
            case PlayStateUpdateAction:
                if (songPlayStore.isEmpty()){
                    return;
                }
                PlayStore playstore = songPlayStore.get(this.position);
                switch (playstore.playState){
                    case noPlayState:
                        emitStoreChange(new PlayerNoPlayerCommand(position));
                        break;
                    case dataSetState:
                        emitStoreChange(new PlayerDataSetStateCommand());
                        break;
                    case prepareState:
                        emitStoreChange(new PlayerPrepareCommand(position,
                                playstore.getDataSource()));
                        break;
                    case prepareErrState:
                        emitStoreChange(new PlayerPrepareErrCommand());
                        break;
                    case startedState:
                        emitStoreChange(new PlayerPlayCommand(position));
                        break;
                    case suspendState:
                        emitStoreChange(new PlayerSuspendCommand(position));
                        break;
                    case stopState:
                        break;
                    case releaseState:
                        break;

                }
                Log.v("state playsate",playstore.playState+"");
                Log.v("state postion",position+"");
                break;
        }
    }

    public void reallyPlay(){
        List<PlayStore> songPlayStore = PlayStore.get(Dispatcher.get()).songPlayStore;
        PlayStore playStore = songPlayStore.get(PlayStore.get(Dispatcher.get()).position);
        playStore.playState = playStore.startedState;
        songPlayStore.set(PlayStore.get(Dispatcher.get()).position,playStore);
        Do.with(Dispatcher.get()).action(new PlayStateChangeAction());
    }
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    @Override
    public String getType() {
        return "播放";
    }

    public void reallyErr() {
        List<PlayStore> songPlayStore = PlayStore.get(Dispatcher.get()).songPlayStore;
        PlayStore playStore = songPlayStore.get(PlayStore.get(Dispatcher.get()).position);
        playStore.playState = playStore.prepareErrState;
        songPlayStore.set(PlayStore.get(Dispatcher.get()).position,playStore);
        Do.with(Dispatcher.get()).action(new PlayStateChangeAction());
    }
}
