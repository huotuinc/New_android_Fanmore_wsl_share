package cy.com.morefan.view;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cy.com.morefan.R;
import cy.com.morefan.bean.ShareModel;

/**
 * 分享弹出框
 */
public class SharePopupWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private PlatformActionListener platformActionListener;
    private Platform.ShareParams shareParams;

    public SharePopupWindow(Context cx) {
        this.context = cx;
    }

    public void setPlatformActionListener ( PlatformActionListener platformActionListener ) {
        this.platformActionListener = platformActionListener;
    }

    public void showShareWindow ( ) {
        View view = LayoutInflater.from ( context ).inflate (  R.layout.share_layout, null  );
        Button btn_cancel = (Button) view.findViewById ( R.id.btn_cancel );
        LinearLayout sharewechat = (LinearLayout) view.findViewById(R.id.sharewechat);
        LinearLayout sharewechatmoments = (LinearLayout) view.findViewById(R.id.sharewechatmoments);
        LinearLayout shareqzone = (LinearLayout) view.findViewById(R.id.shareqzone);
        sharewechat.setOnClickListener(this);
        sharewechatmoments.setOnClickListener(this);
        shareqzone.setOnClickListener(this);
        // 取消按钮
        btn_cancel.setOnClickListener (
                new View.OnClickListener ( ) {
                    public void onClick ( View v ) {
                        // 销毁弹出框
                        dismiss ( );
                    }});
        //TextView shareTitle = ( TextView ) view.findViewById ( R.id.share_title );

        // 设置SelectPicPopupWindow的View
        this.setContentView ( view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth( LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight( LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimationPop);
        //WindowUtils.backgroundAlpha ( aty, 0.4f );
        // 实例化一个ColorDrawable颜色为半透明
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        //this.setBackgroundDrawable(dw);
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.share_window_bg));
    }

//    private void initShareItems(LinearLayout shareItems) {
//        List<ShareItem> items = new ArrayList<ShareItem>();
//        //微信好友
//        ShareItem wechat = new ShareItem(1, "微信好友", R.drawable.logo_wechat);
//        items.add(wechat);
//        //微信朋友圈
//        ShareItem wechatmoments = new ShareItem(2, "微信朋友圈", R.drawable.logo_wechatmoments);
//        items.add(wechatmoments);
//        //QQ空间分享
//        ShareItem Qzone = new ShareItem(3, "QQ空间", R.drawable.logo_qzone);
//        items.add(Qzone);
//        //sina微博
//        ShareItem sinaweibo = new ShareItem(4, "新浪微博", R.drawable.logo_sinaweibo);
//        items.add(sinaweibo);
//
//        int itemWidth = Constants.SCREEN_WIDTH/4*80/100; //DensityUtils.dip2px( context , 58 );
//        int itemHeight = itemWidth; //DensityUtils.dip2px(context , 58);
//        int itemspace = (Constants.SCREEN_WIDTH - itemWidth * 4 ) /8;
//        //构建ui
//        for(ShareItem item:items){
//            View view = LayoutInflater.from ( context ).inflate ( R.layout.share_item, null);
//            view.setOnClickListener(this);
//            view.setId(item.getId());
//            ImageView img = (ImageView) view.findViewById(R.id.share_icon);
//            img.setImageResource(item.getShareIcon());
//            RelativeLayout.LayoutParams params =(RelativeLayout.LayoutParams) img.getLayoutParams();
//            params.rightMargin = itemspace;
//            params.leftMargin = itemspace;
//            params.width = itemWidth;
//            params.height = itemHeight;
//            img.setLayoutParams(params);
//
//            TextView txt = (TextView) view.findViewById(R.id.share_title);
//            txt.setText(item.getShareName());
//            shareItems.addView(view);
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sharewechat:
                //微信好友
                Platform plat1 = null;
                plat1 = ShareSDK.getPlatform ( context, Wechat.NAME );
                if (platformActionListener != null) {
                    plat1.setPlatformActionListener ( platformActionListener );
                }

                plat1.share(shareParams);
                break;
            case R.id.sharewechatmoments:
                Platform plat = null;
                plat = ShareSDK.getPlatform ( context, WechatMoments.NAME );
                if (platformActionListener != null) {
                    plat.setPlatformActionListener ( platformActionListener );
                }

                plat.share(shareParams);
                break;
            case R.id.shareqzone:
                //qq控件分享
                qzone ( );
                break;
