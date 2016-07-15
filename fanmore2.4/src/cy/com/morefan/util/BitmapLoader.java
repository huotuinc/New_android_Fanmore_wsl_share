package cy.com.morefan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import cy.com.morefan.view.ImageLoad;


/**
 * 
 * @类名称：BitmapLoader
 * @类描述：加载图片
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月2日 上午9:26:12
 * @修改备注：
 * @version:
 */
public class BitmapLoader
{

    private static BitmapLoader instance;

    public synchronized static BitmapLoader create()
    {
        if (instance == null)
        {
            instance = new BitmapLoader();
        }
        return instance;
    }

    private BitmapLoader()
    {
        
    }

    /**
     * 
     * @方法描述：采用volly加载网络图片
     * @方法名：displayUrl
     * @参数：@param context 上下文环境
     * @参数：@param imageView 图片空间
     * @参数：@param imageUrl url地址
     * @参数：@param initImg 初始化图片
     * @参数：@param errorImg 错误图片
     * @返回：void
     * @exception
     * @since
     */







    public void displayUrlBanner(final Context context, final ImageView imageView, String logoUrl, final int errorImg)
    {
        VolleyUtil.getImageLoader(context).get(logoUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer != null && imageContainer.getBitmap() != null) {
                    imageView.setImageBitmap(imageContainer.getBitmap());
                }else{
                    imageView.setBackgroundResource(errorImg);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                imageView.setBackgroundResource(errorImg);
            }
        }, 0, 0 , ImageView.ScaleType.CENTER_INSIDE);
    }
}
