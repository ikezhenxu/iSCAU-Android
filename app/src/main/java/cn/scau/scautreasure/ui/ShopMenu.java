package cn.scau.scautreasure.ui;

import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.appmsg.AppMsg;
import com.joanzapata.android.BaseAdapterHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.scau.scautreasure.R;
import cn.scau.scautreasure.helper.FoodShopHelper;
import cn.scau.scautreasure.model.ShopMenuDBModel;

/**
 * Created by apple on 14-8-24.
 */
@EActivity(R.layout.activity_shop_menu)
public class ShopMenu extends CommonActivity {
    @Extra("")
    String id;
    @Extra("")
    String shop_name;
    @Extra("")
    String phone;
    @Extra("")
    String edit_time;
    @Extra
    String shop_logo;

    @Extra
   String start_time;
    @Extra
    String end_time;
    @ViewById(R.id.foodMenu)
    ListView lvMenu;
    FoodMenuAdapter fmAdapter;
    @Bean
    FoodShopHelper helper;


    private boolean isRest=false;
    List<ShopMenuDBModel> menuList,orderList;
    private String msg="";


    @Click(R.id.order)
    void next(){
        if(!isRest) {
            makeList();
            if ((orderList.size() > 0)) {
                OrderFood_.intent(this).sendList(orderList).msg(msg).phone(phone).shopName(shop_name).shopLogo(shop_logo).start();
            } else {
                Toast.makeText(this, "你还没选择饭菜", Toast.LENGTH_SHORT).show();
            }
        }else{
            AppMsg.makeText(getSherlockActivity(),"本外卖店休息中",AppMsg.STYLE_ALERT).show();
        }
    }
    private void checkTime(String start,String end ){
        String startTime[]=start.split(":");
        String endTime[]=end.split(":");
        int startMin=Integer.parseInt(startTime[0])*60+Integer.parseInt(startTime[1]);
        int endMin=Integer.parseInt(endTime[0])*60+Integer.parseInt(endTime[1]);
        java.util.Calendar c = java.util.Calendar.getInstance();
        int nowMIn=c.get(java.util.Calendar.HOUR_OF_DAY)*60+c.get(java.util.Calendar.MINUTE);
        if (!(nowMIn>startMin&&nowMIn<endMin)){
            isRest=true;
            getSherlockActivity().getSupportActionBar().setSubtitle("休息中");
        }
    }
    /*
    鸡扒饭 1份,鸡腿饭 3份,送到北京[来自华农宝客户端]----菜名1 份数,菜名2 份数,…。送到:地址。[来自华农宝客户端]
     */
    private void makeList(){
        orderList=new ArrayList<ShopMenuDBModel>();
        for (int i=0;i<menuList.size();i++){


            if (menuList.get(i).getCount()>0) {
                msg += menuList.get(i).getFood_name() + " " + menuList.get(i).getCount() + "份,";
                orderList.add(menuList.get(i));
            }

        }
        Log.i("订餐菜数:",String.valueOf(orderList.size()));

    }

    @AfterViews
    void initView(){
         setTitle(""+shop_name);
        checkTime(start_time,end_time);

        loadData();
        fmAdapter=new FoodMenuAdapter();
        lvMenu.setAdapter(fmAdapter);
         Log.i("当前快餐店:",id+"|"+shop_name+"|"+phone+"|"+edit_time);
     }

    void loadData(){
        menuList=helper.getMenuList("food_shop_id",id);
        Log.i("数据数量",String.valueOf(menuList.size()));


    }
    //方便起见就不写成独立adapter
     class FoodMenuAdapter extends BaseAdapter{


         @Override
         public int getCount() {
             return menuList.size();
         }

         @Override
         public Object getItem(int i) {
             return menuList.get(i);
         }

         @Override
         public long getItemId(int i) {
             return i;
         }

         @Override
         public View getView(final int i, View view, ViewGroup viewGroup) {
             ViewHolder viewHolder=null;
            if (view==null){
                viewHolder=new ViewHolder();
                view= LayoutInflater.from(getSherlockActivity()).inflate(R.layout.food_menu_list_item_layout,null);
                viewHolder.removeOne=(ImageButton)view.findViewById(R.id.removeOne);
                viewHolder.addOne=(ImageButton)view.findViewById(R.id.addOne);
                viewHolder.foodName=(TextView)view.findViewById(R.id.foodName);
                viewHolder.foodCount=(TextView)view.findViewById(R.id.foodCount);
                viewHolder.foodMoney=(TextView)view.findViewById(R.id.foodMoney);

                view.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)view.getTag();
            }
             viewHolder.foodName.setText(menuList.get(i).getFood_name());
             viewHolder.foodMoney.setText( "￥"+menuList.get(i).getFood_price());
             final ViewHolder finalViewHolder = viewHolder;
             viewHolder.removeOne.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                    if (menuList.get(i).getCount()>0) {
                        menuList.get(i).setCount(menuList.get(i).getCount() - 1);
                        finalViewHolder.foodCount.setText(String.valueOf(menuList.get(i).getCount()));
                    }
                 }
             });
             viewHolder.addOne.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                         menuList.get(i).setCount(menuList.get(i).getCount()+1);
                     finalViewHolder.foodCount.setText(String.valueOf(menuList.get(i).getCount()));
                 }
             });

             return view;
         }
        class ViewHolder{
            public ImageButton removeOne;
            public ImageButton addOne;
            public TextView foodName;
            public TextView foodCount;
            public TextView foodMoney;
        }

     }


}