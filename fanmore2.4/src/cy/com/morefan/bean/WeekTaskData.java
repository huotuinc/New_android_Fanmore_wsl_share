package cy.com.morefan.bean;

/**
 * Created by 47483 on 2016/6/20.
 */

public class WeekTaskData implements BaseData {


    public int id ;

    public String title ;/// 本周任务指标
    public int target;/// 已完成数
    public int myFinishCount ;/// 操作类型
    public int actionType ;/// 绑定的商户ID
    public int bindStoreId ;

    public String storeName;

    public int sort ;

    public int reward ;

    public int createtime ;

    @Override
    public String getPageTag() {
        // TODO Auto-generated method stub
        return null;
    }
}
