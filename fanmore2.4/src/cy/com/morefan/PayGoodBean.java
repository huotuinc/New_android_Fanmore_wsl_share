package cy.com.morefan;

/**
 * 支付商品模型
 */
public
class PayGoodBean extends BaseBean {

    public String getOrderNo() {
        return orderNo;
    }

    public int getProductType() {
        return productType;
    }

    public long getProductId() {
        return productId;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }
    public String getTag() {        return tag;    }

    public void setTag(String tag) {        this.tag = tag;    }

    private String orderNo;
    private int productType;
    private long productId;
    private String tag;
}
