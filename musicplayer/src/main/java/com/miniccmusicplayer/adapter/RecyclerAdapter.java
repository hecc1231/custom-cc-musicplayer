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
public class RecyclerAdapter extends RecyclerView.Adapter{
    private List<Song>mSongList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public RecyclerAdapter(Context context,List<Song> mSongList)
    {
        this.mSongList = mSongList;
        layoutInflater = LayoutInflater.from(context);
    }
    //自定义接口类
    public interface OnItemClickListener
    {
        //待实现的接口方法
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolder)holder).songTitle.setText(mSongList.get(position).getTitle());
        ((ViewHolder) holder).artistTitle.setText(mSongList.get(position).getArtist());
        if(this.mOnItemClickListener!=null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();//若删除了或增加了item可实时更改
                    mOnItemClickListener.onItemClick(v,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(v,pos);
                    return true;
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mSongList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitle;
        public TextView artistTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView)itemView.findViewById(R.id.song_title);
            artistTitle = (TextView)itemView.findViewById(R.id.artist_title);
        }
    }
}
