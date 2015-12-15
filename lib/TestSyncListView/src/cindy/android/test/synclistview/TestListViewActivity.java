package cindy.android.test.synclistview;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class TestListViewActivity extends AbstructCommonActivity 
	implements AdapterView.OnItemClickListener{
	
	NetImageListView viewBookList;
	
	BookItemAdapter adapter;
	
	//ViewGroup listFolder;
	
	LoadStateView loadStateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		viewBookList = (NetImageListView) findViewById(R.id.viewBookList);
		adapter = new BookItemAdapter(this,viewBookList.getImageLoader());
		loadStateView = (LoadStateView) findViewById(R.id.downloadStatusBox);
		
		loadStateView.setOnReloadClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				reload();
			}
		});
		//listFolder = (ViewGroup) getLayoutInflater().inflate(R.layout.load_more, null);
		//viewBookList.addFooterView(listFolder);
		viewBookList.setAdapter(adapter);
		viewBookList.setOnItemClickListener(this);
		reload();
	}
	
	private void reload(){
		adapter.clean();
		loadStateView.startLoad();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDate();
				sendMessage(REFRESH_LIST);
				//sendMessageDely(LOAD_IMAGE, 500);
			}
		}).start();
	}
	
	public void loadDate(){
		for(int i=0;i<10;i++){
			adapter.addBook("女红"+i, 
			"http://www.pfwx.com/bookinfo/11/11000.html", 
			"http://www.pfwx.com/files/article/image/11/11000/11000s.jpg");
			
			adapter.addBook("暂无灰"+i, 
			"http://www.pfwx.com/bookinfo/9/9760.html", 
			"http://www.pfwx.com/files/article/image/9/9760/9760s.jpg");
			
			adapter.addBook("山"+i, 
			"http://www.pfwx.com/bookinfo/13/13939.html", 
			"http://b.hiphotos.baidu.com/image/w%3D2048/sign=42a1e7c5a41ea8d38a227304a332314e/1ad5ad6eddc451daf0905c95b4fd5266d016326d.jpg");
			
			adapter.addBook("暂无绿"+i, 
			"http://www.pfwx.com/bookinfo/3/3237.html", 
			"http://www.pfwx.com/files/article/image/3/3237/3237s.jpg");
			
			adapter.addBook("水"+i, 
			"http://www.pfwx.com/bookinfo/11/11381.html", 
			"http://f.hiphotos.baidu.com/image/w%3D2048/sign=141d1958612762d0803ea3bf94d409fa/d62a6059252dd42a95c5a7d0013b5bb5c8eab8f9.jpg");		
		}		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
	private static final int REFRESH_LIST = 0x10001;
	private static final int SHOW_LOAD_STATE_VIEW = 0x10003;
	private static final int HIDE_LOAD_STATE_VIEW = 0x10004;

	@Override
	protected void handleOtherMessage(int flag) {
		switch (flag) {
		case REFRESH_LIST:
			adapter.notifyDataSetChanged();
			loadStateView.stopLoad();
			if(adapter.getCount() == 0){
				loadStateView.showEmpty();
			}
			break;
		case SHOW_LOAD_STATE_VIEW:
			loadStateView.startLoad();
			break;
		case HIDE_LOAD_STATE_VIEW:
			loadStateView.stopLoad();
			break;

		default:
			break;
		}
	}

}
