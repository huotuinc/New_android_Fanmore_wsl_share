package cy.com.morefan;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.service.UserService;
import cy.com.morefan.view.CyButton;

public class UserExchangeActivity extends BaseActivity implements View.OnClickListener {
    public CyButton btnBack;
    //public TextView history;
    public Button btnexchange;
    protected UserService userService;
    protected UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_exchange);
        btnBack= (CyButton) findViewById(R.id.btnBack);
        btnexchange= (Button) findViewById(R.id.btnexchange);
        btnBack.setOnClickListener(this);

        //history= (TextView) findViewById(R.id.history);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
//            case R.id.history:
//                break;
            case R.id.btnexchange:

                userService.GetUserList(userData.loginCode, BusinessStatic.getInstance().accountModel.getAccountUnionId());
                break;
            default:
                break;
        }

    }
}
