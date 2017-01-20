package cy.com.morefan.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import cy.com.morefan.R;

/**
 * Created by Administrator on 2017/1/18.
 */

public class TipDialog {
    /***
     *
     * @param context
     * @param title
     * @param content
     * @param buttonText
     * @param yes
     * @param checkedChangeListener
     */
    public static void show(Context context , String title , String content , String buttonText , DialogInterface.OnClickListener yes , CompoundButton.OnCheckedChangeListener checkedChangeListener ){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.tipdialog,null);
        CheckBox chk =(CheckBox) view.findViewById(R.id.chkRemember);
        chk.setOnCheckedChangeListener( checkedChangeListener);
        AlertDialog alertDialog = new AlertDialog.Builder(context).setPositiveButton(buttonText, yes).create();
        alertDialog.setView(view);
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText(title);
        TextView tvContent = (TextView)view.findViewById(R.id.message) ;
        tvContent.setText(content);
        //alertDialog.setTitle(title);
        //alertDialog.setMessage(content);
        alertDialog.show();
    }
}
