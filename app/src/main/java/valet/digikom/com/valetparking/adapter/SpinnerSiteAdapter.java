package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.AuthResponse;

/**
 * Created by DIGIKOM-EX4 on 2/15/2017.
 */

public class SpinnerSiteAdapter extends BaseAdapter {

    private Context context;
    private List<AuthResponse.Data.RoleOption> roleOptions;

    public SpinnerSiteAdapter(Context context, List<AuthResponse.Data.RoleOption> roleOptions) {
        this.context = context;
        this.roleOptions = roleOptions;
    }

    @Override
    public int getCount() {
        return roleOptions.size();
    }

    @Override
    public Object getItem(int i) {
        return roleOptions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return roleOptions.get(i).getUserRoleId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layout_valet_type, null);

        TextView txtSite = (TextView) convertView.findViewById(R.id.text_valet_type_yp);
        AuthResponse.Data.RoleOption roleOption = (AuthResponse.Data.RoleOption) getItem(i);
        txtSite.setText(roleOption.getSiteName());
        return convertView;
    }
}
