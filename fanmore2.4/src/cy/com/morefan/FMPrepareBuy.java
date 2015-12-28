package cy.com.morefan;

import java.util.List;

/**
 * 准备购买接口 返回数据类
 */
public
class FMPrepareBuy extends BaseBean {

    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = 4147236602400059956L;

    private InnerData resultData;

    public class InnerData
    {
        /**
         * 运营商信息
         */
        private String mobileMsg;

        /**
         * 可供选择的流量包
         */
        private List< Purchase > purchases;

        /**
         *
         */
        private String alipayPartner;

        private String alipayNotifyUri;

        private String wxpayAppId;

        private String wxpayMerchantId;

        private String wxpayNotifyUri;

        public String getMobileMsg ( ) {
            return mobileMsg;
        }

        public
        void setMobileMsg ( String mobileMsg ) {
            this.mobileMsg = mobileMsg;
        }

        public List< Purchase > getPurchases ( ) {
            return purchases;
        }

        public
        void setPurchases ( List< Purchase> purchases)
        {
            this.purchases = purchases;
        }

        public String getAlipayPartner()
        {
            return alipayPartner;
        }

        public void setAlipayPartner(String alipayPartner)
        {
            this.alipayPartner = alipayPartner;
        }

        public String getAlipayNotifyUri()
        {
            return alipayNotifyUri;
        }

        public void setAlipayNotifyUri(String alipayNotifyUri)
        {
            this.alipayNotifyUri = alipayNotifyUri;
        }

        public String getWxpayAppId()
        {
            return wxpayAppId;
        }

        public void setWxpayAppId(String wxpayAppId)
        {
            this.wxpayAppId = wxpayAppId;
        }

        public String getWxpayMerchantId()
        {
            return wxpayMerchantId;
        }

        public void setWxpayMerchantId(String wxpayMerchantId)
        {
            this.wxpayMerchantId = wxpayMerchantId;
        }

        public String getWxpayNotifyUri()
        {
            return wxpayNotifyUri;
        }

        public void setWxpayNotifyUri(String wxpayNotifyUri)
        {
            this.wxpayNotifyUri = wxpayNotifyUri;
        }
    }

    public InnerData getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerData resultData)
    {
        this.resultData = resultData;
    }
}
