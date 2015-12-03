package oren.com.inplacecirclecroppertest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Oren on 12/3/2015.
 */
public class CropCircleView extends TouchImageView {

    protected int mSidePadding = 50;
    protected Paint mPaint;
    protected Path mCirclePath;
    protected RectF mCirclePathRect;
    protected Path mSquarePath;
    protected RectF mSquarePathRect;

    public int getSidePadding()
    {
        return mSidePadding;
    }

    public void setSidePadding(final int sidePadding)
    {
        if(getWidth() - (2*sidePadding) <= 0 || getHeight() - (2*sidePadding) <= 0 || sidePadding < 0)
        {
            Log.e("moo", "padding size bigger than image, keeping old size");
            return;
        }


        this.mSidePadding = sidePadding;
        calculateRectangles();
        invalidate();
    }

    public CropCircleView(final Context context)
    {
        super(context);
        init();
    }

    public CropCircleView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    protected void init()
    {
        mPaint = new Paint();
        mPaint.setARGB(200, 255, 255, 255);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStrokeWidth(0);
        mCirclePath = new Path();
        mCirclePath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        mCirclePathRect = new RectF(getSidePadding(),getSidePadding(),getWidth() - getSidePadding() , getHeight() - getSidePadding());
        mSquarePath = new Path();
        mSquarePath.setFillType(Path.FillType.EVEN_ODD);
        mSquarePathRect = new RectF(0,0,getWidth(),getHeight());
        setMinZoom(1.2f);
        setZoom(1.2f);
    }

    /**
     * Performs synchronous image cropping based on configuration.
     *
     * @return A {@link Bitmap} cropped based on viewport and user panning and zooming or <code>null</code> if no {@link Bitmap} has been
     * provided.
     */
    @Nullable
    public Bitmap crop() {

        boolean drawingCacheEnabled = isDrawingCacheEnabled();
        setDrawingCacheEnabled(true);
        buildDrawingCache(false);
        final Bitmap drawingCache = getDrawingCache(false);


        if (drawingCache == null) {
            return null;
        }

        final Bitmap src = drawingCache;
        final Bitmap.Config srcConfig = src.getConfig();
        final Bitmap.Config config = srcConfig == null ? Bitmap.Config.ARGB_8888 : srcConfig;

        final Bitmap dst = Bitmap.createBitmap(getWidth() - (getSidePadding() * 2), getHeight() - (getSidePadding() * 2), config);

        Canvas canvas = new Canvas(dst);
        canvas.save();
        canvas.translate(-getSidePadding(), -getSidePadding());
        Matrix matrix = new Matrix();
        matrix.set(getMatrix());

        //int tweakedPadding = getSidePadding() - (getSidePadding()/2);
        int tweakedPadding = getSidePadding();
        //matrix.setRectToRect(new RectF(tweakedPadding,tweakedPadding,getWidth()-tweakedPadding,getHeight()-tweakedPadding),new RectF(0,0,getWidth(),getHeight()), Matrix.ScaleToFit.CENTER);
        canvas.drawBitmap(drawingCache, matrix, new Paint());

        Paint paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        Path circlePath = new Path();
        circlePath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        final RectF rectF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        circlePath.addOval(rectF, Path.Direction.CW);
        canvas.restore();
        canvas.drawPath(circlePath,paint);

        if(!drawingCacheEnabled)
        {
            setDrawingCacheEnabled(false);
        }

        return dst;
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);
        mCirclePath.reset();
        mCirclePath.addOval(mCirclePathRect, Path.Direction.CW);
        mSquarePath.reset();
        int sidePaddingWithOverlapDecrease = getSidePadding() - 2; // we remove two pixels where the square and circle paths overlap (an issue with opacity).
        mSquarePath.moveTo(sidePaddingWithOverlapDecrease, sidePaddingWithOverlapDecrease);
        mSquarePath.lineTo(getWidth() - sidePaddingWithOverlapDecrease, sidePaddingWithOverlapDecrease);
        mSquarePath.lineTo(getWidth() - sidePaddingWithOverlapDecrease, getHeight() - sidePaddingWithOverlapDecrease);
        mSquarePath.lineTo(sidePaddingWithOverlapDecrease, getHeight() - sidePaddingWithOverlapDecrease);
        mSquarePath.lineTo(sidePaddingWithOverlapDecrease, sidePaddingWithOverlapDecrease);
        mSquarePath.lineTo(0, 0);
        mSquarePath.lineTo(0, getHeight());
        mSquarePath.lineTo(getWidth(), getHeight());
        mSquarePath.lineTo(getWidth(), 0);
        mSquarePath.lineTo(0, 0);
        mSquarePath.close();
        canvas.drawPath(mCirclePath, mPaint);
        canvas.drawPath(mSquarePath, mPaint);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateRectangles();
    }

    protected void calculateRectangles()
    {
        mCirclePathRect = new RectF(getSidePadding(),getSidePadding(),getWidth() - getSidePadding() , getHeight() - getSidePadding());
        mSquarePathRect = new RectF(0,0,getWidth(),getHeight());
    }
}
