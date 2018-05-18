package cy.com.morefan;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import cy.com.morefan.view.loopview.LoopView;
import cy.com.morefan.view.loopview.OnItemSelectedListener;

import java.lang.invoke.MethodHandle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import cy.com.morefan.adapter.FavoriteAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.FavoriteData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.ScoreService;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.RecycleItemDivider;

public class FavoriteActivity extends BaseActivity
        implements View.OnClickListener , BaseQuickAdapter.OnItemChildClickListener,
        BusinessDataListener ,Handler.Callback {
    RecyclerView recyclerView;
    ImageView ivBack;
    LinearLayout favorite_date_top;
    TextView favorite_date_value;
    ImageView favorite_date_corner;
    LinearLayout favorite_date_select;
    LinearLayout favorite_date_container;
    boolean isShow;
    LoopView loopView;
    FavoriteAdapter favoriteAdapter;
    ArrayList<String> dateList;
    ScoreService scoreService;
    EmptyView layEmpty;
    List<FavoriteData> favoriteData;
    private Handler mHandler = new Handler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ivBack = (ImageView) findViewById(R.id.favorite_back);
        ivBack.setOnClickListener(this);

        favorite_date_container = (LinearLayout)findViewById(R.id.favorite_date_container);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        favorite_date_corner = (ImageView) findViewById(R.id.favorite_date_corner);
        favorite_date_value = (TextView) findViewById(R.id.favorite_date_value);
        favorite_date_select =(LinearLayout) findViewById(R.id.favorite_date_select);
        favorite_date_top = (LinearLayout) findViewById(R.id.favorite_date_top);

        favorite_date_top.setOnClickListener(this);

        favorite_date_select.setVisibility(isShow?View.VISIBLE:View.GONE);
        favorite_date_corner.setImageResource(isShow?R.drawable.corner2:R.drawable.corner);

        layEmpty =(EmptyView) findViewById(R.id.layEmpty);

        loopView= (LoopView)findViewById(R.id.loopView);

        Calendar calendar=Calendar.getInstance();
        Date currentDate= calendar.getTime();
        dateList = new ArrayList<>();

        favorite_date_container.setVisibility(View.GONE);

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        for(int i=-;i<=0;i++){
//            calendar.setTime(currentDate);
//            calendar.add(Calendar.DAY_OF_MONTH , i);
//            String fdate = simpleDateFormat.format(calendar.getTime());
//            dateList.add( fdate );
//        }


        //loopView.setItems(dateList);
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                favorite_date_value.setText( dateList.get(index) );
                scoreService.getFavoriteList(FavoriteActivity.this , UserData.getUserData().loginCode , dateList.get(index));
            }
        });

//        int curentP = dateList.size()/2;
//        loopView.setCurrentPosition( dateList.size()/2 );
//        favorite_date_value.setText( dateList.get(curentP) );


        favoriteData = new ArrayList<>();
