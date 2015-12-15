package cy.com.morefan.view;

import com.nineoldandroids.animation.ObjectAnimator;

import cy.com.morefan.R;
import cy.com.morefan.view.ElasticScrollView.ScrollType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class HeadView {


	private View view;
	private ImageView imgAnim;
	private ObjectAnimator objAnim;
	public HeadView(Context context){
		view = LayoutInflater.from(context).inflate(R.layout.tool_head, null);
		imgAnim = (ImageView) view.findViewById(R.id.img1);
	}
	public View getView(){
		return view;
	}
	public void onRefreshComplete(){
		if(null != objAnim){
			objAnim.end();
			objAnim.cancel();
		}

	}
	public void onRefresh(float per, ScrollType type){

		switch (type) {
		case DONE:
			if(null != objAnim){
				objAnim.end();
				objAnim.cancel();
			}
			break;
		case PULL_To_REFRESH:
		case RELEASE_To_REFRESH:
			if(per < 0)
				imgAnim.setBackgroundResource(R.drawable.e1);
			if(per > 0)
				imgAnim.setBackgroundResource(R.drawable.e2);
			if(per > 0.1)
				imgAnim.setBackgroundResource(R.drawable.e3);
			if(per > 0.2)
				imgAnim.setBackgroundResource(R.drawable.e4);
			if(per > 0.3)
				imgAnim.setBackgroundResource(R.drawable.e5);
			break;
		case REFRESHING:
			imgAnim.setBackgroundResource(R.drawable.e5);
			if(null == objAnim){
				objAnim = ObjectAnimator.ofFloat(imgAnim, "rotation", 0, 360);
				objAnim.setRepeatCount(-1);
				objAnim.setInterpolator(new LinearInterpolator());
				objAnim.setDuration(500);
			}
			objAnim.start();
			break;

		default:
			break;
		}



	}
}
