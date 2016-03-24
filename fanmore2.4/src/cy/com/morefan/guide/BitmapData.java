package cy.com.morefan.guide;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class BitmapData implements Parcelable {
    public Bitmap bitmap;
    public ImageView imageView;
    public BitmapData(Bitmap bitmap,ImageView imageView){
        this.bitmap = bitmap;
        this.imageView = imageView;
    }

    protected
    BitmapData ( Parcel in ) {
        bitmap = in.readParcelable ( Bitmap.class.getClassLoader ( ) );
    }

    public static final Creator< BitmapData > CREATOR = new Creator< BitmapData >( ) {
        @Override
        public
        BitmapData createFromParcel ( Parcel in ) {
            return new BitmapData ( in );
        }

        @Override
        public
        BitmapData[] newArray ( int size ) {
            return new BitmapData[ size ];
        }
    };

    @Override
    public
    int describeContents ( ) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public
    void writeToParcel ( Parcel dest, int flags ) {
        // TODO Auto-generated method stub

        dest.writeParcelable ( bitmap, flags );
    }

}