//        for(int i = 0;i<20;i++){
//            data.add(i);
//        }
        favoriteAdapter = new FavoriteAdapter(favoriteData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(favoriteAdapter);
        recyclerView.addItemDecoration(new RecycleItemDivider(this,  LinearLayoutManager.VERTICAL , 10 , R.color.favorite_bg));
        favoriteAdapter.setOnItemChildClickListener(this);



        scoreService = new ScoreService(this  );
        scoreService.getFavoriteDate(this , UserData.getUserData().loginCode );

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.setSwipeBackEnable(true);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.favorite_date_top){
            isShow=!isShow;
            favorite_date_select.setVisibility(isShow?View.VISIBLE:View.GONE);
            favorite_date_corner.setImageResource(isShow?R.drawable.corner2:R.drawable.corner);

        }else if(v.getId()==R.id.favorite_back){
            this.finish();
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if(view.getId()==R.id.favorite_item_delete){
            //ToastUtil.show(this , "ssss");
            FavoriteData bean = favoriteData.get(position);
            scoreService.collection(this, UserData.getUserData().loginCode , bean.TaskId);
        }else if(view.getId()==R.id.favorite_item_container){
            Intent intentDetail = new Intent(this ,TaskDetailActivity.class);
            FavoriteData item = favoriteData.get(position );
            TaskData taskData = new TaskData();
            taskData.taskId = item.TaskId;
            taskData.id=item.TaskId;
            taskData.taskName = item.TaskName;
            intentDetail.putExtra("taskData", taskData);

            startActivity(intentDetail);
        }
    }


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what){
            case BusinessDataListener.DONE_GET_FAVORITE_DATE:
                initDate(msg.obj);
                break;
            case BusinessDataListener.DONE_GET_FAVORITE_LIST:
                initList(msg.obj);
                break;
            case BusinessDataListener.ERROR_GET_FAVORITE_DATE:
                toast(msg.obj.toString());
                break;
            case BusinessDataListener.ERROR_GET_FAVORITE_LIST:
                toast(msg.obj.toString());
                break;
            case BusinessDataListener.DONE_COLLECTION:
                delete(msg.obj);
                break;
            case BusinessDataListener.ERROR_COLLECTION:
                toast(msg.obj.toString());
                break;
            default:
                return false;
        }

        return false;

    }

    protected void delete(Object obj  ){
        if(obj==null )return;
        Bundle bd = (Bundle) obj;
        if(bd==null)return;
        if(!bd.containsKey("taskId"))return;

        int taskid=bd.getInt("taskId",0);

        for(FavoriteData item : favoriteData){
            if(item.TaskId==taskid){
                favoriteData.remove(item);
                favoriteAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void initDate(Object obj ){
        if(obj ==null ){
            favorite_date_container.setVisibility(View.GONE);
            layEmpty.setVisibility(View.VISIBLE);
            return;
        }
        Bundle  bd = (Bundle)obj;
        if(bd==null){
            favorite_date_container.setVisibility(View.GONE);
            layEmpty.setVisibility(View.VISIBLE);
            return;
        }
        if(!bd.containsKey("date")){
            favorite_date_container.setVisibility(View.GONE);
            layEmpty.setVisibility(View.VISIBLE);
            return;
        }

        String[] dates = bd.getStringArray("date");
        if(dates==null|| dates.length<1){
            favorite_date_container.setVisibility(View.GONE);
            layEmpty.setVisibility(View.VISIBLE);
            return;
        }
        favorite_date_container.setVisibility(View.VISIBLE);
        layEmpty.setVisibility(View.GONE);

        dateList.clear();
        for(int i=0;i<dates.length;i++){
            dateList.add(dates[i]);
        }

        loopView.setItems(dateList);
        int curentP = dateList.size()-1;
        loopView.setCurrentPosition( curentP );
        favorite_date_value.setText( dateList.get(curentP) );
        String month = dateList.get(curentP);

        scoreService.getFavoriteList(this, UserData.getUserData().loginCode , month );
    }
    private void initList(Object obj ){
        if(obj==null) {
            recyclerView.setVisibility(View.GONE);
            layEmpty.setVisibility(View.VISIBLE);
        }

        recyclerView.setVisibility(View.VISIBLE);
        layEmpty.setVisibility(View.GONE);
        FavoriteData[] data = (FavoriteData[])obj;
        favoriteData.clear();
        for(int i=0;i<data.length;i++) {
            favoriteData.add(data[i]);
        }

        recyclerView.setAdapter(favoriteAdapter);
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);

        mHandler.obtainMessage(type, des).sendToTarget();
    }

    @Override
    public void onDataFail(int type, String des, Bundle extra) {
        super.onDataFail(type, des, extra);

        mHandler.obtainMessage(type,des).sendToTarget();
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);


        if(type==BusinessDataListener.DONE_GET_FAVORITE_DATE){
            mHandler.obtainMessage(type, extra).sendToTarget();

        }else if( type== BusinessDataListener.DONE_GET_FAVORITE_LIST){
            mHandler.obtainMessage(type , datas).sendToTarget();
        }else if(type==BusinessDataListener.DONE_COLLECTION){
            mHandler.obtainMessage(type , extra ).sendToTarget();
        }

    }
}
