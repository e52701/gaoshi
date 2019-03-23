package xiaosu.widget.dashboard;


import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class GestureDetector {

    private static final String TAG = "GestureDetector";

//    private final int mTouchSlopSquare;

    private float mLastFocusX;//最后一个触点所在x轴坐标
    private float mLastFocusY;//最后一个触点所在y轴坐标
    private float mDownFocusX;//第一个触点所在x轴坐标
    private float mDownFocusY;//第一个触点所在y轴坐标
    private MotionEvent mCurrentDownEvent;

    private boolean mAlwaysInTapRegion;//触点存在标志

    private OnGestureListener mListener;//手势监听对象

    private PointF mCenterPoint;//手势旋转中心点

//    public GestureDetector(Context context) {
//        final ViewConfiguration configuration = ViewConfiguration.get(context);
//        final int touchSlop = configuration.getScaledTouchSlop();//获取TouchSlop,判断是否为滑动事件
//
//        mTouchSlopSquare = touchSlop * touchSlop;
//    }

    /***
     * 空构造函数
     */
    public GestureDetector(){

    }

    /***
     * 设置手势监听事件
     * @param listener
     */
    public void setListener(OnGestureListener listener) {
        mListener = listener;
    }

    /***
     * 设置手势旋转中心点
     * @param centerPoint
     */
    public void setCenterPoint(PointF centerPoint) {
        mCenterPoint = centerPoint;
        Log.i(TAG, "centerPoint: " + centerPoint.toString());
    }

    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();

        //手指松开标志
        final boolean pointerUp =
                (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? ev.getActionIndex() : -1;//如果有触点，则把事件index赋值给skipIndex，若没有，则为-1

        // 确定触摸位置
        float sumX = 0, sumY = 0;
        final int count = ev.getPointerCount();//获取触点数量
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += ev.getX(i);//获取屏幕x轴相对坐标
            sumY += ev.getY(i);//获取屏幕y轴相对坐标
        }
        final int div = pointerUp ? count - 1 : count;
        final float focusX = sumX / div;//触点运动时的x轴坐标
        final float focusY = sumY / div;//触点运动时的y轴坐标

        boolean handled = false;

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN://原本有触点的基础上增加一个触点时的操作
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                // Cancel long press and taps
                cancelTaps();
                break;

            case MotionEvent.ACTION_POINTER_UP://多个触点中的其中一个触点离开时的操作
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                break;
            case MotionEvent.ACTION_DOWN://第一个触点触发的操作
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                if (mCurrentDownEvent != null) {
                    mCurrentDownEvent.recycle();
                }
                mCurrentDownEvent = MotionEvent.obtain(ev);
                mAlwaysInTapRegion = true;
                handled |= mListener.onDown(ev);
                break;

            case MotionEvent.ACTION_MOVE://所有触点移动时的操作
                final float scrollX = mLastFocusX - focusX;
                final float scrollY = mLastFocusY - focusY;
                if ((Math.abs(scrollX) >= 1) || (Math.abs(scrollY) >= 1)) {
                    float deltaAngle = getDeltaAngle(mLastFocusX, mLastFocusY, focusX, focusY);
                    if (!Float.isNaN(deltaAngle))
                        handled = mListener.onScroll(deltaAngle);
                    mLastFocusX = focusX;
                    mLastFocusY = focusY;
                }
                break;

            case MotionEvent.ACTION_UP://最后一个触点离开时的操作
                break;
            case MotionEvent.ACTION_CANCEL://触点取消时的操作
                cancel();
                break;
        }

        return handled;
    }

    /***
     * 获取转动角度
     * @param x1
     * @param y1
     * @return

    private float getAngle(float x1, float y1) {
        double angle = Math.atan((x1 - mCenterPoint.x) / (mCenterPoint.y - y1));

        if (x1 > mCenterPoint.x && y1 < mCenterPoint.y) {
            //第一象限
            return (float) angle;
        }

        if (y1 > mCenterPoint.y) {
            //第二、四象限
            return (float) (Math.PI + angle);
        }

        //第三象限
        return (float) (2 * Math.PI + angle);
    }
     */

    private float getDeltaAngle(float x1, float y1, float x2, float y2) {

        if (fromFirstQuadrant2FourthQuadrant(x1, y1, x2, y2) ||
                fromFourthQuadrant2FirstQuadrant(x1, y1, x2, y2)) {
            //从圆右上转向圆左上
            double angle1 = Math.atan2(x1 - mCenterPoint.x, mCenterPoint.y - y1);
            double angle2 = Math.atan2(x2 - mCenterPoint.x, mCenterPoint.y - y2);
            double deltaAngle = -angle1 + angle2;
            double degrees = Math.toDegrees(deltaAngle);
            return (float) degrees;
        }

        double deltaAngle = Math.atan2(x1 - mCenterPoint.x, y1 - mCenterPoint.y) -
                Math.atan2(x2 - mCenterPoint.x, y2 - mCenterPoint.y);
        return (float) Math.toDegrees(deltaAngle);
    }

    /**
     * 从第一象限滑动到第四象限
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private boolean fromFirstQuadrant2FourthQuadrant(float x1, float y1, float x2, float y2) {
        return x1 > mCenterPoint.x && y1 < mCenterPoint.y
                && x2 < mCenterPoint.x && y2 < mCenterPoint.y;
    }

    /**
     * 从第四象限滑动到第一象限
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private boolean fromFourthQuadrant2FirstQuadrant(float x1, float y1, float x2, float y2) {
        return x2 > mCenterPoint.x && y2 < mCenterPoint.y
                && x1 < mCenterPoint.x && y1 < mCenterPoint.y;
    }

    /**
     * 获取坐标点所在象限
     *
     * @param x
     * @param y
     * @return
     */
    int getQuadrant(float x, float y) {
        if (x >= 0 && y >= 0)
            return 1;
        if (x >= 0 && y < 0)
            return 2;
        if (x < 0 && y < 0)
            return 3;
        if (x < 0 && y > 0)
            return 4;
        return 1;
    }

    /**
     * 取消触点事件
     */
    private void cancelTaps() {
        mAlwaysInTapRegion = false;
    }

    private void cancel() {
        mAlwaysInTapRegion = false;
    }

    public interface OnGestureListener {

        boolean onDown(MotionEvent e);

        /**
         * @param deltaAngle 角度偏移量，单位是角度
         * @return
         */
        boolean onScroll(float deltaAngle);
    }

}
