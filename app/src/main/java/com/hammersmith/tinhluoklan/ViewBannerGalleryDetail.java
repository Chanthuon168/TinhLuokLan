package com.hammersmith.tinhluoklan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by ASUS on 2015/3/18.
 */
public class ViewBannerGalleryDetail extends RelativeLayout {
    private Context mContext;
    private ViewFlipper mViewFlipper;
    private TextView tvHint;
    private TextView tvSubtitle;
    private float lastX;
    private final long FLIP_NEXT_DELAY = 2000;
    private final static int WHAT_ADD_VIEW = 101, WHAT_DOWNLOAD_ERROR = 102;
    private ArrayList<BannerItem> mListData = new ArrayList<>();
    LinearLayout layoutBannerIndex;
    private boolean beforeSizeChanged = true;
    private int dotSize = 13;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_ADD_VIEW:
                    log("WHAT_ADD_VIEW");
                    int i = 0;
                    for (; mListData.size() > 0 && i < mListData.size(); i++) {
                        ImageView iv = new ImageView(mContext);
                        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        final BannerItem item = mListData.get(i);
                        String bannerDir = Utils.getExternalPathPrefix(mContext, "banner");
//                        log("bannerDir:" + bannerDir);
                        if (bannerDir != null) {
                            String localPath = bannerDir + getMD5(item.imageUrl);
                            File localFile = new File(localPath);
                            if (localFile != null && localFile.exists()) {
                                BitmapFactory.Options opts = new BitmapFactory.Options();
                                opts.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(localPath, opts);
//                                opts.inSampleSize = computeSampleSize(opts, -1, 512 * 128);
                                opts.inJustDecodeBounds = false;

                                Bitmap bitmap = null;
                                int tryCount = 0;
                                while (tryCount < 5) {
                                    try {
                                        bitmap = BitmapFactory.decodeFile(localPath, opts);
                                        break;
                                    } catch (Exception e) {
                                        opts.inSampleSize = opts.inSampleSize * 2;
                                        tryCount++;
                                    }
                                }
                                if (bitmap != null) {
                                    iv.setImageBitmap(bitmap);
                                    mViewFlipper.addView(iv);
                                    if (!TextUtils.isEmpty(item.linkUrl) && item.linkUrl.startsWith("http://")) {
                                        iv.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(item.linkUrl));
                                                mContext.startActivity(intent);
                                            }
                                        });
                                    }

                                    ImageView image = new ImageView(mContext);
                                    image.setPadding(3,0,3,0);
                                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    image.setAdjustViewBounds(true);
                                    if (i == 0) {
                                        image.setBackgroundResource(android.R.drawable.presence_online);
                                        if (!TextUtils.isEmpty(item.title))
                                            tvSubtitle.setText(item.title);
                                    } else {
                                        image.setBackgroundResource(android.R.drawable.presence_invisible);
                                    }

                                    layoutBannerIndex.addView(image, new LayoutParams(dotSize, dotSize));
                                } else {
                                    mHandler.sendEmptyMessage(WHAT_DOWNLOAD_ERROR);
                                    break;
                                }

                            }
                        } else {

                            mHandler.sendEmptyMessage(WHAT_DOWNLOAD_ERROR);
                            break;
                        }


                    }
                    if (mListData.size() == 0) {
                        tvHint.setText("No data");
                    } else if (i == mListData.size()) {

                        tvHint.setVisibility(View.GONE);
                        startFlip();
                    }
                    break;

                case WHAT_DOWNLOAD_ERROR:
                    log("WHAT_DOWNLOAD_ERROR");
                    tvHint.setText("Failed to load, click reload");
                    break;
            }
        }
    };

    private Runnable flipNext = new Runnable() {
        @Override
        public void run() {
            if (mViewFlipper != null) {
                mViewFlipper.showNext();
            }
            for (int i = 0; layoutBannerIndex != null && i < layoutBannerIndex.getChildCount(); i++) {
                ImageView iv = (ImageView) layoutBannerIndex.getChildAt(i);
                iv.setPadding(3,0,3,0);
                if (i == mViewFlipper.getDisplayedChild()) {
                    iv.setBackgroundResource(android.R.drawable.presence_online);

                    BannerItem item = mListData.get(i);
                    if (!TextUtils.isEmpty(item.title))
                        tvSubtitle.setText(item.title);
                } else {
                    iv.setBackgroundResource(android.R.drawable.presence_invisible);
                }
            }

            mHandler.postDelayed(flipNext, FLIP_NEXT_DELAY);
        }
    };

    public ViewBannerGalleryDetail(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ViewBannerGalleryDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewBannerGalleryDetail(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View layout = LayoutInflater.from(mContext).inflate(R.layout.view_banner_gallery_detail, this, true);
        tvHint = (TextView) layout.findViewById(R.id.tv_hint);
        tvHint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flip(mListData, true);
            }
        });
        layoutBannerIndex = (LinearLayout) layout.findViewById(R.id.layout_dots);
        tvSubtitle=(TextView)layout.findViewById(R.id.tv_subtitle);
        mViewFlipper = (ViewFlipper) layout.findViewById(R.id.viewflipper);
        mViewFlipper.setFlipInterval(2000);
