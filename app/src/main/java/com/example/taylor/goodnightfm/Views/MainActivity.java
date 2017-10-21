package com.example.taylor.goodnightfm.Views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taylor.goodnightfm.Actions.PlayStateChangeAction;
import com.example.taylor.goodnightfm.Actions.PlayStateUpdateAction;
import com.example.taylor.goodnightfm.Actions.PlayerNoPlayerCommand;
import com.example.taylor.goodnightfm.Actions.SongInfoUpdateAction;
import com.example.taylor.goodnightfm.Actions.SongPositionUpdateAction;
import com.example.taylor.goodnightfm.Commands.PlayerDataSetStateCommand;
import com.example.taylor.goodnightfm.Commands.SongInfoChangeCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPlayCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPrepareCommand;
import com.example.taylor.goodnightfm.Commands.PlayerPrepareErrCommand;
import com.example.taylor.goodnightfm.Commands.PlayerReleaseCommand;
import com.example.taylor.goodnightfm.Commands.PlayerSuspendCommand;
import com.example.taylor.goodnightfm.DoActions.Do;
import com.example.taylor.goodnightfm.DoActions.Dispatcher;
import com.example.taylor.goodnightfm.Models.SongInfo;
import com.example.taylor.goodnightfm.R;
import com.example.taylor.goodnightfm.Stores.MainActivityStore;
import com.example.taylor.goodnightfm.Stores.PlayStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class MainActivity extends AppCompatActivity {

    private List<Integer> View_IDs_list;
    private Map<Integer, View> view_map;
    private Dispatcher dispatcher=Dispatcher.get();
    private MainActivityStore mainActivityStore = MainActivityStore.get(dispatcher);
    private mRecyclerViewAdapter adapter;
    private ServiceConnection connection;
    private PlayStore playStore = PlayStore.get(dispatcher);
    private GoodNightFMMusicPlayService.MusicPlayBinder binder;
    private List<SongInfo> ListData = new ArrayList<>();
    private LinearLayoutManager horizontal_linear_layoutManager;
    private LinearSnapHelper linearSnapHelper;

    @Subscribe
    public void onDataSetCommand(PlayerDataSetStateCommand command){
        Log.v("action","data settd command acted");
        Do.with(dispatcher).action(new PlayStateChangeAction());
    }


    @Subscribe
    public void onPrepareCommand(PlayerPrepareCommand command){
        binder.prepare(command.getUri());
        if (adapter.clickedHolder == null){
            return;
        }
        adapter.clickedHolder.play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.loading));
        adapter.animator = ObjectAnimator.ofFloat(adapter.clickedHolder.play, "rotation", 0, 360);
        adapter.animator.setDuration(1000);
        adapter.animator.setRepeatCount(-1);
        adapter.animator.start();
    }

    @Subscribe
    public void onNoPlayCommand(PlayerNoPlayerCommand command){
        if (adapter.animator != null){
            adapter.animator.end();
        }
        if (adapter.clickedHolder == null){
            return;
        }
        adapter.clickedHolder.play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.paly));
    }

    @Subscribe
    public void onPrepareErrCommand(PlayerPrepareErrCommand command){
        if (adapter.animator != null){
            adapter.animator.end();
        }
        if (adapter.clickedHolder == null){
            return;
        }
        adapter.clickedHolder.play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.err));
