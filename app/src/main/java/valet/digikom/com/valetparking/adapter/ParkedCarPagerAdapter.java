package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import valet.digikom.com.valetparking.R;

/**
 * Created by DIGIKOM-EX4 on 1/23/2017.
 */

public class ParkedCarPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments;
    List<String> titles;
    Context context;

    public ParkedCarPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragments(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public View getTabView(int pos) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab,null);
        TextView textView = (TextView) v.findViewById(R.id.text_tab_title);
        textView.setText(titles.get(pos));
        return v;
    }
}
