package cy.com.morefan;

import android.os.Bundle;
import android.view.View;

import cy.com.morefan.frag.SelectionFrag;

public class SelectionActivity extends BaseActivity {
    SelectionFrag selectionFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        selectionFrag=SelectionFrag.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.selection_container , selectionFrag)
                .commit();

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.selection_back:
                finish();
                break;
            default:
                break;
        }
    }
}
