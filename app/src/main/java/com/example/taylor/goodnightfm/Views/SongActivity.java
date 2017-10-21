package com.example.taylor.goodnightfm.Views;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taylor.goodnightfm.Actions.PlayStateChangeAction;
import com.example.taylor.goodnightfm.Actions.PlayStateUpdateAction;
import com.example.taylor.goodnightfm.Actions.PlayerNoPlayerCommand;
import com.example.taylor.goodnightfm.Actions.SongActivityExitAction;
import com.example.taylor.goodnightfm.Actions.StartSongActivityAction;
import com.example.taylor.goodnightfm.Commands.PlayerDataSetStateCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPlayCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPrepareCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPrepareErrCommand;
import com.example.taylor.goodnightfm.Commands.PlayerReleaseCommand;
import com.example.taylor.goodnightfm.Commands.PlayerSuspendCommand;
import com.example.taylor.goodnightfm.Commands.SongActivitySetCoverAndTitles;
import com.example.taylor.goodnightfm.DoActions.Do;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;
import com.example.taylor.goodnightfm.R;
import com.example.taylor.goodnightfm.Stores.MainActivityStore;
import com.example.taylor.goodnightfm.Stores.PlayStore;
import com.example.taylor.goodnightfm.Stores.SongActivityStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by taylor on 2017/10/7.
 */

public class SongActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean play_state = false;
    private List<Integer> View_IDs_list;
    private static Map<Integer, View> view_map;
    private GoodNightFMMusicPlayService.MusicPlayBinder binder;
    private ServiceConnection connection;
    private Dispatcher dispatcher = Dispatcher.get();
    private MainActivityStore mainActivityStore = MainActivityStore.get(dispatcher);
    private PlayStore playStore = PlayStore.get(dispatcher);
    private SongActivityStore songActivityStore = SongActivityStore.get(dispatcher);
    private ImageView play;
    private ObjectAnimator animator;


    @Subscribe
    public void onNoPlayCommand(PlayerNoPlayerCommand command){
        if (animator != null){
            animator.end();
        }
        play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.paly));
    }

    @Subscribe
    public void onDataSetCommand(PlayerDataSetStateCommand command){
        Do.with(dispatcher).action(new PlayStateChangeAction());
    }


    @Subscribe
    public void onPrepareCommand(PlayerPrepareCommand command){
        play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.loading));
        animator = ObjectAnimator.ofFloat(play, "rotation", 0, 360);
        animator.setDuration(1000);
        animator.setRepeatCount(-1);
        animator.start();
        binder.prepare(command.getUri());
    }

    @Subscribe
    public void onPrepareErrCommand(PlayerPrepareErrCommand command){
        if (animator != null){
            animator.end();
        }
        play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.err));
//        TODO change the play to err icon;
        if (binder ==null){
            return;
        }
        binder.release();
    }

    @Subscribe
    public void onPlayCommand(PlayerPlayCommand command){
        if (animator != null){
            animator.end();
        }
        play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.paly));
        if (binder ==null){
            return;
        }
        binder.start();
    }


    @Subscribe
    public void onSuspendCommand(PlayerSuspendCommand command){
        if (animator != null){
            animator.end();
        }

        play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.pause));
        if (binder ==null){
            return;
        }
        binder.suspend();
    }

    @Subscribe
    public void onReleaseCommand(PlayerReleaseCommand command){
        if (animator != null){
            animator.end();
        }
        play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.paly));
        if (binder ==null){
            return;
        }
        binder.release();
    }

    @Subscribe
    public void onSongActivitySetCoverAndTitles(SongActivitySetCoverAndTitles command){
        Glide.with(this)
                .load(command.getCover_uri())
                .into((ImageView)view_map.get(R.id.big_cover));
        ((TextView)view_map.get(R.id.title)).setText(command.getTitle());
        ((TextView)view_map.get(R.id.subtitle)).setText(command.getSubtitle());

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        EventBus.getDefault().register(this);


        View_IDs_list = new ArrayList<>();
        View_IDs_list.add(R.id.big_cover);
        View_IDs_list.add(R.id.control_button);
        View_IDs_list.add(R.id.title);
        View_IDs_list.add(R.id.subtitle);
        view_map = findView(View_IDs_list);

        play = (ImageView)view_map.get(R.id.control_button);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            ViewCompat.setTransitionName(view_map.get(R.id.big_cover), "transitionImg");
            ViewCompat.setTransitionName(view_map.get(R.id.control_button), "transitionPlay");
            ViewCompat.setTransitionName(view_map.get(R.id.title), "transitionTitle");
            ViewCompat.setTransitionName(view_map.get(R.id.subtitle), "transitionSubtitle");
        }
        play.setOnClickListener(this);

        if (connection == null){
            connection =  new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    binder = (GoodNightFMMusicPlayService.MusicPlayBinder) service;
                    Do.with(dispatcher).action(new StartSongActivityAction());
                    Do.with(dispatcher).action(new PlayStateUpdateAction());
                }
            };
            bindService(new Intent(this,GoodNightFMMusicPlayService.class),
                    connection, BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (connection != null){
            unbindService(connection);
        }
        Do.with(dispatcher).action(new SongActivityExitAction());
        EventBus.getDefault().unregister(this);
    }

    private Map<Integer,View> findView(List<Integer> view_ids) {
//        use the ids in the view_ids list to find out the view and
//        mainActivityStore as a Map<ID,View>
        Map<Integer,View> views_map = new HashMap<>();
        for (int id:view_ids) {
            View view = findViewById(id);
            views_map.put(id,view);
        }
        return views_map;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.control_button:
                Do.with(dispatcher).action(new PlayStateChangeAction());
                break;
        }
    }
}
