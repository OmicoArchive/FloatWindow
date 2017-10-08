/*
 * Copyright 2017 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.omico.support.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatWindow {

    private View mView;
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private int moveX, moveY;
    private int deviationAmount = 3;

    private View.OnTouchListener mOnTouchListener;
    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public FloatWindow(@NonNull Context context) {
        this.mContext = context;
    }

    public FloatWindow init(@NonNull View view, @NonNull WindowManager.LayoutParams layoutParams) {
        this.mWindowManager = getWindowManager(mContext);
        this.mView = view;
        this.mLayoutParams = layoutParams;
        if (mOnTouchListener != null) {
            mView.setOnTouchListener(mOnTouchListener);
        } else {
            this.setDefaultFloatWindowGestureListener();
        }
        return this;
    }

    public FloatWindow init(@NonNull View view, @NonNull WindowManager.LayoutParams layoutParams, int gravity) {
        this.init(view, layoutParams);
        this.setLayoutParamsGravity(gravity);
        return this;
    }

    public FloatWindow init(@NonNull View view) {
        this.init(view, getLayoutParams());
        return this;
    }

    public FloatWindow setLayoutParams(int width, int height, int type, int flags, int format) {
        this.mLayoutParams = new WindowManager.LayoutParams(width, height, type, flags, format);
        return this;
    }

    public FloatWindow setLayoutParams(int width, int height, int type, int flags, int format, int gravity) {
        this.setLayoutParams(width, height, type, flags, format);
        this.setLayoutParamsGravity(gravity);
        return this;
    }

    public FloatWindow setLayoutParams(@NonNull WindowManager.LayoutParams layoutParams) {
        this.mLayoutParams = layoutParams;
        return this;
    }

    public FloatWindow setLayoutParams(@NonNull WindowManager.LayoutParams layoutParams, int gravity) {
        this.mLayoutParams = layoutParams;
        this.setLayoutParamsGravity(gravity);
        return this;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public FloatWindow setLayoutParamsGravity(int gravity) {
        this.mLayoutParams.gravity = gravity;
        return this;
    }

    public int getLayoutParamsGravity() {
        return mLayoutParams.gravity;
    }

    public FloatWindow updateFloatWindowPosition(int x, int y, int currentX, int currentY) {
        this.mLayoutParams.x = x - currentX;
        this.mLayoutParams.y = y - currentY;
        this.mWindowManager.updateViewLayout(mView, mLayoutParams);
        return this;
    }

    public FloatWindow setFloatWindowOnTouchListener(View.OnTouchListener onTouchListener) {
        this.mOnTouchListener = onTouchListener;
        return this;
    }

    public FloatWindow setOnFloatWindowClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
        return this;
    }

    public FloatWindow setOnFloatWindowLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
        return this;
    }

    public int getGestureDeviationAmount() {
        return deviationAmount;
    }

    public FloatWindow setGestureDeviationAmount(int deviationAmount) {
        this.deviationAmount = deviationAmount;
        return this;
    }

    private WindowManager getWindowManager(@NonNull Context context) {
        if (mWindowManager == null)
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return mWindowManager;
    }

    private void setDefaultFloatWindowGestureListener() {
        this.mView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int startX = (int) event.getRawX();
                int startY = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        moveX = (int) event.getX();
                        moveY = (int) event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        updateFloatWindowPosition(startX, startY, moveX, moveY);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        int endX = (int) event.getX();
                        int endY = (int) event.getY();
                        if (Math.abs(moveX - endX) <= deviationAmount && Math.abs(moveY - endY) <= deviationAmount) {
                            if (mOnClickListener != null)
                                mView.setOnClickListener(mOnClickListener);
                            if (mOnLongClickListener != null)
                                mView.setOnLongClickListener(mOnLongClickListener);
                        }
                        updateFloatWindowPosition(startX, startY, moveX, moveY);
                        break;
                }
                return false;
            }
        });
    }

    private void checkException(@NonNull String method) {
        String tag = getClass().getSimpleName() + "." + method + ": ";
        if (mView == null)
            throw new RuntimeException(tag + "View should not be null.");
        if (mWindowManager == null)
            throw new RuntimeException(tag + "WindowManager should not be null.");
        if (mLayoutParams == null)
            throw new RuntimeException(tag + "WindowManager.LayoutParams should not be null.");
    }

    public void attach() {
        checkException("attach()");
        mWindowManager.addView(mView, mLayoutParams);
    }

    public void detach() {
        checkException("detach()");
        mWindowManager.removeView(mView);
    }

    public void show() {
        checkException("show()");
        if (mView.getVisibility() != View.VISIBLE) mView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        checkException("hide()");
        if (mView.getVisibility() != View.GONE) mView.setVisibility(View.GONE);
    }
}