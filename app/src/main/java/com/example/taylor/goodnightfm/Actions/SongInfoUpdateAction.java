package com.example.taylor.goodnightfm.Actions;

import android.util.Log;

import com.example.taylor.goodnightfm.DoActions.Action;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;
import com.example.taylor.goodnightfm.Models.Cover;
import com.example.taylor.goodnightfm.Models.Song;
import com.example.taylor.goodnightfm.Models.SongInfo;
import com.example.taylor.goodnightfm.Stores.MainActivityStore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by taylor on 2017/10/8.
 */

public class SongInfoUpdateAction extends Action {
    public static final String SongInfoUpdateAction="歌曲信息更新";

    Retrofit retrofit= new Retrofit.Builder()
            .baseUrl("http://121.42.143.93:5050")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private MainActivityStore mainActivityStore;

    public SongInfoUpdateAction() {
        setType(SongInfoUpdateAction);
        mainActivityStore = MainActivityStore.get(Dispatcher.get());
    }

    @Override
    public Action dispatch(Dispatcher dispatcher) {
        SongInfoUpdate(dispatcher,this);
        return this;
    }

    public void SongInfoUpdate(final Dispatcher dispatcher, final Action action) {
        SongInfo.SongInfoList songInfoList = retrofit.create(SongInfo.SongInfoList.class);
        Call<List<SongInfo>> model = songInfoList.repo(mainActivityStore.getDay());
        model.enqueue(new Callback<List<SongInfo>>() {
            @Override
            public void onResponse(Call<List<SongInfo>> call, Response<List<SongInfo>> response) {
                mainActivityStore.SongInfos = response.body();
                postOn(dispatcher);
            }

            @Override
            public void onFailure(Call<List<SongInfo>> call, Throwable t) {
                List<SongInfo> SongInfos = MainActivityStore.get(dispatcher).SongInfos;
                for (int i = 0; i < 20; i++) {
                    SongInfo Songinfo = new SongInfo();
                    Cover cover = new Cover();
                    cover.setFile_path("xxx");
                    Songinfo.setCovers(cover);
                    Song song = new Song();
                    song.setFile_path("xxx");
                    Songinfo.setSongs(song);
                    Songinfo.setTitle("XXXX");
                    Songinfo.setSubtitle("XXXX");
                    SongInfos.add(Songinfo);
                }
                postOn(dispatcher);
                Log.v("network debug",t.getMessage());
            }
        });
    }
}
