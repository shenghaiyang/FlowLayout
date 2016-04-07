/*
 * Copyright (C) 2016 shenghaiyang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shenghaiyang.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.LinkedList;

public class FlowLayout extends FrameLayout {

    private class ChildHelper {
        //child view
        View child;
        //child view position, relative to parent
        int l, t, r, b;

        public ChildHelper(View child, int l, int t, int r, int b) {
            this.child = child;
            this.l = l;
            this.t = t;
            this.r = r;
            this.b = b;
        }
    }

    private LinkedList<ChildHelper> children = new LinkedList<>();

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //max width/height
        int width = 0;
        int height = 0;
        //line max width/height
        int lineWidth = 0;
        int lineHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            ChildHelper helper;
            if (lineWidth + childWidth > widthSize) {
                height += lineHeight;
                helper = new ChildHelper(child,
                        params.leftMargin,
                        params.topMargin + height,
                        params.leftMargin + child.getMeasuredWidth(),
                        params.topMargin + height + child.getMeasuredHeight());
                lineWidth = childWidth;
                lineHeight = childHeight;
                width = Math.max(lineWidth, childWidth);
            } else {
                helper = new ChildHelper(child,
                        lineWidth + params.leftMargin,
                        height + params.topMargin,
                        lineWidth + params.leftMargin + child.getMeasuredWidth(),
                        height + params.topMargin + child.getMeasuredHeight());
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            children.addLast(helper);
        }
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY ? widthSize : width),
                (heightMode == MeasureSpec.EXACTLY ? heightSize : height));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int size = children.size();
        for (int i = 0; i < size; i++) {
            ChildHelper helper = children.removeFirst();
            helper.child.layout(helper.l, helper.t, helper.r, helper.b);
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }
}