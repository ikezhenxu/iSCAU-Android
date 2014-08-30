package cn.scau.scautreasure.widget;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.scau.scautreasure.R;

/**
 * 巴士站点部件
 * <p/>
 * User: special
 * Date: 13-9-8
 * Time: 下午4:40
 * Mail: specialcyci@gmail.com
 */
@EViewGroup(R.layout.bus_station_widget)
public class BusStationWidget extends TableRow {

    private static final int[] DRAWABLE_LIST = new int[]{
            R.drawable.icon_station_1,
            R.drawable.icon_station_2,
            R.drawable.icon_station_3,
            R.drawable.icon_station_4
    };
    @ViewById
    TextView tv_station;
    @ViewById
    ImageView iv_station;

    public BusStationWidget(Context context) {
        super(context);
    }

    public void setStationAndIndex(String station, int index) {
        tv_station.setText(station);
        iv_station.setImageResource(DRAWABLE_LIST[index % DRAWABLE_LIST.length]);

    }
}
