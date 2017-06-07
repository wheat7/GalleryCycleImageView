package com.wheat7.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by wheat7 on 18/03/2017.
 */

public class GalleryCycleImageView extends RelativeLayout {
    //轮播切换TAG
    private static final int SWITCH = 1;
    private Context mContext;
    //轮播持续时间
    private int duration;
    private ViewPager mViewPager;
    //小圆点指示器容器
    private LinearLayout mLinearLayout;
    //Image计数
    private int mCount;
    //回调接口
    private GalleyCycleImageListener mGalleyCycleImageListener;
    //px转dp工具类
    private DensityUtil densityUtil;
    //指示器shape
    private GradientDrawable indicatorFocus;
    private GradientDrawable indicatorNormal;
    //指示器颜色
    private int indicatorFocusColor;
    private int indicatorNormalColor;
    //指示器大小
    private int indicatorSize;
    //图片高度
    private int viewPagerHeight;

    /*

    构造
     */
    public GalleryCycleImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GalleryCycleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GalleryCycleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GalleryCycleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /*
    传入count
     */
    public void setCount(int count) {
        mCount = count;
        setIndicatorFocusColor(indicatorFocusColor);
        setIndicatorNormalColor(indicatorNormalColor);
        mLinearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(densityUtil.dip2px(mContext, 2), densityUtil.dip2px(mContext, 0),
                densityUtil.dip2px(mContext, 2), densityUtil.dip2px(mContext, 0));
        mLinearLayout.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = densityUtil.dip2px(mContext, 10);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
        addView(mLinearLayout, params);

        //动态添加小圆点指示器
        for (int i = 0; i < mCount; i++) {
            //创建一个ImageView用于存放一个小圆点
            ImageView indicatorImage = new ImageView(mContext);
            if (i == 0) {
                //为了兼容,使用setBackgroundDrawable
                indicatorImage.setBackgroundDrawable(indicatorFocus);
            } else {
                indicatorImage.setBackgroundDrawable(indicatorNormal);
            }
            mLinearLayout.addView(indicatorImage, layoutParams);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(mCount);
        int targetItemPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % count;
        mViewPager.setCurrentItem(targetItemPosition);
    }

    //设置默认Shape
    private void setIndicatorNormalColor(int indicatorNormalColor) {
        indicatorNormal = new GradientDrawable();
        indicatorNormal.setShape(GradientDrawable.RECTANGLE);
        indicatorNormal.setColor(indicatorNormalColor);
        indicatorNormal.setCornerRadius(densityUtil.dip2px(mContext, 50));
    }

    //设置选中Shape
    private void setIndicatorFocusColor(int indicatorFocusColor) {
        indicatorFocus = new GradientDrawable();
        indicatorFocus.setShape(GradientDrawable.RECTANGLE);
        indicatorFocus.setColor(indicatorFocusColor);
        indicatorFocus.setCornerRadius(densityUtil.dip2px(mContext, 50));
    }

    //初始化View
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.GalleryCycleImage);
            indicatorFocusColor = attributes.getColor(R.styleable.GalleryCycleImage_indicatorFocusColor, 0);
            indicatorNormalColor = attributes.getColor(R.styleable.GalleryCycleImage_indicatorNormalColor, 0);
            indicatorSize = attributes.getDimensionPixelOffset(R.styleable.GalleryCycleImage_indicatorSize, 0);
            duration = attributes.getInteger(R.styleable.GalleryCycleImage_duration, 0);
            viewPagerHeight = attributes.getDimensionPixelOffset(R.styleable.GalleryCycleImage_viewPagerHeight, 0);

            //ViewPager布局
            mViewPager = new ViewPager(getContext());
            mViewPager.setClipChildren(false);
            mViewPager.setPageMargin(attributes.getDimensionPixelOffset(R.styleable.GalleryCycleImage_pageMargin, 0));
            mViewPager.setPageTransformer(true, new ScaleTransformer());
            attributes.recycle();
        }
        mContext = context;
        densityUtil = new DensityUtil();

        //设置滑动速度
        setSliderTransformDuration(1500);

        //触摸时不自动轮播
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopAutoPlay();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stopAutoPlay();
                        break;
                    case MotionEvent.ACTION_UP:
                        startAutoPlay();
                        break;
                }
                return false;
            }
        });

        //指示器圆点与轮播图联动
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
                    //ViewPager滑动或自动切换到哪一页就把哪一页对应的小圆点换Focus
                    if (i == position % mLinearLayout.getChildCount()) {
                        mLinearLayout.getChildAt(i).setBackgroundDrawable(indicatorFocus);
                    }
                    //否则换成Normal
                    else {
                        mLinearLayout.getChildAt(i).setBackgroundDrawable(indicatorNormal);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //必须设置ViewPagerMargin，否则画廊效果失效
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewPagerHeight);
        params.leftMargin = densityUtil.dip2px(mContext, 55);
        params.rightMargin = densityUtil.dip2px(mContext, 55);
        params.addRule(Gravity.CENTER);
        addView(mViewPager, params);
        startAutoPlay();
    }

    //自动轮播
    private Handler viewPagerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == SWITCH) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                    viewPagerHandler.sendEmptyMessageDelayed(SWITCH, duration);
                }
            }
            return false;
        }
    });

    public void startAutoPlay() {
        //避免出现重复消息传递
        stopAutoPlay();
        viewPagerHandler.sendEmptyMessageDelayed(SWITCH, duration);
    }

    public void stopAutoPlay() {
        viewPagerHandler.removeMessages(SWITCH);
    }


    /*
    ViewPager适配器
     */
    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //如果要自定义ItemView在这里操作
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            View v = null;
            if (mGalleyCycleImageListener != null)
                v = mGalleyCycleImageListener.setItem(imageView, getPosition(position));
            container.addView(v);
            return v;
        }

        //动态改变position
        private int getPosition(int position) {
            int p = position % mCount;
            if (p < 0) {
                p = mCount + p;
            }
            return p;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    //px转dp工具类
    class DensityUtil {

        public int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        public int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }


    //切换动画效果

    class ScaleTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.9f;
        //可以在这里更改动画的透明度
        private static final float MIN_ALPHA = 1f;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.1f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.1f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
        }
    }

    //设置滑动速度方法
    public void setSliderTransformDuration(int duration) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, null, duration);
            mScroller.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //调整viewpager中的滑动速率,默认250ms太快
    private class FixedSpeedScroller extends Scroller {
        private int mDuration = 1500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int mDuration) {
            this(context, interpolator);
            this.mDuration = mDuration;
        }
    }

    public void setGalleyCycleImageListener(GalleyCycleImageListener mGalleyCycleImageListener) {
        this.mGalleyCycleImageListener = mGalleyCycleImageListener;
    }

    //接口
    public interface GalleyCycleImageListener {
        View setItem(View imageView, int position);
    }
}
