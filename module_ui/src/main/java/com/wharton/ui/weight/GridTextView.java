package com.wharton.ui.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wharton.ui.R;

public class GridTextView extends View {

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
    private int mInputType = 1;

    private RectF mRectF = new RectF();

    private Paint mStrokePaint;

    private Paint mPointPaint;

    private Paint mTextPaint;

    private String mText = "";

    public GridTextView(Context context) {
        this(context, null);
    }

    public GridTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GridTextView, defStyleAttr, 0);
        int n = a.getIndexCount();
        //文字的大小
        float mTextSize = 40;
        //文字的颜色
        int mTextColor = Color.BLACK;
        //圆点的颜色
        int mPointColor = Color.BLACK;
        //描边的颜色
        int mStrokeColor = Color.GRAY;
        //描边的宽度
        float mStrokeWidth = 1;
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.GridTextView_maxLength) {//最大输入个数
                mMaxLength = a.getInt(R.styleable.GridTextView_maxLength, 6);

            } else if (attr == R.styleable.GridTextView_strokeColor) {//描边的颜色
                mStrokeColor = a.getColor(R.styleable.GridTextView_strokeColor, Color.GRAY);

            } else if (attr == R.styleable.GridTextView_strokeWidth) {//描边的宽度
                mStrokeWidth = a.getDimension(R.styleable.GridTextView_strokeWidth, 1);

            } else if (attr == R.styleable.GridTextView_strokeRadius) {//描边的圆角
                mStrokeRadius = a.getDimension(R.styleable.GridTextView_strokeRadius, 0);

            } else if (attr == R.styleable.GridTextView_pointColor) {//圆点的颜色
                mPointColor = a.getColor(R.styleable.GridTextView_pointColor, Color.BLACK);

            } else if (attr == R.styleable.GridTextView_pointRadius) {//圆点的圆角
                mPointRadius = a.getDimension(R.styleable.GridTextView_pointRadius, 0);

            } else if (attr == R.styleable.GridTextView_textColor) {//文字的颜色
                mTextColor = a.getColor(R.styleable.GridTextView_textColor, Color.BLACK);

            } else if (attr == R.styleable.GridTextView_textSize) {//文字的大小
                mTextSize = a.getDimension(R.styleable.GridTextView_textSize, 40);

            } else if (attr == R.styleable.GridTextView_text) {//文字
                mText = a.getString(R.styleable.GridTextView_text);

            } else if (attr == R.styleable.GridTextView_inputType) {//输入类型
                mInputType = a.getInt(R.styleable.GridTextView_inputType, 1);

            }
        }
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
        super.onDraw(canvas);
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
        //绘制点或文字
        if (mText != null && mText.length() > 0) {
            int length = mText.length();
            for (int i = 0; i < length && i < mMaxLength; i++) {
                float mCenterX = paddingStart + (mAbsWidth / mMaxLength) * (i + 0.5F);
                float mCenterY = paddingTop + mAbsHeight / 2;
                if (mInputType == 1) {
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

    }


}
