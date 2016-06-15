package com.miniccmusicplayer.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.adapter.RecyclerAdapter;
import com.miniccmusicplayer.bean.MsgManager;
import com.miniccmusicplayer.bean.MyLatelySong;
import com.miniccmusicplayer.bean.MyUser;
import com.miniccmusicplayer.bean.Song;
import com.miniccmusicplayer.bean.TestItemDecoraton;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Hersch on 2016/6/14.
 */
public class CustomPopUpWindow extends PopupWindow {
    private final int POPUPWINDOW = 1;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private Button mClearBtn;
    private View rootView;

    private MusicService musicService;
    private List<Song> mSongList;
    private Handler handler;//用来与Activity传递消息

    public CustomPopUpWindow(Context context, final MusicService musicService, Handler handler) {
        super(context);
        this.musicService = musicService;
        this.handler = handler;
        View contentView = LayoutInflater.from(context).inflate(R.layout.lrc_popupwindow, null);
        rootView = LayoutInflater.from(context).inflate(R.layout.activity_lrc, null);
        setContentView(contentView);//添加布局文件
        initViews();
        setParameters();
    }

    public void initViews() {
        mClearBtn = (Button) getContentView().findViewById(R.id.lrc_popupwindow_clear_btn);
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLatelySong();
                for (int i = 0; i < mSongList.size(); i++) {
                    mRecyclerAdapter.removData();
                }
            }
        });
        getContentView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取Recyclerview的高度
                int height = v.findViewById(R.id.popupwindow_down_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    public void deleteLatelySong() {
        List<MyLatelySong> list;
        MyUser user = BmobUser.getCurrentUser(getContentView().getContext(), MyUser.class);
        BmobQuery<MyLatelySong> query = new BmobQuery<MyLatelySong>();
        query.addWhereEqualTo("user", user.getObjectId());    // 查询当前用户的所有帖子
        query.order("-updatedAt");
        query.include("user");
        query.findObjects(getContentView().getContext(), new FindListener<MyLatelySong>() {
            @Override
            public void onSuccess(List<MyLatelySong> object) {
                List<BmobObject> objects = new ArrayList<BmobObject>();
                for (MyLatelySong myLatelySong : object) {
                    objects.add(myLatelySong);
                }
                new BmobObject().deleteBatch(getContentView().getContext(), objects, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContentView().getContext(), "清空成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(getContentView().getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void setParameters() {
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0);//设置在父布局中的位置
        setFocusable(true);//获取焦点可点击
        setRecyclerview();
    }

    /**
     * 设置RecyclerView的布局
     */
    public void setRecyclerview() {
        mSongList = new ArrayList<>();
        View view = getContentView();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.lrc_popupwindow_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setBackgroundColor(Color.TRANSPARENT);
        mRecyclerView.addItemDecoration(new TestItemDecoraton(view.getContext()));
        //线性布局管理器
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(view.getContext());
        mLinearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置recyclerView适配器
        mRecyclerAdapter = new RecyclerAdapter(view.getContext(), mSongList, POPUPWINDOW);
        //设置item点击监听
        mRecyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                musicService.setSongList(mSongList, MusicService.LATE);//设置为最近听过歌单
                musicService.onItemPlay(position);
                //发送LrcActivity更新歌曲信息
                Message message = new Message();
                message.what = MsgManager.POPUPWINDOW_MSG;
                handler.sendMessage(message);
            }
        });
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    public void addRecyclerViewList(List<Song> list) {
        this.mSongList = list;
        for (Song song : list) {
            mRecyclerAdapter.addData(song);
        }
    }
}