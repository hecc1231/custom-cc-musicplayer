package com.miniccmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.bean.Song;

import java.util.List;

/**
 * Created by Hersch on 2016/3/24.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {
    private List<Song> mSongList;
    private LayoutInflater layoutInflater;
    private int flag = 0;
    private OnItemClickListener mOnItemClickListener;

    public RecyclerAdapter(Context context, List<Song> mSongList, int flag) {
        this.flag = flag;
        this.mSongList = mSongList;
        layoutInflater = LayoutInflater.from(context);
    }

    //自定义接口类
    public interface OnItemClickListener {
        //待实现的接口方法
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (flag == 0) {
            view = layoutInflater.inflate(R.layout.music_frg_recycleview, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.lrc_popupwindow_recycleview, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolder) holder).songTitle.setText(mSongList.get(position).getTitle());
        ((ViewHolder) holder).artistTitle.setText(mSongList.get(position).getArtist());
        holder.itemView.setTag(position);//记录对应视图在RecyclerView的位置
        //如果设置了回调函数
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(v, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public void addData(Song song) {
        mSongList.add(song);
        notifyItemInserted(mSongList.size() - 1);
    }

    public void removData() {
        mSongList.remove(0);
        notifyItemRemoved(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitle;
        public TextView artistTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            artistTitle = (TextView) itemView.findViewById(R.id.artist_title);
        }
    }
}
