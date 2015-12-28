package cy.com.morefan.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



import java.io.Serializable;

import cy.com.morefan.MainApplication;

public class ActivityUtils
{

    public MainApplication application;

    private static
    class Holder {
        private static final ActivityUtils instance = new ActivityUtils ( );
    }
    
    private ActivityUtils() {
        
    }

    public static final
    ActivityUtils getInstance ( ) {
        return Holder.instance;
    }
    
    public
    void showActivity ( Activity aty, Class clazz ) {
        Intent i = new Intent( aty, clazz );
        aty.startActivity ( i );
    }
    
    public void showActivity(Activity aty , Class clazz , String key , Serializable serialize ){
        Intent i = new Intent(aty, clazz);
        i.putExtra(key, serialize);
        aty.startActivity(i);
    }

    public void skipActivity(Activity aty, Class clazz)
    {
        Intent i = new Intent(aty, clazz);
        aty.startActivity(i);
        aty.finish();
    }    
    
    public void skipActivity( Activity aty , Class clazz , String key , Serializable serialize ){
        Intent i = new Intent(aty , clazz);
        i.putExtra(key, serialize);
        aty.startActivity(i);
        aty.finish();
    }

    public void showActivity(Activity aty, Intent i)
    {
        aty.startActivity(i);
    }

    public void showActivity(Activity aty, String action)
    {
        Intent i = new Intent(action);
        aty.startActivity(i);
    }
    
    public void showActivityForResult(Activity aty, int requestCode , Class clazz)
    {
        Intent i = new Intent(aty,clazz);
        aty.startActivityForResult(i, requestCode);
    }
    
    public void showActivityForResult(Activity aty  ,int requestCode, Class clazz , Bundle bundle){
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        aty.startActivityForResult(i, requestCode);
    }

    public void showActivityForResult(Activity aty  ,int requestCode, Intent intent ){
        aty.startActivityForResult(intent , requestCode);
    }
    
    public void showActivity(Activity aty, Class clazz, Bundle bundle)
    {
        Intent i = new Intent(aty, clazz);
        i.putExtras(bundle);
        aty.startActivity ( i );
    }

    public void skipActivity(Activity aty, Class clazz, Bundle bundle)
    {
        Intent i = new Intent(aty, clazz);
        i.putExtras(bundle);
        aty.startActivity(i);
        aty.finish ();
    }
}
