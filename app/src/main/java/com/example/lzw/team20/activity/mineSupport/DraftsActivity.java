package com.example.lzw.team20.activity.mineSupport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.MatchActivity;
import com.example.lzw.team20.activity.TableBuildingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DraftsActivity extends AppCompatActivity {

    private JSONObject data;

    private ListView lv_drafts;
    private MyDraftAdapter myDraftAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_drafts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("草稿箱");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try{
            data=new JSONObject(getIntent().getStringExtra("data"));
        }catch (JSONException e){
            e.printStackTrace();
        }



        lv_drafts=findViewById(R.id.mine_drafts);
        myDraftAdapter=new MyDraftAdapter();
        myDraftAdapter.setContext(this);
        List<Draft> drafts=getDrafts();
        if(drafts.size()==0){
            Toast.makeText(getApplicationContext(),"您当前还没有创建草稿~",Toast.LENGTH_SHORT).show();
        }else{
            myDraftAdapter.setmData(drafts);

            lv_drafts.setAdapter(myDraftAdapter);
            lv_drafts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Draft draft=(Draft)myDraftAdapter.getItem(i);

                    Intent intent = new Intent(DraftsActivity.this, DraftUpdateActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("draft",draft);
                    bundle.putString("data",data.toString());

                    intent.putExtras(bundle);
                    startActivity(intent);


                }
            });
        }

    }

    public List<Draft> getDrafts(){
        try {
            final List<Draft> drafts = new ArrayList<>();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try{
                        String result = WebService.getAllUndo();

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Draft draft = new Draft();
                            draft.needid = jsonObject1.getString("needid");           //草稿id
                            draft.firstTag = jsonObject1.getString("firstTag");  //草稿一级标签
                            draft.secondTag = jsonObject1.getString("secondTag"); //草稿二级标签
                            draft.createTime = jsonObject1.getString("createtime"); //创建时间
                            draft.ddl = jsonObject1.getString("ddl"); //截止时间
                            draft.detail = jsonObject1.getString("detail"); //详情

                            drafts.add(draft);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
            thread.join();

            return drafts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
