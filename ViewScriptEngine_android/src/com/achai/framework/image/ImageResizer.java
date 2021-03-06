package com.achai.framework.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.Log;

import com.achai.framework.debug.BuildConfig;

/**
 * 用于调整图片的大小 圆角等
 * 
 * @author tom_achai
 * 
 */
public class ImageResizer extends ImageWorker {
	private static final String TAG = "ImageWorker";
	protected int mImageWidth;
	protected int mImageHeight;
	protected boolean isScale = false;

	// 设置是否图片圆角
	protected boolean isRound = false;
	//默认圆角弧度
	protected int roundPx = 10;
	
	public void setRoundPx(int roundPx) {
		this.roundPx = roundPx;
	}

	public void setRoound(boolean isRoound) {
		this.isRound = isRoound;
	}

	public void setmNeedScale(boolean mNeedScale) {
		this.isScale = mNeedScale;
	}

	public static Bitmap setImageRound(Bitmap bitmap, int roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		// 圆角大小
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * @param context
	 */
	public ImageResizer(Context context, int imageWidth, int imageHeight) {
		super(context);
		setImageSize(imageWidth, imageHeight);
	}

	/**
	 * 设置宽高一致
	 * 
	 * @param context
	 * @param size
	 */
	public ImageResizer(Context context, int size) {
		super(context);
		setImageSize(size, size);
	}

	/**
	 * 设置目标图片大小
	 * 
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height) {
		mImageWidth = width;
		mImageHeight = height;
	}

	/**
	 * 直接重置资源文件的图片大小
	 * 
	 * @param resId
	 * @return
	 */
	private Bitmap processBitmap(int resId) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "processBitmap - " + resId);
		}
		return decodeSampledBitmapFromResource(mContext.getResources(), resId,
				mImageWidth, mImageHeight);
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		return processBitmap(Integer.parseInt(String.valueOf(data)));
	}

	public static synchronized Bitmap decodeSampledBitmapFromFile(
			String filename, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 返回一个正方形的图片大小
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		// final int height = options.outHeight;
		// final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further.
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

}
