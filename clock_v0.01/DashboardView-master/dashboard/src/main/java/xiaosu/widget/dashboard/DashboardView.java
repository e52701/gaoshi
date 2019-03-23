package xiaosu.widget.dashboard;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 仪表盘控件
 */
public class DashboardView extends View implements GestureDetector.OnGestureListener {

    private static final String TAG = "RulerView";

    private Option mOption;//在本类里定义的类   Class

    private int mWidth;//时钟宽度
    private int mHeight;//时钟高度
    private float mRadius;//圆形时钟大小
    private int mCenterX;//x轴坐标
    private int mCenterY;//y轴坐标
    private Paint mCirclePaint;//时钟圆形画图
    private Paint mShortLinePaint;//分针画图
    private Paint mLongLinePaint;//时针画图
    private Paint mTextPaint;//时针数字文本画图
    private float mAngle;//角度
    private GestureDetector mGestureDetector;
    private Paint mPointerPaint;//指针画图
    private Paint mCenterCirclePaint;//时钟中心圆形画图

    private OnValueChangedListener mOnValueChangedListener;//数值改变监听

    private float mUnitDegree;//一个刻度对应的角度大小
    private float mUnitNum;//一个刻度显示的数值的大小

    public DashboardView(Context context) {
        super(context);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parAttr(context, attrs, 0);
        init(context);//初始化
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parAttr(context, attrs, defStyleAttr);
        init(context);//初始化
    }

    private void init(Context context) {

//        mGestureDetector = new GestureDetector(context);
        mGestureDetector = new GestureDetector();
        mGestureDetector.setListener(this);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//建立时钟画图对象
        mCirclePaint.setColor(mOption.circleStrokeColor);//设置圆形颜色
        mCirclePaint.setStyle(Paint.Style.STROKE);//设置画图风格
        mCirclePaint.setStrokeWidth(mOption.circleStrokeWidth);//设置圆形宽度

        mShortLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//建立分针画图对象
        mShortLinePaint.setColor(mOption.shortLineColor);//设置分针颜色
        mShortLinePaint.setStrokeWidth(mOption.shortLineWidth);//设置分针宽度

        mLongLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//建立时针画图对象
        mLongLinePaint.setColor(mOption.longLineColor);//设置时针颜色
        mLongLinePaint.setStrokeWidth(mOption.longLineWidth);//设置时针宽度

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//建立时针文本画图对象
        mTextPaint.setColor(mOption.textColor);//设置时针文本颜色
        mTextPaint.setTextSize(mOption.textSize);//设置时针文本字体大小

        mPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//建立指针画图对象
        mPointerPaint.setColor(mOption.pointerColor);//设置指针颜色
        mPointerPaint.setStrokeWidth(mOption.pointerWidth);//设置指针宽度

        mCenterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//建立时钟中心圆形画图对象

        mUnitDegree = 360f / mOption.spaceNum;//设置一个刻度对应角度
        mUnitNum = (mOption.maxNum - mOption.valueOffset) / mOption.spaceNum;//设置一个刻度显示的数值大小
    }

    private void parAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        mOption = new Option();//建立对象