//        TODO change the play to err icon;
        if (binder ==null){
            return;
        }
        binder.release();
    }

    @Subscribe
    public void onPlayCommand(PlayerPlayCommand command){
        if (adapter.animator != null){
            adapter.animator.end();
        }
        if (adapter.clickedHolder == null){
            return;
        }
        adapter.clickedHolder.play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.paly));
        if (binder ==null){
            return;
        }
        binder.start();
    }


    @Subscribe
    public void onSuspendCommand(PlayerSuspendCommand command){
        if (adapter.animator != null){
            adapter.animator.end();
        }
        if (adapter.clickedHolder == null){
            return;
        }
        adapter.clickedHolder.play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.pause));
        if (binder ==null){
            return;
        }
        binder.suspend();
    }

    @Subscribe
    public void onReleaseCommand(PlayerReleaseCommand command){
        if (adapter.animator != null){
            adapter.animator.end();
        }
        if (adapter.clickedHolder == null){
            return;
        }
        adapter.clickedHolder.play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.mipmap.paly));
        if (binder ==null){
            return;
        }
        binder.release();
    }

    @Subscribe
    public void onSongInfoUpdateAction(SongInfoChangeCommand command){
        ListData = command.getSongInfos();
        ListData.add(0,null);
        Log.v("action",ListData.size()+"");
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this,GoodNightFMMusicPlayService.class));

        View_IDs_list = new ArrayList<>();
        View_IDs_list.add(R.id.Main_layout_recyclerView);
        View_IDs_list.add(R.id.indicator);
        view_map = findView(View_IDs_list);


        RecyclerView recyclerView = (RecyclerView) view_map.get(R.id.Main_layout_recyclerView);

        horizontal_linear_layoutManager =  new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontal_linear_layoutManager);

        linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);
        adapter = new mRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        EventBus.getDefault().register(this);


        connection =  new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (GoodNightFMMusicPlayService.MusicPlayBinder) service;
                Do.with(dispatcher).action(new SongInfoUpdateAction());
            }
        };
        bindService(new Intent(this,GoodNightFMMusicPlayService.class),
                connection, BIND_AUTO_CREATE);
        recyclerView.addOnScrollListener(new OnScrollListener() {

            public float totalDx;
            private View indicator= view_map.get(R.id.indicator);

            final private int ANIMATION_NO = -1;
            final private int ANIMATION_START = 0;
            final private int ANIMATION_MIDDLE = 1;
            final private int ANIMATION_END = 2;
            private int animation_state = ANIMATION_NO;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                start animation will play at newState::SCROLL_STATE_DRAGGING
//                      |--->scroll with the percent of the items
//                middle animation will play at newState::SCROLL_STATE_IDLE
//                      |--->scroll to the right end point
//                end animation will play at newState::SCROLL_STATE_SETTLING
//                      |--->scroll with and be elastic

                switch (newState){
                    case SCROLL_STATE_DRAGGING:
                        animation_state = ANIMATION_START;
                        Log.v("scroll state","animation start");
                        break;
                    case SCROLL_STATE_SETTLING:
                        animation_state = ANIMATION_MIDDLE;
                        Log.v("scroll state","animation middle");
                        break;
                    case SCROLL_STATE_IDLE:
                        animation_state = ANIMATION_END;
                        endAnimation();
                        Log.v("scroll state","animation end");
                        break;
                    default:
                        animation_state = ANIMATION_NO;
                }
            }

            private void endAnimation() {
                totalDx = 0;
//                Log.v("animation start","indicator scroll with and be elastic");

                ObjectAnimator backAnimator = ObjectAnimator.ofFloat(indicator,"translationX",0);
                backAnimator.setDuration(100);

                ObjectAnimator elasticAnimator = ObjectAnimator.ofFloat(indicator,"translationX",-10,10,0);
                elasticAnimator.setRepeatCount(2);
                elasticAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                elasticAnimator.setDuration(80);

                ObjectAnimator gobackAnimator = ObjectAnimator.ofFloat(indicator,"translationX",0);
                gobackAnimator.setDuration(0);

                AnimatorSet set = new AnimatorSet();
                set.playSequentially(backAnimator,elasticAnimator,gobackAnimator);
                set.start();

            }

            private void middleAnimation(RecyclerView recyclerView, int dx, int dy) {
//                int endX=400;
//                int windowWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
//                if (dx>0){
//                    endX = windowWidth/2-indicator.getWidth();
//                }else if(dx<0){
//                    endX = -(windowWidth/2-indicator.getWidth());
//                }
//                ObjectAnimator endAnimator = ObjectAnimator.ofFloat(indicator,"translationX",totalDx*0.6f, endX);
//                endAnimator.setDuration(0);
//                endAnimator.start();

            }

            private void startAnimation(RecyclerView recyclerView, int dx, int dy) {
//                Log.v("animation start","indicator scroll with precent totalDx="+totalDx);
                totalDx += dx;
//                the magic number 0.6f is a scale of the indicator move speed;
                ObjectAnimator precentAnimator = ObjectAnimator.ofFloat(indicator,"translationX",totalDx*0.6f);
                precentAnimator.setDuration(0);
                precentAnimator.start();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                switch (animation_state){
                    case ANIMATION_START:
                        startAnimation(recyclerView, dx, dy);
                        break;
                    case ANIMATION_MIDDLE:
                        middleAnimation(recyclerView, dx, dy);
                        break;
                    case ANIMATION_END:
                        break;
                }
            }
        });
    }

    private Map<Integer,View> findView(List<Integer> view_ids) {
//        use the ids in the view_ids list to find out the view and mainActivityStore as a Map<ID,View>
        Map<Integer,View> views_map = new HashMap<>();
        for (int id:view_ids) {
            View view = findViewById(id);
            views_map.put(id,view);
        }
        return views_map;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        Log.v("onResume","onResume");
        Do.with(dispatcher).action(new PlayStateUpdateAction());
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connection != null){
            unbindService(connection);
        }
    }

    private class mRecyclerViewAdapter extends RecyclerView.Adapter<mViewHolder> {
        Context context;
        mViewHolder clickedHolder;
        ObjectAnimator animator;

        public mRecyclerViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.main_activity_recycler_view_item, parent,false);
            return new mViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final mViewHolder holder, final int position) {
            Log.v("position",position+"");
            if (position==0){
                if (holder.itemView.getVisibility() == View.VISIBLE){
                    holder.itemView.setVisibility(View.INVISIBLE);
                }
                return;
            }
            holder.itemView.setVisibility(View.VISIBLE);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedHolder = holder;
                    mainActivityStore.position = position;
                    Do.with(dispatcher).action(new SongPositionUpdateAction());
                    Do.with(dispatcher).action(new PlayStateChangeAction());
                }
            });
            try {
                Glide.with(context)
                        .load(ListData.get(position).getCovers().getFile_path())
                        .into(holder.cover);
            } catch (NullPointerException e){
                Log.v("Exception","null@Glide load");
            }

            holder.title.setText(ListData.get(position).getTitle());
            holder.subtitle.setText(ListData.get(position).getSubtitle());

            holder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    mainActivityStore.position = position;
                    Do.with(dispatcher).action(new SongPositionUpdateAction());
                    intent.setClass(MainActivity.this,SongActivity.class);
                    if (android.os.Build.VERSION.SDK_INT > 20) {
                        Pair img = Pair.create(holder.cover, "transitionImg");
                        Pair play = Pair.create(holder.play, "transitionPlay");
                        Pair title = Pair.create(holder.title, "transitionTitle");
                        Pair subtitle = Pair.create(holder.play, "transitionSubtitle");
                        startActivity(intent,
                                ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                                img,play,title,subtitle).toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListData.size();
        }
    }

    private class mViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        ImageView play;
        TextView title;
        TextView subtitle;
        public mViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            play = (ImageView) itemView.findViewById(R.id.paly);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}
