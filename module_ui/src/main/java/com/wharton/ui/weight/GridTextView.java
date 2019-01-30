package com.wharton.ui.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;

import com.wharton.ui.R;

import java.util.ArrayList;

public class GridTextView extends AppCompatTextView {

    /**
     * 最大输入个数
     */
    private int mMaxLength = 6;
    /**
     * 描边圆角的半径
     */
    private float mStrokeRadius = 0;
    /**
     * 圆点的半径
     */
    private float mPointRadius = 0;
    /**
     * 绝对宽度
     */
    private int mAbsWidth;
    /**
     * 绝对高度
     */
    private int mAbsHeight;
    /**
     * paddingStart
     */
    private int paddingStart;
    /**
     * paddingTop
     */
    private int paddingTop;
    /**
     * 输入类型
     */
    private int mTextType = 1;

    private RectF mRectF = new RectF();

    private Paint mStrokePaint;

    private Paint mPointPaint;

    private Paint mTextPaint;

    private ArrayList<TextWatcher> mListeners;

    public GridTextView(Context context) {
        this(context, null);
    }

    public GridTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GridTextView, defStyleAttr, 0);
        //文字的大小
        float mTextSize;
        //文字的颜色
        int mTextColor;
        //圆点的颜色
        int mPointColor;
        //描边的颜色
        int mStrokeColor;
        //描边的宽度
        float mStrokeWidth;
        //最大输入个数
        mMaxLength = a.getInt(R.styleable.GridTextView_maxLength, 6);
        //描边的颜色
        mStrokeColor = a.getColor(R.styleable.GridTextView_strokeColor, Color.GRAY);
        //描边的宽度
        mStrokeWidth = a.getDimension(R.styleable.GridTextView_strokeWidth, dip2px(1F));
        //描边的圆角
        mStrokeRadius = a.getDimension(R.styleable.GridTextView_strokeRadius, 0);
        //圆点的颜色
        mPointColor = a.getColor(R.styleable.GridTextView_pointColor, Color.BLACK);
        //圆点的圆角
        mPointRadius = a.getDimension(R.styleable.GridTextView_pointRadius, 0);
        //文字的颜色
        mTextColor = a.getColor(R.styleable.GridTextView_textColor, Color.BLACK);
        //文字的大小
        mTextSize = a.getDimension(R.styleable.GridTextView_textSize, dip2px(16F));
        //输入类型
        mTextType = a.getInt(R.styleable.GridTextView_textType, 1);
        a.recycle();
        //描边Paint
        mStrokePaint = new Paint();
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
        //圆点Point
        mPointPaint = new Paint();
        mPointPaint.setColor(mPointColor);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setAntiAlias(true);
        //文字Point
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        super.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                sendBeforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendOnTextChanged(s, start, before, count);
                postInvalidate();
                if (s.length() == mMaxLength && actionListener != null) {
                    actionListener.onMaxLength(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                sendAfterTextChanged(s);
            }
        });
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
        setCursorVisible(false);
        setSingleLine(true);
        setFocusable(false);
    }

    private void sendAfterTextChanged(Editable text) {
        if (mListeners != null) {
            final ArrayList<TextWatcher> list = mListeners;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).afterTextChanged(text);
            }
        }
    }

    private void sendOnTextChanged(CharSequence text, int start, int before, int after) {
        if (mListeners != null) {
            final ArrayList<TextWatcher> list = mListeners;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).onTextChanged(text, start, before, after);
            }
        }
    }

    private void sendBeforeTextChanged(CharSequence text, int start, int before, int after) {
        if (mListeners != null) {
            final ArrayList<TextWatcher> list = mListeners;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).beforeTextChanged(text, start, before, after);
            }
        }
    }

    public void addTextChangedListener(TextWatcher watcher) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(watcher);
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        if (mListeners != null) {
            int i = mListeners.indexOf(watcher);
            if (i >= 0) {
                mListeners.remove(i);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量View宽高
        int mWidth = measureHandler(widthMeasureSpec);
        int mHeight = measureHandler(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        //获取Padding数据
        paddingStart = getPaddingStart();
        paddingTop = getPaddingTop();
        mAbsWidth = mWidth - paddingStart - getPaddingEnd();
        mAbsHeight = mHeight - paddingTop - getPaddingBottom();
        mRectF.set(paddingStart, paddingTop, paddingStart + mAbsWidth, paddingTop + mAbsHeight);
    }

    private int measureHandler(int measureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            result = Math.max(size, result);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制描边的边缘
        canvas.drawRoundRect(mRectF, mStrokeRadius, mStrokeRadius, mStrokePaint);
        //绘制描边分割线
        if (mMaxLength > 1) {
            for (int i = 1; i < mMaxLength; i++) {
                //第一个点的X位置(从上到下绘制)
                float mStartX = paddingStart + (mAbsWidth / mMaxLength) * i;
                float mStartY = paddingTop;
                float mStopX = paddingStart + (mAbsWidth / mMaxLength) * i;
                float mStopY = paddingTop + mAbsHeight;
                canvas.drawLine(mStartX, mStartY, mStopX, mStopY, mStrokePaint);
            }
        }
        String mText = getText().toString();
        //绘制点或文字
        int length = mText.length();
        for (int i = 0; i < length && i < mMaxLength; i++) {
            float mCenterX = paddingStart + (mAbsWidth / mMaxLength) * (i + 0.5F);
            float mCenterY = paddingTop + mAbsHeight / 2;
            if (mTextType == 1) {
                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float top = fontMetrics.top;
                float bottom = fontMetrics.bottom;
                //基线中间点的Y轴计算公式
                int mFinalTextY = (int) (mCenterY - top / 2 - bottom / 2);
                String substring = mText.substring(i, i + 1);
                canvas.drawText(substring, mCenterX, mFinalTextY, mTextPaint);
            } else {
                canvas.drawCircle(mCenterX, mCenterY, mPointRadius, mPointPaint);
            }
        }
    }

    private float dip2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    private OnGridActionListener actionListener;

    public interface OnGridActionListener {
        void onMaxLength(String s);
    }

    public void setOnGridActionListener(OnGridActionListener completeListener) {
        this.actionListener = completeListener;
    }


}
