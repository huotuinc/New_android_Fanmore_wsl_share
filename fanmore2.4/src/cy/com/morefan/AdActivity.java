package cy.com.morefan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.view.ImageLoad;

public class AdActivity extends BaseActivity {

    @Bind(R.id.adimg)
    ImageView adimg;
    @Bind(R.id.skipText)
    TextView skipText;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        ButterKnife.bind(this);
        ImageLoad.loadLogo(BusinessStatic.getInstance().ADIMG,adimg,this);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
              if(!TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME)) &&
                        !TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD))) {

                    ActivityUtils.getInstance().skipActivity(AdActivity.this, HomeActivity.class);
                } else {
                    ActivityUtils.getInstance().skipActivity(AdActivity.this, MoblieLoginActivity.class);
                }
            }
        });
        adimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.removeCallbacks(runnable);

                ActivityUtils.getInstance().skipActivity(AdActivity.this,NewWebActivity.class);
            }
        });
        runnable=
                new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME)) &&
                        !TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD))) {

                    ActivityUtils.getInstance().showActivity(AdActivity.this, HomeActivity.class);
                }else {
                    ActivityUtils.getInstance().skipActivity(AdActivity.this, MoblieLoginActivity.class);
                }
            }
        };

        handler.postDelayed( runnable,10000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
