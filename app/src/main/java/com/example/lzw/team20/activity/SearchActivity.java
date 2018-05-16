package com.example.lzw.team20.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.lzw.team20.R;

/**
 * Created by Myh on 2018/4/18.
 */

public class SearchActivity extends AppCompatActivity {

    private String[] mStrs = {"aaa", "bbb", "ccc", "airsaid"};
    private SearchView searchView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_bar);
        searchView = (SearchView) findViewById(R.id.searchview);
        listView = (ListView) findViewById(R.id.listview);

        searchView.setSubmitButtonEnabled(true);//设置提交按钮
        searchView.onActionViewExpanded();//初始状态下展开搜索框，否则需要点击“放大镜”

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStrs));
        listView.setTextFilterEnabled(true);

        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    listView.setFilterText(newText);
                }else{
                    listView.clearTextFilter();
                }
                return false;
            }
        });

    }

}
