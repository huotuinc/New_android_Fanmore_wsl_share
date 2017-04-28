package cy.com.morefan.bean;

//import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by 47483 on 2016/7/7.
 */

public class AdlistModel   implements BaseData,Serializable {

    int itemId;
    String itemCreateTime;
    int itemShowSort;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemCreateTime() {
        return itemCreateTime;
    }

    public void setItemCreateTime(String itemCreateTime) {
        this.itemCreateTime = itemCreateTime;
    }

    public int getItemShowSort() {
        return itemShowSort;
    }

    public void setItemShowSort(int itemShowSort) {
        this.itemShowSort = itemShowSort;
    }

    public int getItemShowTime() {
        return itemShowTime;
    }

    public void setItemShowTime(int itemShowTime) {
        this.itemShowTime = itemShowTime;
    }

    public String getItemImgDescLink() {
        return itemImgDescLink;
    }

    public void setItemImgDescLink(String itemImgDescLink) {
        this.itemImgDescLink = itemImgDescLink;
    }

    public String getItemImgUrl() {
        return itemImgUrl;
    }

    public void setItemImgUrl(String itemImgUrl) {
        this.itemImgUrl = itemImgUrl;
    }

    int itemShowTime;
    String itemImgDescLink;
    String itemImgUrl ;

    public AdlistModel()
    {

    }

    public AdlistModel(int itemId, String itemImgDescLink, String itemImgUrl)
    {
        this.itemId = itemId;
        this.itemImgDescLink = itemImgDescLink
        ;
        this.itemImgUrl = itemImgUrl;
    }


    @Override
    public String getPageTag() {
        return null;
    }
}
