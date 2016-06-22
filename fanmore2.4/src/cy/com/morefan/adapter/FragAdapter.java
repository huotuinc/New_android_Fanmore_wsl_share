package cy.com.morefan.adapter;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragAdapter extends FragmentPagerAdapter{
	private ArrayList<Fragment> frags;

	public FragAdapter(FragmentManager fm, ArrayList<Fragment> frags) {
		super(fm);
		this.frags = frags;
	}


	@Override
	public Fragment getItem(int arg0) {
		return frags.get(arg0);
	}

	@Override
	public int getCount() {
		return frags.size();
	}

}
