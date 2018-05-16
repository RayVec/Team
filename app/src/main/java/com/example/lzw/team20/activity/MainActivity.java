package com.example.lzw.team20.activity;
import com.example.lzw.team20.R;
import com.example.lzw.team20.fragment.MyFragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {

    private RadioGroup rg_tab_bar;
    private RadioButton rb_table;
    private RadioButton rb_news;
    private RadioButton rb_mine;
    private ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;

    //几个代表页面的常量
    public static final int PAGE_TABLE = 0;
    public static final int PAGE_NEWS = 1;
    public static final int PAGE_MINE = 2;

    private String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        rb_table.setChecked(true);
        getData();
    }



    private void bindViews() {
        mAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager());
        //Bundle bundle=new Bundle();
        //bundle.putString("data",data);


        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_table = (RadioButton) findViewById(R.id.rb_table);
        rb_news = (RadioButton) findViewById(R.id.rb_news);
        rb_mine = (RadioButton) findViewById(R.id.rb_mine);
        rg_tab_bar.setOnCheckedChangeListener(this);
        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);

        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_table:
                vpager.setCurrentItem(PAGE_TABLE);
                break;
            case R.id.rb_news:
                vpager.setCurrentItem(PAGE_NEWS);
                break;
            case R.id.rb_mine:
                vpager.setCurrentItem(PAGE_MINE);
                break;
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_TABLE:
                    rb_table.setChecked(true);
                    break;
                case PAGE_NEWS:
                    rb_news.setChecked(true);
                    break;
                case PAGE_MINE:
                    rb_mine.setChecked(true);
                    break;
            }
        }
    }

    //获取LoginActivity传入数值
    public void getData() {
        Intent intent = getIntent();
        data=intent.getStringExtra("data");
    }
}
