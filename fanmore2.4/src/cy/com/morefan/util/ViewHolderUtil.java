package cy.com.morefan.util;

import android.util.SparseArray;
import android.view.View;
/**
 * in adapter methods getview to use ViewHolderUtil.get(convertView, vid)
 * @author cy
 *
 */
public class ViewHolderUtil{

	private ViewHolderUtil(){

	}
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View convertView, int vid){
		SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
		if(viewHolder == null){
			viewHolder = new SparseArray<View>();
			convertView.setTag(viewHolder);
		}
		View childView = viewHolder.get(vid);
		if(null == childView){
			childView = convertView.findViewById(vid);
			viewHolder.put(vid, childView);
		}
		return (T) childView;
	}

}
