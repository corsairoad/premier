package valet.intan.com.valetparking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.domain.ColorMaster;

/**
 * Created by DIGIKOM-EX4 on 1/10/2017.
 */

public class ColorTypeAdapter extends BaseAdapter {

    private List<ColorMaster> colorMasterList;
    private Context context;

    public ColorTypeAdapter(List<ColorMaster> colorMasterList, Context context) {
        this.colorMasterList = colorMasterList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return colorMasterList.size();
    }

    @Override
    public Object getItem(int i) {
        return colorMasterList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return colorMasterList.get(i).getAttrib().getId_color();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_item_cartype,viewGroup, false);
        }

        CircleImageView cm = (CircleImageView) view.findViewById(R.id.circle_image);
        cm.setVisibility(View.VISIBLE);
        TextView textColor = (TextView) view.findViewById(R.id.text_cartype);

        ColorMaster colorMaster = colorMasterList.get(i);
        cm.setBackgroundColor(Color.parseColor(colorMaster.getAttrib().getColorHex()));
        textColor.setText(colorMaster.getAttrib().getColorName());

        return view;
    }
}
