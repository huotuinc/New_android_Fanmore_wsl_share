package cy.com.morefan.view;

import java.util.HashMap;
import java.util.List;

import cy.com.morefan.adapter.TrendAdapter;
import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TrendView extends View implements android.view.View.OnTouchListener, OnScrollListener {
	public interface OnLoadMoreListener{
		void onLoadMore();
	}
	public enum LoadStatus{
		Loading, Normal
	}
	public static int POINT_COUNT = 8;//一屏内点个数
	private int width;
	private int height;
	private int perWidth;
	private float perHeight;
	private Paint paintLine;//线
	private Paint paintSelect;//高亮圆圈
	private Paint paintScore;//积分
	private Paint paintDate;//日期
	private float textDis;//积分日期高度差

	private ListView mListView;
	private List<AllScoreData> trendDatas;
	private boolean isShowInCenter;
	private OnLoadMoreListener listener;
	private LoadStatus loadStatus;
	private int diameterLarge;
	private int diameterMiddle;
	private int diameterSmall;
	//static List<Point> points = new ArrayList<Point>();

	public TrendView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int[] size = DensityUtil.getSize(context);
		width = size[0];
		height = size[1]/4;
		perWidth = width/(POINT_COUNT + 1);
		initPaint();
	}
	public void setOnLoadMoreListener(OnLoadMoreListener listener){
		this.loadStatus = LoadStatus.Normal;
		this.listener = listener;
	}
	public void setOnFinishLoadMore(){
		this.loadStatus = LoadStatus.Normal;
	}
	public HashMap<String, Integer> positionDats;
	public void setTrendData(List<AllScoreData> datas, double minVaule, double maxVaule){
		minVaule = (int)minVaule;
		maxVaule = (int)maxVaule;
		L.i(">>>min:" + minVaule + ",max:" + maxVaule);
		int dis = (int) (maxVaule - minVaule);
		dis = (int) (dis == 0 ? maxVaule : dis);
		perHeight = (float) (height*0.6/dis);
		int dy = DensityUtil.dip2px(getContext(), 8);
		for(int i = 0, length = datas.size(); i < length; i++){
			AllScoreData data = datas.get(i);
			int x = width - (i + 1) * perWidth;
			int y = (int) ((maxVaule - (int)Double.parseDouble(data.score)) * perHeight) + dy;
			data.point = new Point(x, y);

		}
		this.trendDatas = datas;
		if(datas.size() == 0){
			translateX = 0;
			selectPosition = 0;
			if(adapter != null)
				adapter.setFirstVisibleItem(0);
		}else{
			isShowInCenter = trendDatas.size() < POINT_COUNT;
			if(isShowInCenter)
				translateX = - width/2 + trendDatas.size()*perWidth/2;
		}

		positionDats = new HashMap<String, Integer>();
		for(int i = 0, length = trendDatas.size(); i < length; i++ ){
			positionDats.put(trendDatas.get(i).date, i);
		}


		postInvalidate();
	}
	private TrendAdapter adapter;
	public void setListView(ListView listView , TrendAdapter adapter){
		this.adapter = adapter;
		mListView = listView;
		listView.setOnTouchListener(this);
		listView.setOnScrollListener(this);
	}
	private void initPaint() {
		diameterLarge = DensityUtil.dip2px(getContext(), 7);
		diameterMiddle = DensityUtil.dip2px(getContext(), 4);
		diameterSmall = DensityUtil.dip2px(getContext(), 3);

		paintLine = new Paint();
		paintLine.setStrokeWidth(DensityUtil.dip2px(getContext(), 1));//设置线宽
		paintLine.setColor(0xff9BF7FF);
		paintLine.setStyle(Style.FILL);
		paintLine.setAntiAlias(true);

		paintSelect = new Paint();
		paintSelect.setColor(0xff65E3E9);
		paintSelect.setAntiAlias(true);
		paintSelect.setStyle(Style.FILL);

		paintScore = new Paint();
		paintScore.setColor(Color.WHITE);
		paintScore.setTextSize(DensityUtil.dip2px(getContext(), 15));
		paintScore.setAntiAlias(true);
		paintScore.setStyle(Style.FILL);
		paintScore.setTextAlign(Paint.Align.CENTER);
		//取文字高度的1.5倍做为高度差
		FontMetrics fm = paintScore.getFontMetrics();
		textDis = (float) (0.6 * Math.ceil(fm.descent - fm.ascent) );

		paintDate = new Paint();
		paintDate.setColor(Color.WHITE);
		paintDate.setTextSize(DensityUtil.dip2px(getContext(), 12));
		paintDate.setAntiAlias(true);
		paintDate.setStyle(Style.FILL);
		paintDate.setTextAlign(Paint.Align.CENTER);

	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, height);
		//super.onMeasure(width, height);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(trendDatas == null)
			return;
		drawPoints(canvas);
		if(selectPosition < trendDatas.size()){
			canvas.drawText(trendDatas.get(selectPosition).score + "", width / 2, (int)(height*0.82), paintScore);
			canvas.drawText("时间●" + trendDatas.get(selectPosition).date , width / 2, (int)(height*0.84 + textDis),  paintDate);
		}

	}
	private void drawPoints(Canvas canvas) {
		canvas.save();
		//canvas.drawColor(0xff19C2D2);
		canvas.translate(translateX, 0);
		for(int i = 0, length = trendDatas.size(); i < length; i ++){
			Point curPoint = trendDatas.get(i).point;
			if(curPoint == null)
				continue;
			if(i + 1 < length){
				Point nextPoint = trendDatas.get(i + 1).point;
				if(nextPoint != null)
					canvas.drawLine(curPoint.x, curPoint.y, nextPoint.x, nextPoint.y,paintLine);
			}

			//#65E3E9
			if(selectPosition == i){
				paintSelect.setColor(0xff65E3E9);
				canvas.drawCircle(curPoint.x, curPoint.y, diameterLarge, paintSelect);
				paintSelect.setColor(Color.WHITE);
				canvas.drawCircle(curPoint.x, curPoint.y, diameterMiddle, paintSelect);
			}else{
				canvas.drawCircle(curPoint.x, curPoint.y, diameterSmall, paintLine);
			}


		}

		canvas.restore();

	}
	private int selectPosition = 0;
 	private float translateX;
 	private float perX;

	public void move(float x){
		if(selectPosition >= trendDatas.size())
			return;
		if(!isShowInCenter){
			float selectX = trendDatas.get(selectPosition).point.x + translateX;
			if(selectX >= width/2 - perWidth && selectX <= width/2 + perWidth){
				if(itemHeight == 0)
					itemHeight = getListViewItemHeight();
				float temp = (float) (translateX + x * (perWidth * 1.0)/itemHeight);
				perX = (float) (x * (perWidth * 1.0)/itemHeight % perWidth);
				if(temp >= 0 && temp <= perWidth *(trendDatas.size() - POINT_COUNT) )
					translateX = temp;
			}
		}
			//set selection
			selectPosition = firstVisibleItem;//mListView.getFirstVisiblePosition();
		postInvalidate();
	}
	public void setPosition(int position){
		if(!isShowInCenter){
			if(position == trendDatas.size() - 1){
				translateX = perWidth *(trendDatas.size() - POINT_COUNT);
			}else if(position == 0){
				translateX = 0;
			}else{
				if(position > 2 && position < trendDatas.size() - 3){
					float temp = perWidth * (position - 3) + perX;
					if(temp >= 0 && temp <= perWidth *(trendDatas.size() - POINT_COUNT) )
						translateX = temp;
				}

		}
		}
		//selectPosition = firstVisibleItem;//mListView.getFirstVisiblePosition();


	postInvalidate();
	}
	private float y;
	private int dirFlag;
	private int itemHeight;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			y = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(canMove){
				dirFlag = y - event.getY() > 0 ? 1 : -1;
				move(y - event.getY());
				y = event.getY();
			}

			break;

		default:
			break;
		}
		return false;
	}
	private int getListViewHeight(){
		return mListView.getHeight();
	}
	private int getListViewItemHeight(){
		ListAdapter mAdapter = mListView.getAdapter();
		if(mAdapter == null )
			return 0;
		if(mAdapter.getCount() == 0)
			return 0;
		 View mView = mAdapter.getView(0, null, mListView);
		 mView.measure(0, 0);
		return mView.getMeasuredHeight();

	}
	private boolean haveAdd;
	private int firstVisibleItem;
	private int visibleItemCount;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(adapter != null && positionDats != null && trendDatas != null && trendDatas.size() != 0){
			String date = adapter.getDate(firstVisibleItem);
			if(!TextUtils.isEmpty(date)){
				try {
					int position = positionDats.get(date);
					L.i(">>>date:" + date + ",positon:" + position);
					setPosition(position);
					selectPosition = positionDats.get(adapter.getDate(firstVisibleItem));
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}


		//setPosition(positionDats.get(adapter.getDate(firstVisibleItem)));
		if(firstVisibleItem + visibleItemCount + this.visibleItemCount == totalItemCount && listener != null && loadStatus == LoadStatus.Normal){
			this.loadStatus = LoadStatus.Loading;
			listener.onLoadMore();

		}
//		L.i(">>>>>count:" + this.visibleItemCount + "," + visibleItemCount);
//		this.firstVisibleItem = visibleItemCount > this.visibleItemCount ? firstVisibleItem + 1 : firstVisibleItem;
//		L.i(">>>>>firstVisibleItem:" + this.visibleItemCount );
//		if(trendDatas != null)
//			this.firstVisibleItem = this.firstVisibleItem > trendDatas.size() - 1 ? (trendDatas.size() - 1) : this.firstVisibleItem;

		if(visibleItemCount != 0 && !haveAdd){
			int listHeight = getListViewHeight();
			int itemHeight = getListViewItemHeight();
			if( listHeight != itemHeight){
				int addCount = visibleItemCount - 1;
				int tempAdd= listHeight%itemHeight == 0 ? 0 : 1;
				L.i(">>>>>listHeight:" + listHeight + ",itemHeight:" + itemHeight);
				int temp = listHeight / itemHeight + tempAdd;
				L.i(">>>>>temp:" + temp);
				addCount = addCount > temp ? addCount : (temp - 1);
				adapter.addCount(addCount);
				haveAdd = !haveAdd;
				this.visibleItemCount = addCount + 1;
				L.i(">>>>>addCount:" + addCount);
			}

		}
//
//
//		L.i(">>>>>>>" + firstVisibleItem);
//		if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
//			setPosition(firstVisibleItem);
//		}


	}
	private boolean canMove;
	private int scrollState;
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && adapter != null){
//			L.i(">>>>>>>set:" + firstVisibleItem);
//			//adapter.setFirstVisibleItem(firstVisibleItem);
//		}
//
//		this.scrollState = scrollState;
//		if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//			canMove = true;
//		}else{
//			canMove = false;
//		}
	}


}
