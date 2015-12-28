package cy.com.morefan;

/**
 * Created by Administrator on 2015/12/25.
 */
public class OrderInfo {
    int resultCode ;
    int status;
    String description;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public innerClas getResultData() {
        return resultData;
    }

    public void setResultData(innerClas resultData) {
        this.resultData = resultData;
    }

    innerClas resultData;

    public class innerClas{
        String ToStr;

        public double getFinal_Amount() {
            return Final_Amount;
        }

        public void setFinal_Amount(double final_Amount) {
            Final_Amount = final_Amount;
        }

        public String getToStr() {
            return ToStr;
        }

        public void setToStr(String toStr) {
            ToStr = toStr;
        }

        double Final_Amount;
    }

}
