package cy.com.morefan.bean;

public class FavoriteData implements BaseData{
    public int Id;
    public int TurnUserId;
    public int TaskId;
    public String CreateTime;
    public  String TaskName;
    public String TaskPicUrl;
    public String time;

    @Override
    public String getPageTag() {
        return null;
    }
}
