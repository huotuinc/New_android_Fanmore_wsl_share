package cy.com.morefan.bean;

import android.graphics.Point;

public class AllScoreData implements  BaseData{
	//public int id;
	public int scanCount;
	public String score;
	/**
	 * 额外描述（多条以^隔开)
	 */
	public String extra;
	public String date;
	public Point point;

	@Override
	public boolean equals(Object o) {
		if (o instanceof AllScoreData) {
			AllScoreData data = (AllScoreData) o;
			return this.date.equals(data.date);
		}
		return super.equals(o);
	}

	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}



}