//            case R.id.sharesinaweibo:
//                //sina
//                sinaWeibo();
//                break;
            default:
                break;
        }
        this.dismiss();
    }

//    /**
//     * 分享
//     *
//     * @param position
//     */
//    public void share(int position) {
//
//       if(3 == position){
//            //qq控件分享
//            qzone ( );
//        }
//       else if(4 == position)
//       {
//           //sina
//           sinaWeibo();
//       }
//       else if(2 == position)
//       {
//           //微信朋友圈
//            Platform plat = null;
//            plat = ShareSDK.getPlatform ( context, WechatMoments.NAME );
//            if (platformActionListener != null) {
//                plat.setPlatformActionListener ( platformActionListener );
//            }
//
//            plat.share(shareParams);
//        }
//        else if(1 == position)
//       {
//           //微信好友
//           Platform plat = null;
//           plat = ShareSDK.getPlatform ( context, Wechat.NAME );
//           if (platformActionListener != null) {
//               plat.setPlatformActionListener ( platformActionListener );
//           }
//
//           plat.share(shareParams);
//       }
//    }

    /**
     * 初始化分享参数
     *
     * @param shareModel
     */
    public void initShareParams(ShareModel shareModel) {
        if (shareModel != null) {
            Platform.ShareParams sp = new Platform.ShareParams ();
            sp.setShareType(Platform.SHARE_WEBPAGE);
            //sp.setTitle(shareModel.getText());
            sp.setText(shareModel.getText());
            sp.setTitle(shareModel.getTitle());
            sp.setUrl(shareModel.getUrl());
            sp.setImageUrl(shareModel.getImageUrl());
            if( shareModel.getImageData() !=null) {
                sp.setImageData(shareModel.getImageData());
            }
            shareParams = sp;
        }
    }

    /**
     * 获取平台
     *
     * @param position
     * @return
     */
    private String getPlatform(int position) {
        String platform = "";
        switch (position) {
            case 0:
                platform = Wechat.NAME;
                break;
            case 1:
                platform = WechatMoments.NAME;
                break;
            case 2:
                platform = QZone.NAME;
                break;
            default:
                break;
        }
        return platform;
    }

    /**
     * 分享到QQ空间
     */
    public void qzone() {

        Platform.ShareParams sp = new Platform.ShareParams ();
        sp.setTitle ( shareParams.getTitle ( ) );
        sp.setTitleUrl ( shareParams.getUrl ( ) ); // 标题的超链接
        sp.setText ( shareParams.getText ( ) );
        sp.setImageUrl ( shareParams.getImageUrl ( ) );
        Platform qzone = ShareSDK.getPlatform(context, QZone.NAME);
        qzone.setPlatformActionListener(platformActionListener); // 设置分享事件回调 //
        // 执行图文分享
        qzone.share(sp);
    }

//    /**
//     * 分享到sina微博
//     */
//    public void sinaWeibo() {
//        Platform.ShareParams sp = new Platform.ShareParams ( );
//        sp.setShareType(Platform.SHARE_WEBPAGE);
//        sp.setText(shareParams.getText() + shareParams.getUrl());
//        sp.setImageUrl(shareParams.getImageUrl());
//
//        Platform sinaWeibo = ShareSDK.getPlatform ( context, SinaWeibo.NAME );
//        sinaWeibo.setPlatformActionListener ( platformActionListener );
//        //执行分享
//        sinaWeibo.share ( sp );
//    }

    /**
     * 分享单元实体
     */
    public class ShareItem{
        private String shareName;
        private int shareIcon;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ShareItem(int id, String shareName, int shareIcon)
        {
            this.id = id;
            this.shareIcon = shareIcon;
            this.shareName = shareName;
        }

        public String getShareName() {
            return shareName;
        }

        public void setShareName(String shareName) {
            this.shareName = shareName;
        }

        public int getShareIcon() {
            return shareIcon;
        }

        public void setShareIcon(int shareIcon) {
            this.shareIcon = shareIcon;
        }
    }

    /**
     * 判断微博客户端是否可用
     *
     * @param context
     * @return
     */
    public boolean isWeiBoClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }
        return false;
    }
}
