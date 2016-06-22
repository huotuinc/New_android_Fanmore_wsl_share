package cy.com.morefan.frag;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragManager {
	public enum FragType{
		Task, Rule, My, More, Share, Prentice, Tool
	}
	private int viewId;
	private FragmentManager fragManager;
	private FragType preFragType;
	private FragType curFragType;
	//private HashMap<FragType, BaseFragment> frags;

	public FragManager(FragmentActivity context, int viewId){
		this.viewId = viewId;
		this.fragManager = context.getSupportFragmentManager();
		//frags = new HashMap<FragManager.FragType, BaseFragment>();

	}
	public FragType getCurrentFragType(){
		return this.curFragType;
	}
	public BaseFragment getCurrentFrag(){
		return getFragmentByType(preFragType);
	}
	public void setCurrentFrag(FragType type){
		if(type == preFragType)
			return;
		curFragType = type;
		FragmentTransaction ft = fragManager.beginTransaction();
		String fragTag = makeFragmentName(viewId, type);
		BaseFragment frag =  (BaseFragment) fragManager.findFragmentByTag(fragTag);
		if(frag == null){
			switch (type) {
//			case Tool:
//				frag = ToolFrag.newInstance();
//				break;
			case Prentice:
				frag = PrenticeFrag.newInstance();
				break;
			case Rule:
				frag = RuleFrag.newInstance();
				break;
			case My:
				frag = MyFrag.newInstance();
				break;
			case More:
				frag = MoreFrag.newInstance();
				break;
			case Share:
				frag = ShareFrag.newInstance();
				break;
			default:
				frag = TaskNewFrag.newInstance();
				//frag = TaskFrag.newInstance();
				break;
			}
			//frags.put(type, frag);
			ft.add(viewId, frag, fragTag);
		}else{
			frag.onReshow();
		}
		ft.show(frag);
		if(preFragType != null){
			BaseFragment preFrag = getFragmentByType(preFragType);
			preFrag.onPause();
			ft.hide(preFrag);
		}


		//如果替换或者删除一个Fragment然后让用户可以导航到上一个Fragment，你必须在调用commit()方法之前调用addToBackStack()方法添加到回退栈
		//ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
		preFragType = type;



//		if(type == preFragType)
//			return;
//		FragmentTransaction ft = fragManager.beginTransaction();
//		BaseFragment frag = frags.get(type);
//		if(frag == null){
//			switch (type) {
//			case Goods:
//				frag = GoodsFrag.newInstance();
//				break;
//			case My:
//				frag = MyFrag.newInstance();
//				break;
//			case More:
//				frag = MoreFrag.newInstance();
//				break;
//			default:
//				frag = TaskFrag.newInstance();
//				break;
//			}
//			frags.put(type, frag);
//			ft.add(viewId, frag);
//		}else{
//			frag.onReshow();
//		}
//		ft.show(frag);
//		if(preFragType != null)
//			ft.hide(frags.get(preFragType));
//
//		//如果替换或者删除一个Fragment然后让用户可以导航到上一个Fragment，你必须在调用commit()方法之前调用addToBackStack()方法添加到回退栈
//		//ft.addToBackStack(null);
//		ft.commitAllowingStateLoss();
//		preFragType = type;
		//setUserVisibleHint();
	}
	public BaseFragment getFragmentByType(FragType type){
		return (BaseFragment) fragManager.findFragmentByTag(makeFragmentName(viewId, type));
	}

	private String makeFragmentName(int viewId, FragType type) {
		return "android:switcher:" + viewId + ":" + type;
	}
//	private void setUserVisibleHint(){
//		FragmentTransaction ft = fragManager.beginTransaction();
//		frags.get(preFragType).setMenuVisibility(false);
//		frags.get(preFragType).setUserVisibleHint(true);
//		ft.commitAllowingStateLoss();
//	}
}
