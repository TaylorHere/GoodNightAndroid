package com.example.taylor.goodnightfm.Stores;

import com.example.taylor.goodnightfm.Commands.SongActivitySetCoverAndTitles;
import com.example.taylor.goodnightfm.DoActions.Action;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;
import com.example.taylor.goodnightfm.DoActions.Store;

import static com.example.taylor.goodnightfm.Actions.StartSongActivityAction.StartSongActivityAction;

/**
 * Created by taylor on 2017/10/10.
 */

public class SongActivityStore extends Store {
    private static SongActivityStore instance;

    protected SongActivityStore(Dispatcher dispatcher) {
        super(dispatcher);
    }
    private String cover_uri;
    private String title;
    private String subtitle;
    @Override
    public String getType() {
        return "歌曲界面";
    }

    @Override
    public void onAction(Action action) {
        switch (action.getType()){
            case StartSongActivityAction:
                MainActivityStore mainActivityStore = MainActivityStore.get(Dispatcher.get());
                this.cover_uri = mainActivityStore.SongInfos
                        .get(mainActivityStore.position).getCovers().getFile_path();
                this.title = mainActivityStore.SongInfos
                        .get(mainActivityStore.position).getTitle();
                this.subtitle = mainActivityStore.SongInfos
                        .get(mainActivityStore.position).getSubtitle();
                PlayStore.get(Dispatcher.get()).keepState = true;
                emitStoreChange(new SongActivitySetCoverAndTitles(cover_uri,title,subtitle));
                break;
        }
    }

    public static SongActivityStore get(Dispatcher dispatcher) {
            if (instance == null) {
                instance = new SongActivityStore(dispatcher);
            }
            return instance;

    }
}
