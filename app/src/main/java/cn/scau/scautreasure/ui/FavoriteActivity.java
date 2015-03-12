package cn.scau.scautreasure.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


import cn.scau.scautreasure.AppContext;
import org.androidannotations.annotations.*;

import java.util.List;

import cn.scau.scautreasure.R;
import cn.scau.scautreasure.helper.FavoriteHelper;
import cn.scau.scautreasure.model.FavoriteModel;
import cn.scau.scautreasure.widget.AppOKCancelDialog;
import cn.scau.scautreasure.widget.AppToast;
import cn.scau.scautreasure.widget.CircleView;

/**
 * Created by macroyep on 2/8/15.
 * Time:23:49
 */
@EActivity(R.layout.favorite)
@OptionsMenu(R.menu.menu_favorite)
public class FavoriteActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    @Bean
    FavoriteHelper favoriteHelper;
    @ViewById(R.id.favoriteList)
    ListView listView;

    List<FavoriteModel> favoriteModels;
    FavoriteAdapter favoriteAdapter;

    @App
    AppContext app;

    @AfterViews
    void init() {
        setTitleText("我的收藏");
        setMoreButtonVisible(false);//隐藏默认的menu
        loadData();
    }

    boolean isSearching = false;


    @UiThread
    void loadData() {
        if(app.config.first_use_fav().get()){
            FavoriteModel f1 = new FavoriteModel();
            f1.setFavoriteType(FavoriteModel.TEXT);
            f1.setTitle("做笔记");
            f1.setContent("这是一个小小的记事本，用于记录你生活的点滴。");
            f1.setDate(AppContext.getStringDate());
            FavoriteModel f2 = new FavoriteModel();
            f2.setDate(AppContext.getStringDate());
            f2.setFavoriteType(FavoriteModel.ULR);
            f2.setTitle("华农宝官网");
            f2.setUrl("http://www.huanongbao.com");
            favoriteHelper.insertOneFavorite(f1);
            favoriteHelper.insertOneFavorite(f2);
            app.config.first_use_fav().put(false);
        }
        favoriteModels = favoriteHelper.loadAll();

        if (favoriteAdapter == null) {
            favoriteAdapter = new FavoriteAdapter();
            listView.setAdapter(favoriteAdapter);
        } else {
            favoriteAdapter.notifyDataSetChanged();
        }

    }

   /* @OptionsItem(R.id.menu_about)
    void menu_about() {
        AppOKCancelDialog.show(this, "你应该知道", "看到好的东西就收藏下来吧！", "我知道了", "哦", new AppOKCancelDialog.Callback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk() {
                AppToast.show(FavoriteActivity.this, "多谢理解与支持", 0);
            }
        });

    }*/


    @OptionsItem(R.id.menu_add_text)
    void menu_add_text() {
        FavoriteText_.intent(this).edit(false).startForResult(EDIT_FOR_RESULT);

    }

    public static final int EDIT_FOR_RESULT = 12300;

    @ItemClick(R.id.favoriteList)
    void itemClick(int pos) {
        FavoriteModel model = (FavoriteModel) favoriteAdapter.getItem(pos);
        if (model.getFavoriteType() == FavoriteModel.ULR) {
            BaseBrowser_.intent(this).url(model.getUrl()).allCache("0").browser_title(model.getTitle()).start();
        } else if (model.getFavoriteType() == FavoriteModel.TEXT) {
            FavoriteText_.intent(this).edit(true).model(model).startForResult(EDIT_FOR_RESULT);

        } else {

        }

    }

    @ItemLongClick(R.id.favoriteList)
    void onLongClick(final int i) {
        AppOKCancelDialog.show(FavoriteActivity.this, "操作", "是否删除?", "删除", "取消", new AppOKCancelDialog.Callback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk() {
                int result = favoriteHelper.removeOneFavorite(((FavoriteModel) favoriteAdapter.getItem(i)).getId());
                if (result > 0) {
                    favoriteModels.remove(i);
                    favoriteAdapter.notifyDataSetChanged();
                    AppToast.show(FavoriteActivity.this, "已删除", 0);
                } else {
                    AppToast.show(FavoriteActivity.this, "操作失败", 0);
                }
            }
        });

    }

    class FavoriteAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return favoriteModels == null ? 0 : favoriteModels.size();
        }

        @Override
        public Object getItem(int i) {
            return favoriteModels.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(FavoriteActivity.this).inflate(R.layout.favorite_list_item_layout, null);
                holder = new ViewHolder();
                holder.dateText = (TextView) view.findViewById(R.id.date);
                holder.titleText = (TextView) view.findViewById(R.id.title);
                holder.iconText = (CircleView) view.findViewById(R.id.item_logo);


                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.dateText.setText(favoriteModels.get(i).getDate().toString());
            if (favoriteModels.get(i).getFavoriteType() == FavoriteModel.TEXT) {
                holder.iconText.setText("文本");
                holder.iconText.setBackgroundColor(getResources().getColor(R.color.theme_color));
            } else {
                holder.iconText.setText("URL");
                holder.iconText.setBackgroundColor(getResources().getColor(R.color.red_btn_bg_color));
            }
            holder.titleText.setText(favoriteModels.get(i).getTitle());
            return view;
        }

        class ViewHolder {
            public TextView dateText;
            public TextView titleText;
            public CircleView iconText;
        }
    }


    @OptionsMenuItem(R.id.menu_search)
    MenuItem searchMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView = new SearchView(getApplicationContext());
        searchView.setOnQueryTextListener(this);

        searchMenu.setActionView(searchView);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        System.out.println(s);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (!s.equals("")) {
            favoriteModels.clear();
            favoriteModels = favoriteHelper.searchOneFavorite(s);
            favoriteAdapter.notifyDataSetChanged();
            isSearching = true;
        } else {
            favoriteModels.clear();
            favoriteModels = favoriteHelper.loadAll();
            favoriteAdapter.notifyDataSetChanged();
            isSearching = false;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            loadData();
    }


}