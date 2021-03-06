package com.yi2580.roundview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;

import java.util.Arrays;

/**
 * Created by zhangqi on 2017/9/2.
 */

public class RoundTextView extends AppCompatTextView {
    private final String TAG = "RoundButton";

    private int[] shapeTypes = new int[]{GradientDrawable.RECTANGLE, GradientDrawable.OVAL, GradientDrawable.LINE, GradientDrawable.RING};

    private boolean isSelected = true;

    //文字
    private String[] mStateText = new String[2];
    private int textColor;
    private int textSelectedColor;

    public RoundTextView(Context context) {
        this(context, null);
    }

    public RoundTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        //设置单行居中
        setSingleLine(true);
        setGravity(Gravity.CENTER);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundTextView);
        //显示类型
        int shapeTpe = a.getInt(R.styleable.RoundTextView_viewShapeTpe, shapeTypes[0]);
        //圆角
        int cornerRadius = a.getLayoutDimension(R.styleable.RoundTextView_viewCornerRadius, 0);
        float topLeftRadius = a.getLayoutDimension(R.styleable.RoundTextView_viewTopLeftRadius, 0);
        float topRightRadius = a.getLayoutDimension(R.styleable.RoundTextView_viewTopRightRadius, 0);
        float bottomLeftRadius = a.getLayoutDimension(R.styleable.RoundTextView_viewBottomLeftRadius, 0);
        float bottomRightRadius = a.getLayoutDimension(R.styleable.RoundTextView_viewBottomRightRadius, 0);
        //按下(pressed)状态颜色的系数
        float pressedRatio = a.getFloat(R.styleable.RoundTextView_viewPressedRatio, 0.80f);
        //选中和非选中的文字
        try {
            CharSequence[] stateText = a.getTextArray(R.styleable.RoundTextView_rStateText);
            if (stateText.length == 2) {
                mStateText[0] = stateText[0].toString();
                mStateText[1] = stateText[1].toString();
            }
            Log.e(TAG, Arrays.toString(mStateText));
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        //边框颜色
        ColorStateList strokeColorStateList = a.getColorStateList(R.styleable.RoundTextView_viewStrokeColor);
        int strokeColor = 0;
        if (strokeColorStateList != null) {
            strokeColor = strokeColorStateList.getDefaultColor();
        }
        int strokeSelectedColor = a.getColor(R.styleable.RoundTextView_viewStrokeSelectedColor, strokeColor);
        //边框厚度
        int strokeWidth = a.getDimensionPixelSize(R.styleable.RoundTextView_viewStrokeWidth, 0);
        //边框虚线长度
        int strokeDashWidth = a.getDimensionPixelSize(R.styleable.RoundTextView_viewStrokeDashWidth, 0);
        //边框虚线间隔
        int strokeDashGap = a.getDimensionPixelSize(R.styleable.RoundTextView_viewStrokeDashGap, 0);
        //填充颜色
        ColorStateList solidColorStateList = a.getColorStateList(R.styleable.RoundTextView_viewSolidColor);
        //solidColor = a.getColor(R.styleable.RoundTextView_viewSolidColor, 0x0);
        int solidColor = 0;
        if (solidColorStateList != null) {
            solidColor = solidColorStateList.getDefaultColor();
        }
        int solidSelectedColor = a.getColor(R.styleable.RoundTextView_viewSolidSelectedColor, solidColor);
        //文字颜色
        textColor = a.getColor(R.styleable.RoundTextView_rTextColor, getTextColors().getDefaultColor());
        textSelectedColor = a.getColor(R.styleable.RoundTextView_rTextSelectedColor, textColor);


        if (solidColorStateList == null) {
            solidColorStateList = ColorStateList.valueOf(0);
        }
        RoundDrawable rd = new RoundDrawable();

        //设置类型
        rd.setShape(shapeTypes[shapeTpe]);
        //类型为矩形才可设置圆角
        if (shapeTypes[shapeTpe] == GradientDrawable.RECTANGLE) {
            if (cornerRadius != 0) {
                rd.setCornerRadius(cornerRadius);
            } else {
                rd.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
            }
        }

        if (solidColorStateList.isStateful()) {
            //已在xml文件配置各种状态颜色
            rd.setSolidColors(solidColorStateList);
        } else {
            rd.setSolidColors(getSolidColorStateList(solidColor, solidSelectedColor, pressedRatio));
        }
        /**
         * 初始化边框ColorStateList
         */
        if (strokeColorStateList == null || !solidColorStateList.isStateful()) {
            strokeColorStateList = getStrokeColorStateList(strokeColor, strokeSelectedColor);
        }
        rd.setMyStroke(strokeWidth, strokeColorStateList, strokeDashWidth, strokeDashGap);

        setBackground(rd);

        setSelected(false);
    }

    /**
     * 设置是否选中
     * 改变相应文字、边框和填充颜色
     *
     * @param selected 是否选中
     */
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (isSelected != selected) {
            isSelected = selected;
            if (!TextUtils.isEmpty(mStateText[0]) && !TextUtils.isEmpty(mStateText[1])) {
                if (isSelected) {
                    setText(mStateText[1]);
                } else {
                    setText(mStateText[0]);
                }
            }
            if (isSelected) {
                setTextColor(textSelectedColor);
            } else {
                setTextColor(textColor);
            }
        }
    }

    @Override
    public boolean isSelected() {
        return super.isSelected();
    }

    /**
     * 获取边框ColorStateList
     *
     * @param normal   未选中状态颜色
     * @param selected 选中状态颜色
     * @return
     */
    private ColorStateList getStrokeColorStateList(int normal, int selected) {
        if (selected == 0 && normal != 0) {
            selected = normal;
        }
        int[][] states = new int[][]{{android.R.attr.state_selected}, {}};
        int[] colors = new int[]{selected, normal};
        return new ColorStateList(states, colors);
    }

    /**
     * 获取填充ColorStateList
     *
     * @param normal   未选中状态颜色
     * @param selected 选中状态颜色
     * @param ratio    状态颜色系数
     * @return
     */
    private ColorStateList getSolidColorStateList(int normal, int selected, float ratio) {
        int pressed = normal;
        int selectedPressed = selected;
        if (ratio != 0) {
            pressed = darker(normal, ratio);
            selectedPressed = darker(selected, ratio);
        }
        //状态多的放前面
        int[][] states = new int[][]{{android.R.attr.state_pressed, android.R.attr.state_selected}, {android.R.attr.state_selected}, {android.R.attr.state_pressed}, {}};
        int[] colors = new int[]{selectedPressed, selected, pressed, normal};
        return new ColorStateList(states, colors);
    }

    // 灰度
    int greyer(int color) {
        int blue = (color & 0x000000FF) >> 0;
        int green = (color & 0x0000FF00) >> 8;
        int red = (color & 0x00FF0000) >> 16;
        int grey = Math.round(red * 0.299f + green * 0.587f + blue * 0.114f);
        return Color.argb(0xff, grey, grey, grey);
    }

    // 明度
    int darker(int color, float ratio) {
        color = (color >> 24) == 0 ? 0x22808080 : color;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= ratio;
        return Color.HSVToColor(color >> 24, hsv);
    }


    private static class RoundDrawable extends GradientDrawable {
        //填充色
        private ColorStateList mSolidColors;
        private int mFillColor;
        //边框
        private ColorStateList mStrokeColors;
        private int strokeWidth;
        private float strokeDashWidth;
        private float strokeDashGap;
        private int mBorderColor;

        public RoundDrawable() {
        }

        //设置填充色
        public void setSolidColors(ColorStateList colors) {
            mSolidColors = colors;
            setColor(colors.getDefaultColor());
        }

        /**
         * 设置ColorStateList系统版本需要>21
         *
         * @param width
         * @param colorStateList
         * @param dashWidth
         * @param dashGap
         */
        public void setMyStroke(int width, ColorStateList colorStateList, float dashWidth, float dashGap) {
            mStrokeColors = colorStateList;
            strokeWidth = width;
            strokeDashWidth = dashWidth;
            strokeDashGap = dashGap;

            setStroke(strokeWidth, colorStateList.getDefaultColor(), strokeDashWidth, strokeDashGap);
        }

        @Override
        public void setColor(int argb) {
            mFillColor = argb;
            super.setColor(argb);
        }

        @Override
        public void setStroke(int width, @ColorInt int color, float dashWidth, float dashGap) {
            mBorderColor = color;
            super.setStroke(width, color, dashWidth, dashGap);

        }

        /*@Override
        public void setStroke(int width, ColorStateList colorStateList, float dashWidth, float dashGap) {
            super.setStroke(width, colorStateList, dashWidth, dashGap);
        }*/

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
        }


        @Override
        protected boolean onStateChange(int[] stateSet) {

            final int newBorderColor = mStrokeColors.getColorForState(stateSet, 0);
            if (mBorderColor != newBorderColor) {
                setStroke(strokeWidth, newBorderColor, strokeDashWidth, strokeDashGap);
            }

            if (mSolidColors != null) {
                final int newFillColor = mSolidColors.getColorForState(stateSet, 0);
                if (mFillColor != newFillColor) {
                    setColor(newFillColor);
                    return true;
                }
            }
            //return super.onStateChange(stateSet);
            return false;
        }

        @Override
        public boolean isStateful() {
            return super.isStateful() || (mSolidColors != null && mSolidColors.isStateful());
        }
    }
}
