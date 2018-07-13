package com.yhao.floatwindow;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fixedfloatwindow.R;

/**
 * @author  jinxiangdong
 * @date 2016/4/29.
 */
public class TipAlertDialog implements View.OnClickListener{
    AlertDialog dialog;
    Context context;
    TextView titleText;
    TextView messageText;
    Button btn_left;
    Button btn_right;


    public TipAlertDialog(Context context ){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_tip_dialog , null);
        dialog.setView(view, 0, 0, 0, 0);
        titleText = view.findViewById(R.id.titletext);
        messageText =  view.findViewById(R.id.messagetext);
        btn_left =  view.findViewById(R.id.btn_left);
        btn_right =  view.findViewById(R.id.btn_right);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);

    }

    public void dismiss(){
        dialog.dismiss();
    }

    public void show(String title , String message , String url){
        show( title , message , url ,  true ,true );
    }

    public void show(String title , String message , View.OnClickListener cancelListener , View.OnClickListener confirmListener){
        show(title,message, "",  true , true , cancelListener,confirmListener);
    }

    public void show(String title , String message , String url ,
                     boolean isShowLeft , boolean isShowRight , final View.OnClickListener cancelListener , View.OnClickListener confirmListener  ){
        titleText.setText(title);
        //titleText.setTextColor( titleColor );
        messageText.setText(message);
        btn_right.setTag( url);
        btn_left.setVisibility(isShowLeft? View.VISIBLE: View.GONE);
        btn_right.setVisibility( isShowRight? View.VISIBLE:View.GONE );

        if(cancelListener!=null){
            btn_left.setOnClickListener(cancelListener);
        }
        if(confirmListener!=null){
            btn_right.setOnClickListener(confirmListener);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                //return false;
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    btn_left.performClick();
                }
                return true;
            }
        });


        dialog.show();
    }

    public void show(String title , String message , String url  , boolean isShowLeft , boolean isShowRight){

        show(title,message,url,isShowLeft,isShowRight, null,null);
    }

    @Override
    public void onClick(View v) {
        if( v.getId()== R.id.btn_left){
            dialog.dismiss();
        }else if(v.getId()== R.id.btn_right){
            dialog.dismiss();

            if( null== btn_right.getTag() ) return;
            String linkUrl = btn_right.getTag().toString();
            //if(TextUtils.isEmpty(linkUrl)) return;

        }
    }
}
