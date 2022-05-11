package com.nine.musicplayer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;

public class GrandMotionLayout extends MotionLayout {

    private Context mContext;
    private Boolean touchStarted = false;
    private Rect viewRect = new Rect();
    private View viewToDetectTouch;
    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            viewToDetectTouch.getHitRect(viewRect);
            return viewRect.contains((int) e1.getX(), (int) e1.getY());
        }
    };
    private GestureDetector gestureDetector = new GestureDetector(mContext, gestureListener);

    public GrandMotionLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setTransitionListener(new TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                touchStarted = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        viewToDetectTouch = ((View) getParent()).findViewById(R.id.viewBackground);
    }

    // if drag on the youtube player view -> block event from motion layout go to youtube player view
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    // if not touch on video -> on touch event return false -> event go to parent of motion layout
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            touchStarted = false;
            return super.onTouchEvent(event);
        }
        if (!touchStarted) {
            viewToDetectTouch.getHitRect(viewRect);
            touchStarted = viewRect.contains((int) event.getX(), (int) event.getY());
        }
        return touchStarted && super.onTouchEvent(event);
    }
}
