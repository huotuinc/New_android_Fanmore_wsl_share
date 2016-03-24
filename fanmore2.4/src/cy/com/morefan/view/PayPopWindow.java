package cy.com.morefan.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;



import cy.com.morefan.MainApplication;
import cy.com.morefan.PayModel;
import cy.com.morefan.R;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.PoponDismissListener;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.WindowProgress;
import cy.com.morefan.util.WindowUtils;

/**
 * 支付弹出框
 */
public
class PayPopWindow extends PopupWindow {

    private
    Button wxPayBtn;
    private Button alipayBtn;
    private
    Button cancelBtn;
    private View payView;
    private Activity aty;
    private Handler mHandler;
    private MainApplication application;
    private PayModel payModel;
    private Context context;
    public WindowProgress progress;


    public PayPopWindow(final Activity aty, final Context context, final Handler mHandler, final MainApplication application, final PayModel payModel) {
        super ( );
        this.aty = aty;
        this.mHandler = mHandler;
        this.application = application;
        this.payModel = payModel;
        this.context = context;
        progress = new WindowProgress ( aty );
        LayoutInflater inflater = (LayoutInflater) aty.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );

        payView = inflater.inflate ( R.layout.pop_pay_ui, null );
        wxPayBtn = (Button) payView.findViewById ( R.id.wxPayBtn );
        alipayBtn = (Button) payView.findViewById ( R.id.alipayBtn );
        cancelBtn = (Button) payView.findViewById ( R.id.cancelBtn );

        wxPayBtn.setOnClickListener (
                new View.OnClickListener ( ) {
                    @Override
                    public
                    void onClick ( View v ) {
                        if(!application.scanWx ())
                        {
                            //缺少支付信息
                           progress.dismissProgress();
                            ToastUtil.show(aty,"缺少支付信息");
                        }
                        else
                        {
                            progress.showProgress();

                            payModel.setAttach ( payModel.getCustomId ( ) + "_0" );
                            //添加微信回调路径
                            payModel.setNotifyurl ( BusinessStatic.getInstance().URL_WEBSITE + application.readWeixinNotify ( ) );
                            PayFunc payFunc = new PayFunc ( context, payModel, application, mHandler, aty, progress );
                            payFunc.wxPay ( );
                            dismissView ( );
                        }
                                          }
                                      } );
        alipayBtn.setOnClickListener ( new View.OnClickListener ( ) {
                                           @Override
                                           public
                                           void onClick ( View v ) {
                                               Message msg = new Message();
                                               msg.what = Constant.PAY_NET;
                                               payModel.setPaymentType ( "1" );
                                               msg.obj = payModel;
                                               mHandler.sendMessage ( msg );
                                               dismissView ( );
                                           }
                                       } );
        cancelBtn.setOnClickListener ( new View.OnClickListener ( ) {
                                           @Override
                                           public
                                           void onClick ( View v ) {
                                               dismissView ( );
                                           }
                                       } );

        //设置SelectPicPopupWindow的View
        this.setContentView ( payView );
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth ( LinearLayout.LayoutParams.MATCH_PARENT );
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight ( LinearLayout.LayoutParams.WRAP_CONTENT );
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //WindowUtils.backgroundAlpha(aty, 0.4f);

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框

        payView.setOnTouchListener (
                new View.OnTouchListener ( ) {
                    @Override
                    public
                    boolean onTouch ( View v, MotionEvent event ) {
                        int height = payView.findViewById ( R.id.popLayout ).getTop ( );
                        int y      = ( int ) event.getY ( );
                        if ( event.getAction ( ) == MotionEvent.ACTION_UP ) {
                            if ( y < height ) {
                                dismissView ();
                            }
                        }
                        return true;
                    }
                }
                                   );
    }

    public void dismissView()
    {
        setOnDismissListener ( new PoponDismissListener( aty ) );
        dismiss ();

    }
}
