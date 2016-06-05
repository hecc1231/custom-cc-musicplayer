package com.hersch.helloui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hersch on 2016/5/28.
 */
public class MyLrcTextView extends TextView {
    private float width;        //歌词视图宽度
    private float height;       //歌词视图高度
    private ArrayList<String>lrcContentList;
    private Paint currentPaint; //当前画笔对象
    private Paint notCurrentPaint;  //非当前画笔对象
    private float textHeight = 75;  //文本高度
    private float textSize = 45;        //文本大小
    private int index = 0;//设置当前的index
    public MyLrcTextView(Context context) {
        super(context);
        initTextStyle();
        lrcContentList = new ArrayList<>();
        lrcContentList.clear();
    }

    public MyLrcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextStyle();
    }

    public MyLrcTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextStyle();
    }
    public void setLrcContentList(ArrayList<String>lrcContentList){
        this.lrcContentList = lrcContentList;
    }
    public void setIndex(int index){
        this.index = index;//当前播放的歌词行
    }
    public void initTextStyle(){
        setFocusable(true);     //设置可对焦
        //高亮部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas == null) {
            return;
        }
        currentPaint.setColor(Color.argb(240, 251, 255, 255));
        notCurrentPaint.setColor(Color.argb(120, 255, 255, 255));

        currentPaint.setTextSize(textSize);
        currentPaint.setTypeface(Typeface.SERIF);

        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);
        if(lrcContentList.size()>0) {
            canvas.drawText(lrcContentList.get(index), width / 2, height / 2, currentPaint);
            float tempY = height / 2;
            //画出本句之前的句子
            for (int i = index - 1; i >= 0; i--) {
                //向上推移
                tempY = tempY - textHeight;
                canvas.drawText(lrcContentList.get(i), width / 2, tempY, notCurrentPaint);
            }
            tempY = height / 2;
            //画出本句之后的句子
            for (int i = index + 1; i < lrcContentList.size(); i++) {
                //往下推移
                tempY = tempY + textHeight;
                canvas.drawText(lrcContentList.get(i), width / 2, tempY, notCurrentPaint);
            }
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.height = h;
        this.width = w;
    }
}
