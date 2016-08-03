package cy.com.morefan.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import cy.com.morefan.FMPrepareBuy;
import cy.com.morefan.MainApplication;
import cy.com.morefan.PayModel;
import cy.com.morefan.WXPayAsyncTask;
import cy.com.morefan.util.WindowProgress;


/**
 * 支付总览
 */
public
class PayFunc {

    private PayModel payModel;
    private
    MainApplication application;
    private
    FMPrepareBuy prepareBuy;
    private
    Handler handler;
    private
    Context context;
    private
    Activity aty;
    private WindowProgress progress;

    public PayFunc(Context context, PayModel payModel, MainApplication application, Handler handler,
                   Activity aty, WindowProgress progress) {
        this.payModel = payModel;
        this.application = application;
        this.handler = handler;
        this.context = context;
        this.aty = aty;
        this.progress = progress;
    }

    public
    void wxPay ( ) {
        //根据订单号获取支付信息
        String body        = payModel.getDetail ( );
        String price       = String.valueOf(payModel.getAmount());
        int    productType = 0;
        long   productId   = 0;
        prepareBuy = new FMPrepareBuy ();
        progress.dismissProgress();
        //调用微信支付模块
        new WXPayAsyncTask(handler, body, price, productType, productId, context, prepareBuy, application, payModel.getNotifyurl (), payModel.getAttach (), payModel.getTradeNo ()).execute();
    }

    public void aliPay()
    {

    }
}

