package cy.com.morefan.guide;
 
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
public class GetBitMap implements Runnable {
	
	private List<BitMapMsg> mQueue = new ArrayList<BitMapMsg>();
	private static boolean running = false;
	private static GetBitMap mGetBitmap = null;
	private Bitmap bitmap;
	private GetBitMap(){	
	}
	
	public static GetBitMap instance(){
		if(mGetBitmap == null){
			mGetBitmap = new GetBitMap();
		}
		return mGetBitmap;
	}
	
	
	public void add(Context context,Handler handler,String path, int mCode){
		add(context, handler, path, mCode,null);
	}
	
	public void add(Context context,Handler handler,String path, int mCode,ImageView imageView){
		BitMapMsg msg = new BitMapMsg();
		msg.mConext = context;
		msg.mHandler = handler;
		msg.path = path;
		msg.mCode = mCode;
		msg.mImgView = imageView;
		mQueue.add(msg);
		if(!running){
		    new Thread(this).start();
		}
	}
	
	public void run() {
		running = true;
		while(mQueue.size() > 0 ){
			BitMapMsg msg = null;
			synchronized (mQueue) {
				msg = mQueue.remove(0);
			}
			if(msg != null && !msg.path.equals("")){
			    Message message = new Message();
                message.what = msg.mCode;
                if(msg.mImgView != null){
                    BitmapData bitmapData = new BitmapData(getBitmap(msg,msg.path),msg.mImgView);
                    message.getData().putParcelable("bitmap", bitmapData);
                    msg.mHandler.sendMessage(message);
                }
			}
		}
		running = false;
	}
	
	/**
     * 以最小内存获取资源图片
     * @param resId
     * @return
     */
    private Bitmap getBitmap(BitMapMsg msg,String path){
        try {
            InputStream is = msg.mConext.getResources().getAssets().open(path);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opt);
            opt.inSampleSize = computeSampleSize(opt, -1, 480*800);  
            
            opt.inJustDecodeBounds = false;
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(is, null, opt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    /**
     * 动态计算inSampleSize
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

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

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

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
	public static class BitMapMsg{
		public Context mConext;
		public Handler mHandler;
		public String path = "";
		public int mCode;           
		public ImageView mImgView;
		
		public BitMapMsg(){
			
		}
	}
	
}

