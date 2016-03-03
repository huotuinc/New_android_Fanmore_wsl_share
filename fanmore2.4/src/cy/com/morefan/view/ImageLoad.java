package cy.com.morefan.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

//import cindy.android.test.synclistview.ImageLoader;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.VolleyUtil;

/**
 * Created by Administrator on 2016/1/15.
 */
public class ImageLoad {
    public static void loadLogo( String logoUrl,final ImageView iv , final Context context ){
        if(TextUtils.isEmpty(logoUrl)) return;
        VolleyUtil.getImageLoader(context)
                .get(logoUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (imageContainer != null && imageContainer.getBitmap() != null) {
                            iv.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //ToastUtil.show(context, "加载图片失败");
                    }
                });
    }
}
