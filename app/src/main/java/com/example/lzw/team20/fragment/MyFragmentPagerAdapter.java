package com.example.lzw.team20.fragment;
import com.example.lzw.team20.activity.MainActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Myh on 2018/3/23.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT=3;
    private TableFragment tableFragment=null;
    private NewsFragment newsFragment=null;
    private MineFragment mineFragment=null;

    public MyFragmentPagerAdapter(FragmentManager fm){
        super(fm);
        tableFragment=new TableFragment();
        newsFragment=new NewsFragment();
        mineFragment=new MineFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case MainActivity.PAGE_TABLE:
                fragment = tableFragment;
                break;
            case MainActivity.PAGE_NEWS:
                fragment = newsFragment;
                break;
            case MainActivity.PAGE_MINE:
                fragment = mineFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

}