        float density = getResources().getDisplayMetrics().density;//获取屏幕密度

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.DashboardView, defStyleAttr, 0);//自定义view

        //时钟圆形边的初始宽度，初始颜色
        mOption.circleStrokeWidth = attributes.getDimensionPixelSize(R.styleable.DashboardView_circleStrokeWidth, (int) (density * 1));
        mOption.circleStrokeColor = attributes.getColor(R.styleable.DashboardView_circleStrokeColor, Color.GREEN);

        //分针的初始颜色，初始宽度，初始长度
        mOption.shortLineColor = attributes.getColor(R.styleable.DashboardView_shortLineColor, Color.BLUE);
        mOption.shortLineWidth = attributes.getDimensionPixelSize(R.styleable.DashboardView_shortLineWidth, (int) (density * 1));
        mOption.shortLineLength = attributes.getDimensionPixelSize(R.styleable.DashboardView_shortLineLength, (int) (density * 5));

        //时针的初始颜色，初始宽度，初始长度
        mOption.longLineColor = attributes.getColor(R.styleable.DashboardView_longLineColor, Color.GRAY);
        mOption.longLineWidth = attributes.getDimensionPixelSize(R.styleable.DashboardView_longLineWidth, (int) (density * 2));
        mOption.longLineLength = attributes.getDimensionPixelSize(R.styleable.DashboardView_longLineLength, (int) (density * 10));

        //时钟上的初始时针分针总数，初始最大数值，初始间隔凸显时针
        mOption.spaceNum = attributes.getInt(R.styleable.DashboardView_spaceNum, 120);
        mOption.maxNum = attributes.getFloat(R.styleable.DashboardView_maxNum, 360);
        mOption.longLineIndex = attributes.getInteger(R.styleable.DashboardView_longLineIndex, 4);

        // 时针数字的初始颜色，初始字体大小，初始字符间隔
        mOption.textColor = attributes.getColor(R.styleable.DashboardView_android_textColor, Color.BLUE);
        mOption.textSize = attributes.getDimensionPixelSize(R.styleable.DashboardView_android_textSize, (int) (density * 10));
        mOption.textMargin = attributes.getDimensionPixelSize(R.styleable.DashboardView_textMargin, (int) (density * 4));

        //指针的初始宽度，初始颜色，初始长度
        mOption.pointerWidth = attributes.getDimensionPixelSize(R.styleable.DashboardView_pointerWidth, (int) (density * 4));
        mOption.pointerColor = attributes.getColor(R.styleable.DashboardView_pointerColor, 0xFFFF9933);
        mOption.pointerInset = attributes.getDimensionPixelSize(R.styleable.DashboardView_pointerInset, (int) (density * 4));

        //时钟中心圆形初始设置
        mOption.centerCircleRadius = attributes.getDimensionPixelSize(R.styleable.DashboardView_centerCircleRadius, (int) (density * 4));
        if (attributes.hasValue(R.styleable.DashboardView_centerCircleColors)) {
            String centerCircleColors = attributes.getString(R.styleable.DashboardView_centerCircleColors);
            if (null != centerCircleColors && !centerCircleColors.contains(","))
                throw new RuntimeException("中心圆需两种颜色");
            String[] colors = centerCircleColors.split(",");
            mOption.colors = new int[colors.length];
            try {
                for (int i = 0; i < colors.length; i++) {
                    mOption.colors[i] = Color.parseColor(colors[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mOption.sensitivity = attributes.getFloat(R.styleable.DashboardView_sensitivity, 0.1f);
        mOption.sensitivity = Math.min(mOption.sensitivity, 1f);

        //初始时钟显示数字的类型int型
        mOption.asInteger = attributes.getBoolean(R.styleable.DashboardView_asInteger, true);

        //顶部第一根时针数值（默认数值）设置
        mOption.valueOffset = attributes.getFloat(R.styleable.DashboardView_valueOffset, 0);

        attributes.recycle();
    }

    /***
     * 画图方法,将时针,分针,时钟圆形显示在画布上
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1、外圆
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mCirclePaint);
        //2、指针
        canvas.drawLine(mCenterX, mCenterY, mCenterX, mOption.pointerInset, mPointerPaint);
        //3、中心圆
        canvas.drawCircle(mCenterX, mCenterY, mOption.centerCircleRadius, mCenterCirclePaint);
        canvas.rotate(mAngle, mCenterX, mCenterY);

        for (int i = 0; i < mOption.spaceNum; i++) {
            canvas.save();
            //画线条
            canvas.rotate(mUnitDegree * i, mCenterX, mCenterY);

            boolean isLongLine = i % mOption.longLineIndex == 0;

            float startY = mCenterY - mRadius + mOption.insetWidth + mOption.circleStrokeWidth / 2;
            float stopY = startY + (isLongLine ? mOption.longLineLength : mOption.shortLineLength);

            canvas.drawLine(mCenterX, startY, mCenterX, stopY, isLongLine ? mLongLinePaint : mShortLinePaint);

            //时针需要标刻度
            if (isLongLine) {
                String text;

                if (mOption.asInteger)
                    text = String.valueOf((int) (mUnitNum * i + mOption.valueOffset));
                else
                    text = String.valueOf(mUnitNum * i + mOption.valueOffset);

                Rect bounds = getTextBounds(mTextPaint, text);
                canvas.drawText(text, mCenterX - bounds.width() / 2, stopY + bounds.height() + mOption.textMargin, mTextPaint);
            }
            canvas.restore();
        }
    }

    private Rect getTextBounds(Paint textPaint, String text) {
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;//设置时钟宽度
        mHeight = h;//设置时钟高度
        mRadius = Math.min(mWidth, mHeight) / 2 - mOption.circleStrokeWidth / 2;//设置圆形时钟显示大小，保证圆边完整显示

        mCenterX = mWidth / 2;//设置x轴坐标
        mCenterY = mHeight / 2;//设置y轴坐标

        //设置阴影
        RadialGradient centerCircleGradient = new RadialGradient(mCenterX, mCenterY, mOption.centerCircleRadius,
                mOption.colors, mOption.stops, Shader.TileMode.REPEAT);
        mCenterCirclePaint.setShader(centerCircleGradient);

        mGestureDetector.setCenterPoint(new PointF(mCenterX, mCenterY));//设置手势旋转中心点
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    /***
     * 滑动操作方法
     * 滑动的距离跟时钟转动角度的控制
     */
    @Override
    public boolean onScroll(float deltaAngle) {
        mAngle += deltaAngle * 0.1f;//手动转动的角度
        invalidate();

        //转动的角度
        float angle = (mAngle < 0 ? 0 : 360) - mAngle % 360;

        if (null != mOnValueChangedListener)
            mOnValueChangedListener.valueChange(angle / mUnitDegree * mUnitNum + mOption.valueOffset, this);
        return true;
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        mOnValueChangedListener = onValueChangedListener;
    }

    class Option {
        //长线，时针
        float longLineLength;//时针的长度
        float longLineWidth;//时针的宽度
        int longLineColor;//时针的颜色

        //短线，两根时针之间的分针
        float shortLineLength;//分针的长度
        float shortLineWidth;//分针的宽度
        int shortLineColor;//分针的颜色

        //时钟外圈的圆形
        float circleStrokeWidth;//圆形边的宽度
        int circleStrokeColor;//圆形边的颜色

        float insetWidth;

        //时针数字显示
        int textColor;//数字的颜色
        float textSize;//数字的大小
        float textMargin;//数字与周围的间隔

        int spaceNum;//分针与时针的总数（即有多少根），
        float maxNum;//最大值（最大60分钟）
        int longLineIndex;//间隔多少标识数字（每5分钟标一个数）

        boolean asInteger;//时钟显示的数字类型（true为int型，false为float型）

        //时钟唯一指针
        int pointerColor;//指针的颜色
        float pointerWidth;//指针的宽度
        float pointerInset;//TODO:  指针的长度，目前没搞清楚是怎么回事，400dp是反方向，0是最大长度

        //时钟中心的圆形
        float centerCircleRadius;//时钟中心圆形阴影
        int[] colors = {Color.BLACK, Color.GRAY, Color.TRANSPARENT};//圆形渐变颜色
        float[] stops;

        float sensitivity;

        float valueOffset;//时钟顶部指针数值（默认数值）
    }

    /**
     * 改写GestureDetector的触摸事件
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }

    public interface OnValueChangedListener {
        void valueChange(float value, DashboardView dashboard);
    }

}
