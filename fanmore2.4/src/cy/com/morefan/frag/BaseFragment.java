package cy.com.morefan.frag;

import android.support.v4.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment{
//	public abstract void onClickTitleLeft();
//	public abstract void onClickTitleMiddle();
//	public abstract void onClickTitleRight();
	public abstract void onReshow();//再切换至要显示的frag,onReshow()不同于onResume()
	public abstract void onFragPasue();//暂时不可见
	public abstract void onClick(View view);

}
