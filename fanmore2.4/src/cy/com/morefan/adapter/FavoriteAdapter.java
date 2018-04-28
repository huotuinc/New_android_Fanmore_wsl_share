package cy.com.morefan.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.FavoriteData;

public class FavoriteAdapter extends BaseQuickAdapter<FavoriteData, BaseViewHolder> {

    public FavoriteAdapter( @Nullable List<FavoriteData> data) {
        super(R.layout.layout_favorite_item , data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FavoriteData item) {

        helper.setText(R.id.favorite_item_text , item.TaskName);

        SimpleDraweeView simpleDraweeView = helper.getView(R.id.favorite_item_avator);
        simpleDraweeView.setImageURI(item.TaskPicUrl);
        helper.setText(R.id.favorite_item_time , item.time);

        helper.addOnClickListener(R.id.favorite_item_delete);
        helper.addOnClickListener(R.id.favorite_item_container);
    }
}
