package com.example.carl.womenofinfluence;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {

    private int measuredWidth = 0;
    private int measuredHeight = 0;

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomVideoView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setNewDimension(int width, int height) {
        this.measuredHeight = height;
        this.measuredWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }
}