//        mViewFlipper.setAutoStart(true);
        animToLeft();

        mViewFlipper.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();

                        if (mHandler != null) {
                            mHandler.removeCallbacks(flipNext);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        float currentX = event.getX();

                        // Handling left to right screen swap.
                        if (currentX - lastX < -20) {
                            // Display next screen.
                            mViewFlipper.showNext();
                        } else if (currentX - lastX > 20) {

                            animToRight();
                            // Display previous screen.
                            mViewFlipper.showPrevious();

                            animToLeft();
                        }

                        startFlip();

                        break;
                }

                return true;
            }
        });

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        log("onSizeChanged()");
        log("this.getWdith():" + this.getWidth());
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = this.getWidth() * 3 / 5;
        this.setLayoutParams(params);
        log("params new height:" + params.height);
        beforeSizeChanged = false;
//        dotSize = ViewBannerGallery.this.getWidth() / 24;
    }

    public void flip(final ArrayList<BannerItem> listData, final boolean showError) {
        tvHint.setText("Loading…");
        tvHint.setVisibility(View.VISIBLE);
        mListData = listData;
        new Thread() {
            @Override
            public void run() {
                while (beforeSizeChanged) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                    }
                }
                boolean fullDownloaded = true;
                boolean error = false;
                mListData = listData;
                for (int i = 0; i < listData.size(); i++) {
                    BannerItem item = listData.get(i);
                    String bannerDir = Utils.getExternalPathPrefix(mContext, "banner");
                    log("--->bannerDir:" + bannerDir);
                    if (bannerDir != null) {
                        String localPath = bannerDir + getMD5(item.imageUrl);
                        File imageFile = new File(localPath);
                        log("---------Check the file" + item.imageUrl);
                        if (imageFile == null || !imageFile.exists()) {
                            fullDownloaded = false;

                            log("---------File does not exist, start the download");
                            String fileName = Utils.doHttpDownload2(mContext, item.imageUrl, "banner", getMD5(item.imageUrl));
                            log("---------Download results" + fileName);
                            if (TextUtils.isEmpty(fileName)) {
                                error = true;
                                break;
                            }
                        } else {
                            log("---------File exists, skip to view the next");
                            continue;
                        }
                    }
                }

                if (fullDownloaded || !error) {
                    mHandler.sendEmptyMessage(WHAT_ADD_VIEW);
                } else if (showError) {
                    mHandler.sendEmptyMessage(WHAT_DOWNLOAD_ERROR);
                }

            }
        }.start();
    }

    public int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public class BannerItem {
        /**
         * 本地或在线
         */
        public String imageUrl;
        public String linkUrl;
        public String title;

        public BannerItem(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    private void animToRight() {
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_left));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_right));
    }

    private void animToLeft() {
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_right));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_left));
    }

    public void startFlip() {
        mHandler.removeCallbacks(flipNext);

        mHandler.postDelayed(flipNext, FLIP_NEXT_DELAY);

    }

    private String getMD5(String str) {
        String suffix = str.substring(str.lastIndexOf("."));
//        log("suffix:"+suffix);
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString() + suffix;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void log(String str) {
        Log.e("ViewBannerGallery", str);
    }
}