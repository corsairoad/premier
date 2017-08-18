package valet.intan.com.valetparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.domain.DropPointMaster;

/**
 * Created by DIGIKOM-EX4 on 1/11/2017.
 */

public class DropPointAdapter extends BaseAdapter {

    private Context context;
    private List<DropPointMaster> dropPointList;

    public DropPointAdapter(Context context, List<DropPointMaster> dropPointList) {
        this.context = context;
        this.dropPointList = dropPointList;
    }

    @Override
    public int getCount() {
        return dropPointList.size();
    }

    @Override
    public Object getItem(int i) {
        return dropPointList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dropPointList.get(i).getAttrib().getDropId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_item_cartype,viewGroup, false);
        }

        CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.circle_image);
        circleImageView.setVisibility(View.GONE);
        TextView textView = (TextView) view.findViewById(R.id.text_cartype);
        DropPointMaster dropPoint = (DropPointMaster) getItem(i);
        textView.setText(dropPoint.getAttrib().getDropName());

        return view;
    }
}
