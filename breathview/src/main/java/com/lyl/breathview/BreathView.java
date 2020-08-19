package com.lyl.breathview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class BreathView extends View {
    private final static int START_IN_LEFT_TOP = 0;
    private final static int START_IN_RIGHT_BOTTOM = 1;
    //呈现的竖着的
    private final static int V = 0;
    //呈现的横着
    private final static int H = 1;
    //呈现圆形
    private final static int C = 2;
    private Paint mPaint;
    private float mWidth, mHeight;

    private int mGraphical = V;
    private int mRunModel = START_IN_LEFT_TOP;
    //呼吸时间
    private int mBreathTime = 2000;
    private int mSleepTime = 25;
    PorterDuffXfermode mSrcATopXFerMode;
    float mDrawPosition = 20;
    float mMaxPosition = 20;
    float mStep = 20;
    boolean add = true;
    boolean autoRun = true;
    RectF mRectF;
    int mProgress = 0;
    int mMaxProgress = 100;
    int mBgColor = Color.BLUE;
    int mBreathColor = Color.YELLOW;

    public BreathView(Context context) {
        this(context, null);
    }

    public BreathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BreathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BreathView);
            mProgress = a.getInt(R.styleable.BreathView_progress, mProgress);
            mMaxProgress = a.getInt(R.styleable.BreathView_maxProgress, mMaxProgress);
            mRunModel = a.getInt(R.styleable.BreathView_runMode, START_IN_LEFT_TOP);
            mBreathTime = a.getInt(R.styleable.BreathView_breathTime, mBreathTime);
            mSleepTime = a.getInt(R.styleable.BreathView_sleepTime, mSleepTime);
            autoRun = a.getBoolean(R.styleable.BreathView_autoRun, autoRun);
            mBgColor = a.getColor(R.styleable.BreathView_bgColor, mBgColor);
            mBreathColor = a.getColor(R.styleable.BreathView_breathColor, mBreathColor);
            a.recycle();
        }
        mSrcATopXFerMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
        mPaint = new Paint();
        mPaint.setColor(mBreathColor);
        //设置抗锯齿效果
        mPaint.setAntiAlias(true);
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.save();
        float round = mHeight / 2;
        if (mGraphical != C) {
            round = mGraphical == V ? mWidth / 2 : mHeight / 2;
            mPaint.setColor(mBgColor);
            mRectF.set(0, 0, mWidth, mHeight);
            int i = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mRectF.set(0, 0, mWidth, mHeight);
            canvas.translate(getPaddingLeft(), getPaddingTop());
            mPaint.setColor(mBgColor);
            canvas.drawRoundRect(mRectF, round, round, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mBgColor);
            canvas.drawRoundRect(mRectF, round, round, mPaint);
            if (mGraphical == V) {
                if (mRunModel == START_IN_LEFT_TOP) {
                    mRectF.set(0, mDrawPosition, mWidth, mHeight);
                } else {
                    mRectF.set(0, 0, mWidth, mDrawPosition);
                }
            } else {
                if (mRunModel == START_IN_LEFT_TOP) {
                    mRectF.set(mDrawPosition, 0, mWidth, mHeight);
                } else {
                    mRectF.set(0, 0, mDrawPosition, mHeight);
                }
            }
            mPaint.setXfermode(mSrcATopXFerMode);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mBreathColor);
            canvas.drawRoundRect(mRectF, round, round, mPaint);
            canvas.translate(-getPaddingLeft(), -getPaddingTop());
            canvas.restoreToCount(i);
            mPaint.setXfermode(null);
            mPaint.setShader(null);
        } else {
            canvas.drawCircle(round, round, mDrawPosition, mPaint);
        }
        canvas.restore();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (autoRun)
            stop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (autoRun)
            start();
    }

    Thread mDrawThread;
    boolean isStart = false;

    /**
     * 开始
     */
    void start() {
        isStart = true;
        if (mDrawThread == null)
            mDrawThread = new Thread(mRunnable);
        mDrawThread.start();
    }


    /**
     * 停止
     */
    void stop() {
        isStart = false;
        mDrawThread = null;
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        mProgress = progress;
        if (mProgress < 0) {
            mProgress = 0;
        }
        if (mProgress > mMaxProgress) {
            mProgress = mMaxProgress;
        }
        mDrawPosition = mProgress * mStep;
        postInvalidate();
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (isStart) {
                autoChangeDrawParam();
                postInvalidate();
                try {
                    Thread.sleep(mSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 自动改变绘制参数
     */
    protected void autoChangeDrawParam() {
        if (add) {
            mDrawPosition += mStep;
        } else {
            mDrawPosition -= mStep;
        }
        if (mDrawPosition < 0) {
            mDrawPosition = 0;
            add = true;
        } else if (mDrawPosition > mMaxPosition) {
            mDrawPosition = mMaxPosition;
            add = false;
        }
    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        //MeasureSpec.AT_MOST，最大尺寸，只要不超过父控件允许的最大尺寸即可，MeasureSpec.UNSPECIFIED未指定尺寸
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(20);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension((int) mWidth, (int) mHeight);
        mWidth = mWidth - getPaddingLeft() - getPaddingRight();
        mHeight = mHeight - getPaddingBottom() - getPaddingTop();
        if (mWidth > mHeight) {
            mGraphical = H;
            mMaxPosition = mWidth;
            mStep = (mMaxPosition / mBreathTime * mSleepTime);
        } else if (mWidth < mHeight) {
            mGraphical = V;
            mMaxPosition = mHeight;
        } else {
            mMaxPosition = mHeight / 2;
            mGraphical = C;
        }
        mStep = (mMaxPosition / mBreathTime * mSleepTime);
        mDrawPosition = mProgress * mStep;
    }
}
