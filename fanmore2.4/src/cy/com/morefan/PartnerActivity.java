package cy.com.morefan;

import android.os.Bundle;
import android.view.View;

import cy.com.morefan.frag.PrenticeFrag;

public class PartnerActivity extends BaseActivity {
    PrenticeFrag prenticeFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);


        prenticeFrag = PrenticeFrag.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.partner_container , prenticeFrag)
                .commit();
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.partner_back:
                this.finish();
                break;
        }
    }
}
