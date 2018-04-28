package cy.com.morefan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.stat.common.User;
import com.yhao.floatwindow.FloatWindow;

import java.math.BigDecimal;
import java.text.NumberFormat;

import butterknife.OnTextChanged;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.ScoreService;
import cy.com.morefan.util.KeyWordUtil;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.view.OkCancelView;

public class ScoreActivity extends BaseActivity
        implements OkCancelView.OnOkCancelListener, BusinessDataListener
        ,Handler.Callback , TextWatcher {
    EditText etToutiao;
    EditText etMaill;
    TextView tvToutiao;
    TextView tvMall;
    BigDecimal rate;
    OkCancelView okCancelView;
    ScoreService scoreService;
    Handler mHandler = new Handler(this);

    @Override
    public boolean handleMessage(Message msg) {
        dismissProgress();

        switch (msg.what){
            case BusinessDataListener.DONE_SCORE:
                setScore(msg.obj);
                break;
            case BusinessDataListener.ERROR_SCORE:
                ToastUtil.show(this, msg.obj.toString());
                break;
            case BusinessDataListener.DONE_TO_GETUSERLIST:
                getScore();
                break;
            case BusinessDataListener.ERROR_TO_GETUSERLIST:
                ToastUtil.show(this, msg.obj.toString());
                break;
            case BusinessDataListener.DONE_RECHARGE:
                ToastUtil.show("客户~您已经兑换成功，可以去商城消费了!",Gravity.BOTTOM);
                getScore();
                break;
            case BusinessDataListener.ERROR_RECHARGE:
                ToastUtil.show(this, msg.obj.toString());
                break;
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String temp = etToutiao.getText().toString();
        try {
            BigDecimal appScore = new BigDecimal(temp);
            String appMaxScore = tvToutiao.getText().toString();
            BigDecimal appMax = new BigDecimal(appMaxScore);
            if(appScore.compareTo( appMax )>0){
                etToutiao.setText( String.valueOf( appMax.intValue()));
                etToutiao.selectAll();
                //ToastUtil.show("客官~您已超出可选范围哦!",Gravity.BOTTOM);
                appScore = appMax;
            }
            BigDecimal mallScore = appScore.multiply(rate);
            etMaill.setText( String.valueOf( mallScore.intValue() ));
        }catch (Exception ex){
            etMaill.setText("0");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    protected void setScore(Object obj){
        if(obj==null)return;
        Bundle bd = (Bundle)obj;
        String appScore = bd.getString("appScore","0");

        UserData.getUserData().score =appScore;
        MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_REFRESH_USEDATA);

        String mallScore = bd.getString("mallScore", "0");
        //NumberFormat numberFormat = NumberFormat.getNumberInstance();
        rate = new BigDecimal ( bd.getString("scoreRate","1"));

        tvToutiao.setText(  appScore );

        BigDecimal t = new BigDecimal(mallScore);

        tvMall.setText( String.valueOf( t.intValue() ) );

        etToutiao.setText( appScore );
        etToutiao.selectAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        etToutiao = (EditText) findViewById(R.id.score_toutiao_et);
        etMaill = (EditText) findViewById(R.id.score_mall_et);
        tvToutiao = (TextView) findViewById(R.id.score_toutiao);
        tvMall = (TextView) findViewById(R.id.score_mall);

        okCancelView = new OkCancelView(this, this);

        KeyWordUtil.openKeybord(etToutiao, this);

        scoreService = new ScoreService(this);

        etToutiao.addTextChangedListener(this);
        etToutiao.selectAll();

        getScore();
    }

    protected void getScore(){
        String mallUserId = SPUtil.getStringToSpByName( this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId);
        if (TextUtils.isEmpty(mallUserId )) {
            showProgress();
            userService.GetUserList(this , UserData.getUserData().loginCode,
                    SPUtil.getStringToSpByName(this , Constant.SP_NAME_NORMAL, Constant.SP_NAME_UnionId));
        }else {
            showProgress();
            scoreService.getScoreInfo( this , UserData.getUserData().loginCode , Integer.parseInt( mallUserId ));
        }
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.score_ok:
                recharge();
                break;
            case R.id.score_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void recharge(){
        if(TextUtils.isEmpty( etToutiao.getText() )){
            ToastUtil.show("客官~请输入兑换积分哦", Gravity.BOTTOM);
            KeyWordUtil.openKeybord(etToutiao,this);
            return;
        }
        BigDecimal temp = new BigDecimal(etToutiao.getText().toString());
        if(temp.compareTo(BigDecimal.ZERO)<=0){
            ToastUtil.show("客官~请输入正确的积分哦",Gravity.BOTTOM);
            KeyWordUtil.openKeybord(etToutiao,this);
            return;
        }

        KeyWordUtil.closeKeybord(this);

        okCancelView.show();
    }

    @Override
    public void onOkCancel(OkCancelView.OkCancel type) {
        if(type== OkCancelView.OkCancel.ok){
            int mallUserId = Integer.valueOf( SPUtil.getStringToSpByName( this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId));
            String appScore = etToutiao.getText().toString();
            showProgress();
            scoreService.recharge(this, UserData.getUserData().loginCode , appScore , mallUserId );
        }
    }

    @Override
    protected void onBack() {
        super.onBack();
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);

        mHandler.obtainMessage(type , extra).sendToTarget();

    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);

        mHandler.obtainMessage(type , des).sendToTarget();
    }

    @Override
    public void onDataFail(int type, String des, Bundle extra) {
        super.onDataFail(type, des, extra);

        mHandler.obtainMessage(type, des).sendToTarget();
    }
}
