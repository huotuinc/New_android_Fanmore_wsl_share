package cy.com.morefan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class VolleyUtil
{
    private static RequestQueue requestQueue = null;

    private static ImageLoader imageLoader = null;

    public static void init(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(requestQueue, LruImageCache.instance() );
    }
    public static void cancelAllRequest(){
        getRequestQueue().cancelAll("MT_REQUEST");
    }
    public static RequestQueue getRequestQueue() {
        if (requestQueue != null) {
            return requestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }
    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        requestQueue.add(request);
    }
    public static void cancelAll(Object tag) {
        requestQueue.cancelAll(tag);
    }
    public static ImageLoader getImageLoader(Context context) {
        if (null == imageLoader) 
        {
            init(context);
        } 
        return imageLoader;
    }
    
    
    public static void loadImage(final String url ){
        ImageRequest requenst = new ImageRequest(url,
        new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                //imageView.setImageBitmap(response);  
                String key = "#W0#H0"+url;
                LruImageCache.instance().putBitmap( key , response);
            }  
        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //imageView.setImageResource(R.drawable.default_image);  
                //String key ="#W0#H0"+url;
                //LruImageCache.instance().putBitmap(key, BitmapFactory.decodeResource(get , id) R.drawable.ic_launcher);
            }  
        });          
        
        getRequestQueue().add(requenst);        
        
    }
    
}
