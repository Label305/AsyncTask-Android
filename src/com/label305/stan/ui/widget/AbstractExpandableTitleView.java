package com.label305.stan.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.label305.stan.R;
import com.label305.stan.ui.anim.ExpandViewAnimation;
import com.label305.stan.utils.PixelUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public abstract class AbstractExpandableTitleView extends LinearLayout {

    private static final int ANIMATIONDURATION = 400;
    private static final int HEADERHEIGHTDP = 48;

    private CustomFontTextView mTitleTV;
    private ImageView mIconIV;
    private ImageView mMoreImageButton;
    private ProgressBar mProgressBar;

    private ViewGroup mContentVG;
    private View mContentView;

    private boolean mExpanded;
    private int mHeaderHeight;
    private int mBackgroundColor;
    private int mBackgroundDrawableResId;
    private int mIconResId;
    private String mText;
    private int mTextColor;
    private String mTextFont;
    private int mTextSize;
    private int mTextGravity;
    private int mArrowResId;
    private boolean mContentVisible;
    private boolean mDataAvailable = true;

    public AbstractExpandableTitleView(final Context context) {
        super(context);
        init(null);
    }

    public AbstractExpandableTitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AbstractExpandableTitleView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(final AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_expandabletitle, this);
        setOrientation(VERTICAL);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AbstractExpandableTitleView);
            if (a != null) {
                try {
                mBackgroundColor = a.getColor(R.styleable.AbstractExpandableTitleView_etv_background, R.color.transparent);
                } catch (Resources.NotFoundException e) {
                mBackgroundDrawableResId = a.getResourceId(R.styleable.AbstractExpandableTitleView_etv_background, 0);
                }

                mHeaderHeight = (int) a.getDimension(R.styleable.AbstractExpandableTitleView_headerHeight, PixelUtils.dpToPx(getContext(), HEADERHEIGHTDP));
            mIconResId = a.getResourceId(R.styleable.AbstractExpandableTitleView_etv_icon, 0);
                mText = a.getString(R.styleable.AbstractExpandableTitleView_titleText);
                mTextColor = a.getColor(R.styleable.AbstractExpandableTitleView_titleTextColor, R.color.black);
                mTextFont = a.getString(R.styleable.AbstractExpandableTitleView_titleTextFont);
                mTextSize = a.getDimensionPixelSize(R.styleable.AbstractExpandableTitleView_titleTextSize, getContext().getResources().getDimensionPixelSize(R.dimen.textsize_medium));
                mTextGravity = a.getInt(R.styleable.AbstractExpandableTitleView_titleGravity, Gravity.LEFT);
                mArrowResId = a.getResourceId(R.styleable.AbstractExpandableTitleView_arrow, 0);
                mContentVisible = a.getBoolean(R.styleable.AbstractExpandableTitleView_contentVisible, true);
                mExpanded = mContentVisible;
                a.recycle();
            }
        }

        setupViews();
    }

    private void setupViews() {
        View bannerView = findViewById(R.id.view_expandabletitle_banner);
        LinearLayout.LayoutParams layoutParams = (LayoutParams) bannerView.getLayoutParams();
        layoutParams.height = mHeaderHeight;
        bannerView.setLayoutParams(layoutParams);

        if (mBackgroundColor == 0) {
            bannerView.setBackgroundResource(mBackgroundDrawableResId);
        } else {
            bannerView.setBackgroundColor(mBackgroundColor);
        }
        bannerView.setOnClickListener(new BannerOnClickListener());

        mTitleTV = (CustomFontTextView) findViewById(R.id.view_expandabletitle_titletv);
        mTitleTV.setFont(mTextFont);
        mTitleTV.setTextColor(mTextColor);
        mTitleTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTitleTV.setText(mText);
        mTitleTV.setGravity(mTextGravity);

        mIconIV = (ImageView) findViewById(R.id.view_expandabletitle_iconiv);
        mIconIV.setImageResource(mIconResId);

        mMoreImageButton = (ImageView) findViewById(R.id.view_expandabletitle_morebutton);
        if (mArrowResId == 0) {
            mMoreImageButton.setImageDrawable(null);
        } else {
            mMoreImageButton.setImageResource(mArrowResId);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.view_expandabletitle_progressbar);

        mContentVG = (ViewGroup) findViewById(R.id.view_expandabletitle_expandedcontent);
        mContentVG.setVisibility(mContentVisible ? VISIBLE : GONE);
        mContentView = createContentView(mContentVG);
        mContentVG.addView(mContentView);
    }

    protected abstract View createContentView(final ViewGroup parent);

    protected View getContentView() {
        return mContentView;
    }

    public void setTitle(final String title) {
        mTitleTV.setText(title);
    }

    public void setIcon(final int resId) {
        mIconIV.setImageResource(resId);
    }

    public void setIcon(final Drawable drawable) {
        mIconIV.setImageDrawable(drawable);
    }

    public void setIcon(final Bitmap bitmap) {
        mIconIV.setImageBitmap(bitmap);
    }

    public ViewGroup getContentHolder() {
        return mContentVG;
    }


    /**
     * If !mDataAvailable, will show a indeterminate spinner instead of an arrow
     * until setDataAvailable(true);
     */
    public void setDataAvailable(final boolean dataAvailable) {
        this.mDataAvailable = dataAvailable;

        if (dataAvailable) {
            mProgressBar.setVisibility(View.GONE);
            mMoreImageButton.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mMoreImageButton.setVisibility(View.GONE);
        }
    }

    public boolean hasDataAvailable() {
        return mDataAvailable;
    }

    public void expandContent() {
        final int widthSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
        final int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mContentVG.measure(widthSpec, heightSpec);

        ExpandViewAnimation a = new ExpandViewAnimation(mContentVG, ANIMATIONDURATION, ExpandViewAnimation.EXPAND);
        a.setHeight(mContentVG.getMeasuredHeight());
        mContentVG.startAnimation(a);

        mExpanded = true;
    }

    public void collapseContent() {
        ExpandViewAnimation a = new ExpandViewAnimation(mContentVG, ANIMATIONDURATION, ExpandViewAnimation.COLLAPSE);
        mContentVG.startAnimation(a);

        mExpanded = false;
    }

    @Override
    public void setVisibility(final int visibility) {
        if (visibility == View.GONE && getVisibility() != View.GONE) {
            collapseETV();
        } else if (visibility == View.VISIBLE && getVisibility() != View.VISIBLE) {
            expandETV();
        } else {
            super.setVisibility(visibility);
        }
    }

    private void collapseETV() {
        LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) getLayoutParams());
        final int origHeight = getHeight();
        final int origTopMargin = layoutParams == null ? 0 : layoutParams.topMargin;
        final int origBottomMargin = layoutParams == null ? 0 : layoutParams.bottomMargin;

        ValueAnimator etvAnimator = ValueAnimator.ofInt(origHeight, 0);
        etvAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                if (getLayoutParams() == null) {
                    return;
                }

                LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.height = (Integer) animator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator topMarginAnimator = ValueAnimator.ofInt(origTopMargin, 0);
        topMarginAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                if (getLayoutParams() == null) {
                    return;
                }

                LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.topMargin = (Integer) animator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator bottomMarginAnimator = ValueAnimator.ofInt(origBottomMargin, 0);
        bottomMarginAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                if (getLayoutParams() == null) {
                    return;
                }

                LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.bottomMargin = (Integer) animator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(etvAnimator, topMarginAnimator, bottomMarginAnimator);
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(final Animator animator) {
                AbstractExpandableTitleView.super.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.height = origHeight;
                layoutParams.topMargin = origTopMargin;
                layoutParams.bottomMargin = origBottomMargin;
                setLayoutParams(layoutParams);
            }
        });
        set.start();
    }

    private void expandETV() {
        super.setVisibility(View.VISIBLE);

        int origHeight = getLayoutParams().height;
        int origTopMargin = ((MarginLayoutParams) getLayoutParams()).topMargin;
        int origBottomMargin = ((MarginLayoutParams) getLayoutParams()).bottomMargin;

        ValueAnimator etvAnimator = ValueAnimator.ofInt(0, origHeight);
        etvAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                if (getLayoutParams() == null) {
                    return;
                }

                LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.height = (Integer) animator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator topMarginAnimator = ValueAnimator.ofInt(0, origTopMargin);
        topMarginAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                if (getLayoutParams() == null) {
                    return;
                }

                LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.topMargin = (Integer) animator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator bottomMarginAnimator = ValueAnimator.ofInt(0, origBottomMargin);
        bottomMarginAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                if (getLayoutParams() == null) {
                    return;
                }

                LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.bottomMargin = (Integer) animator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(etvAnimator, topMarginAnimator, bottomMarginAnimator);
        set.start();
    }

    private class BannerOnClickListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            if (mDataAvailable) {
                if (mExpanded) {
                    collapseContent();
                } else {
                    expandContent();
                }
            }
        }
    }
}
