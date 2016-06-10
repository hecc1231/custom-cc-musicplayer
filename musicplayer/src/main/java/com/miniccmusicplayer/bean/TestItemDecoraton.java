package com.miniccmusicplayer.bean;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Hersch on 2016/3/24.
 */
public class TestItemDecoraton extends RecyclerView.ItemDecoration {

    //使用系统内置的分割线
    private static final int[] attrs=new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    public TestItemDecoraton(Context context) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs);
        mDivider=typedArray.getDrawable(0);
    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left=parent.getPaddingLeft();
        int right=parent.getWidth()-parent.getPaddingRight();
        int childCount=parent.getChildCount();

        for(int i=0;i<childCount;i++){
            View child=parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams=(RecyclerView.LayoutParams)child.getLayoutParams();
            int top=child.getBottom()+layoutParams.bottomMargin;
            int bottom=top+mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
            if(i==0)//在第一个子View上划线
            {
                top=child.getTop()-layoutParams.topMargin;
                bottom = top+mDivider.getIntrinsicHeight();//线的粗
                mDivider.setBounds(left,top,right,bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(),0);
    }
}
