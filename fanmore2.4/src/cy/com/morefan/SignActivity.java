package cy.com.morefan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import cy.com.morefan.util.KeyWordUtil;

public class SignActivity extends BaseActivity {
    TextView txttitle;
    EditText etContent;
    TextView txtOk;
    ImageView ivback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        txttitle=(TextView)findViewById(R.id.txttitle);
        etContent=(EditText)findViewById(R.id.sign_text);
        txtOk = (TextView)findViewById(R.id.sign_btn);
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("content" , etContent.getText().toString());
                SignActivity.this.setResult(RESULT_OK, intent);
                SignActivity.this.finish();
            }
        });
        ivback = (ImageView)findViewById(R.id.btnback);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignActivity.this.finish();
            }
        });

        if( getIntent().hasExtra("title")){
            txttitle.setText( getIntent().getStringExtra("title") );
        }
        if(getIntent().hasExtra("content")){
            etContent.setText( getIntent().getStringExtra("content") );
        }
        if(getIntent().hasExtra("height")){
            etContent.setMinHeight( getIntent().getIntExtra("height",100) );
            ViewGroup.LayoutParams layoutParams = etContent.getLayoutParams();
            layoutParams.height = getIntent().getIntExtra("height",100);
            etContent.setLayoutParams(layoutParams);
        }
        if(getIntent().hasExtra("sigleLine")){
            etContent.setSingleLine(getIntent().getBooleanExtra("sigleLine",false));
        }

        etContent.setSelection(etContent.getText().length());
        etContent.requestFocus();
        KeyWordUtil.openKeybord(etContent ,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyWordUtil.closeKeybord(this);
    }
}
