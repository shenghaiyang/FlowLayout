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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    private class ChildHelper {
        View child;
        int l, t, r, b;

        public ChildHelper(View child, int l, int t, int r, int b) {
            this.child = child;
            this.l = l;
            this.t = t;
            this.r = r;
            this.b = b;
        }
    }

    private List<ChildHelper> helpers = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        helpers.clear();
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
            helpers.add(helper);
        }
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY ? widthSize : width),
                (heightMode == MeasureSpec.EXACTLY ? heightSize : height));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int size = helpers.size();
        for (int i = 0; i < size; i++) {
            ChildHelper helper = helpers.get(i);
            helper.child.layout(helper.l, helper.t, helper.r, helper.b);
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
}