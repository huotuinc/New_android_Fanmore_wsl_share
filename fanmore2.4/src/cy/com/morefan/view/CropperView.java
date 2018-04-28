package cy.com.morefan.view;

import com.edmodo.cropper.CropImageView;

import cy.com.morefan.R;
import cy.com.morefan.util.L;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

@SuppressLint("SimpleDateFormat")
public class CropperView {
	 private static final int ROTATE_NINETY_DEGREES = 90;
	public interface OnCropperBackListener{
		void OnCropperBack(Bitmap bitmap , int busType);
	}
	private OnCropperBackListener listener;
	private WindowManager mWindowManager;
	private Context mContext;
	private CropImageView cropImageView;
	private View mView;
	private int mOutWidth;
	private int mOutHeight;
	private boolean changSize;
	private int busType=0;

	public void setBusType(int busType){
		this.busType=busType;
	}

	public CropperView(Context context, OnCropperBackListener listener , int busType){
		this.mContext = context;
		this.listener = listener;
		this.busType=busType;
		//initView(context);
	}
	private void dismiss() {
		if (mView != null && mView.getParent() != null) {
			mWindowManager.removeView(mView);
		}
	}
	/**
	 *
	 * @param source
	 * @param outWidth 输出宽度
	 * @param outHeight 输出宽度
	 * 640 等比缩放
	 */
	public void cropper(Bitmap source){
		if(source == null)
			return;
		changSize = false;
		L.i(">>>>>>>>0:" + source.getWidth() + "," + source.getHeight());
		int widthMul = source.getWidth()/960;
		int heightMul = source.getHeight()/960;
		int mul = Math.min(widthMul, heightMul);
		mul = mul == 0 ? 1 : mul;
		this.mOutWidth = source.getWidth()	/ mul;
		this.mOutHeight = source.getHeight() / mul;
		L.i(">>>>>>>>1:" + mOutWidth + "," + mOutHeight);
		if (mWindowManager == null)
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		if(mView == null){
			mView = LayoutInflater.from(mContext).inflate(R.layout.cropper, null);
			cropImageView = (CropImageView) mView.findViewById(R.id.CropImageView);
			cropImageView.setFixedAspectRatio(true);
			//
			 cropImageView.setAspectRatio(10, 10);

			 mView.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {

				 			@Override
				 			public void onClick(View v) {
				 				dismiss();
				 				 if(listener != null){
				 					listener.OnCropperBack(createNewSize(cropImageView.getCroppedImage()) , busType);
//				 					 if(mOutWidth == 0 && mOutHeight == 0){
//				 						listener.OnCropperBack(cropImageView.getCroppedImage());
//				 					 }else{
//				 						listener.OnCropperBack(createNewSize(cropImageView.getCroppedImage()));
//				 					 }
				 				 }

				 			}
				 		});
			 mView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

				 			@Override
				 			public void onClick(View v) {
				 				dismiss();
				 				 if(listener != null)
				 					 listener.OnCropperBack(null , busType);

				 			}
				 		});
			 mView.findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {

				 			@Override
				 			public void onClick(View v) {
				 				changSize = !changSize;
				 				//旋转
				 				cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
				 			}
				 		});
			 mView.setFocusableInTouchMode(true);
			 mView.setOnKeyListener(new View.OnKeyListener() {
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if(keyCode == KeyEvent.KEYCODE_BACK ){
							System.out.println(">>>>>>KEYCODE_BACK");
							dismiss();
							 if(listener != null)
								 listener.OnCropperBack(null , busType );
							// return true;
						}
						return false;
					}
				});


		}

		cropImageView.setImageBitmap(source);

		if (mView.getParent() == null) {
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_APPLICATION,
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
					PixelFormat.TRANSLUCENT);
			mWindowManager.addView(mView, lp);
		}


	}
	private Bitmap createNewSize(Bitmap source){
		 int width = source.getWidth();
		    int height = source.getHeight() ;

		    Matrix matrix = new Matrix();
		    if(changSize){
		    	int temp = mOutWidth;
		    	mOutWidth = mOutHeight;
		    	mOutHeight = temp;
		    }
		    float scaleWidth = ((float) 200 / width);
		    float scaleHeight = ((float) 200 / height);

		    matrix.postScale(scaleWidth, scaleHeight);
		    Bitmap newbmp = Bitmap.createBitmap(source, 0, 0, width, height,
		            matrix, true);
		return newbmp;

	}



}
