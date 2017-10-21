package com.example.taylor.goodnightfm.Views;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.taylor.goodnightfm.DoActions.Dispatcher;
import com.example.taylor.goodnightfm.Stores.PlayStore;

import java.io.IOException;

/**
 * Created by taylor on 2017/10/7.
 */

public class GoodNightFMMusicPlayService extends Service {
    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MusicPlayBinder();
    }



    @Override
    public void onCreate() {
        Log.v("service","service create");
        player = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("service","service start command");
        return super.onStartCommand(intent, flags, startId);
    }

    class MusicPlayBinder extends Binder {


        private String uri;

        public void start(){
            player.start();
        }

        public void prepare(String uri){
            if (uri == this.uri){
                return;
            }
            this.uri = uri;
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.v("play","start");
                    PlayStore.get(Dispatcher.get()).reallyPlay();
                }
            });
            try {
                player.setDataSource(uri);
                player.prepareAsync();
            } catch (IOException e) {
                PlayStore.get(Dispatcher.get()).reallyErr();
                e.printStackTrace();
            }

        }

        public void suspend(){
            player.pause();
        }

        public void release(){
            player.release();
        }

    }
}
