package com.example.lzw.team20.fragment;
import com.example.lzw.team20.R;
//import com.example.myh.team.chatting.ChatActivity;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.LoginActivity;
import com.example.lzw.team20.activity.MatchActivity;
import com.example.lzw.team20.activity.chatting.ChatActivity;
import com.example.lzw.team20.waterfallSupport.Card;
import com.example.lzw.team20.waterfallSupport.WaterfallAdapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Myh on 2018/3/23.
 */

public class TableFragment extends Fragment {

    private JSONObject data;

    //搜索框
    private String[] mStrs = {};
    private SearchView searchView;
    private ListView listView;
    //下拉框
    private TextView txtview;
    private String[] datas = {"截止时间优先","发布时间优先","智能匹配需求", "添加好友"};

    //瀑布流
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private WaterfallAdapter mAdapter;
    private List<Card> cards=new ArrayList<>();

    //瀑布流刷新
    private SwipeRefreshLayout swipeRefreshLayout;

    //瀑布流上拉加载
    private LinearLayoutManager linearLayoutManager;

    //用户id  需要登录注册传入的信息
    private String uid;
    //用户昵称
    private String unick;
    //用户头像
    private String uphoto;

    //学校名
    TextView schoolname;

    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};

    //该圆桌可加入
    Boolean canJoin=true;
    String refuseMessage;
    String resultFromdata;
    private final Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case R.string.msg_joinfail:
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_joinfail), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private static Handler join_msgHandler;

    public TableFragment(){

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_table,container,false);

        schoolname=(TextView)view.findViewById(R.id.schoolname);

        searchView = (SearchView) view.findViewById(R.id.searchview);
        listView = (ListView) view.findViewById(R.id.listview);
        txtview = (TextView) view.findViewById(R.id.txtview);//获取弹出按钮控件

        setPopup();
        setSearchView();
        setWaterfall(view);
        Intent intent=getActivity().getIntent();
        try {
            data=new JSONObject(intent.getStringExtra("data"));
            String str=data.getString("school").equals("null")?"学校":data.getString("school");
            schoolname.setText(str);
            uid=data.getString("userid");
            unick=data.getString("username");
            uphoto=data.getString("photo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setRefresh(view);
        //setLoad(view);


        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setSearchView(){
        searchView.setSubmitButtonEnabled(true);//设置提交按钮
        //searchView.onActionViewExpanded();//初始状态下展开搜索框，否则需要点击“放大镜”
        listView.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, mStrs));
        listView.setTextFilterEnabled(true);
        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {// 当点击搜索按钮时触发该方法
                //searchView.setQuery("", false);//清空内容
                //searchView.onActionViewCollapsed();//收起SearchView视图
                searchCards(query);
                searchView.clearFocus();  //收起键盘

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {// 当搜索内容改变时触发该方法

                //会有悬浮框
 /*               if (!TextUtils.isEmpty(newText)){
                    listView.setFilterText(newText);
                }else{
                    listView.clearTextFilter();
                }
                return false;
*/
                //消除了悬浮框
                ListAdapter adapter = listView.getAdapter();
                if (adapter instanceof Filterable) {
                    Filter filter = ((Filterable) adapter).getFilter();
                    if (newText == null || newText.length() == 0) {
                        filter.filter(null);
                    } else {
                        filter.filter(newText);
                    }
                }
                return true;
            }
        });
    }

    public void setPopup(){

        txtview.setOnClickListener(new View.OnClickListener() {//给按钮设置单击事件监听
            @Override
            public void onClick(View v) {
                final View popupView = getActivity().getLayoutInflater().inflate(R.layout.popupwindow, null);// 构建一个popupwindow的布局
                final ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);// 设置数据
                lsvMore.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, datas));
                final PopupWindow popup = new PopupWindow(popupView, 400, 600);//创建PopupWindow对象，指定宽度和高度
                //window.setAnimationStyle(R.style.popup_window_anim);//设置动画
                popup.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));//设置背景颜色
                popup.setFocusable(true);//设置可以获取焦点
                popup.setOutsideTouchable(true);//设置可以触摸弹出框以外的区域
                popup.update();//更新popupwindow的状态
                popup.showAsDropDown(txtview, 0, 20);//以下拉的方式显示，并且可以设置显示的位置
                lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position){
                            case 0:
                                Log.v(" ","截止时间优先");
                                showCardsByDDL();
                                popup.dismiss();
                                break;
                            case 1:
                                Log.v(" ","创建时间优先");
                                showCardsByCT();
                                popup.dismiss();
                                break;
                            case 2://智能匹配需求
                                Log.v(" ","智能匹配需求");
                                popup.dismiss();
                                Intent intent=new Intent(getActivity(),MatchActivity.class);
                                intent.putExtra("data",data.toString());
                                startActivity(intent);
                                break;
                            case 3://添加好友
                                Log.v(" ","添加好友");
                                Toast.makeText(getContext(),"好友模块暂未上线，敬请期待",Toast.LENGTH_SHORT).show();
                                popup.dismiss();
                                break;
                        }
                    }
                });

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setWaterfall(View view){
        initData();
        mRecyclerView = (RecyclerView)view. findViewById(R.id.recyclerview);
        //设置布局管理器为2列，纵向
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new WaterfallAdapter(this.getContext(), cards);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnCardClickListener(new WaterfallAdapter.OnCardClickListener(){

            //在此处编写点击响应事件

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view,WaterfallAdapter.ClickName clickName,String tid,String tname,String tphoto){
                canJoin=true;
                switch (clickName){
                    case JOIN_BUTTON:

                        //Toast.makeText(getContext(),"Click button from "+tid,Toast.LENGTH_SHORT).show();
                        /*join_msgHandler=new Handler(){
                            @Override
                            public void handleMessage(Message msg){
                                switch (msg.arg1){
                                    case R.string.msg_tableisfull:
                                        Toast.makeText(getActivity(), getResources().getString(R.string.msg_tableisfull), Toast.LENGTH_SHORT).show();
                                        setCanJoinFalse();
                                        break;
                                    case R.string.msg_tableisJoined:
                                        Toast.makeText(getActivity(), getResources().getString(R.string.msg_tableisJoined), Toast.LENGTH_SHORT).show();
                                        setCanJoinFalse();
                                        break;
                                }

                            }
                        };*/
                        isFull(tid);
                        isJoin(tid);

                        if(canJoin)
                        {
                            joinTable(tid);
                            transIntentChatting(view,tid,tname,tphoto);
                        }else
                            Toast.makeText(getActivity(), refuseMessage, Toast.LENGTH_SHORT).show();

                        break;
                    case CARD:
                        //Toast.makeText(getContext(),"Click card from "+tid,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    private void setCanJoinFalse(){
        canJoin=false;
    }

    private void setMessage(String message){
        refuseMessage=message;
    }

    //判断该圆桌是否已满
    private void isFull(final String tid){
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String info = WebService.getOne(tid);
                    if (!TextUtils.isEmpty(info)) {
                        try {
                            JSONObject json = new JSONObject(info);
                            String nowNumber = json.getString("nowNumber");
                            String maxNumber=json.getString("maxNumber");
                            int last=Integer.valueOf(maxNumber)-Integer.valueOf(nowNumber);
                            if (last<=0) {
                                /*Message msg = join_msgHandler.obtainMessage();
                                msg.arg1 = R.string.msg_tableisfull;
                                join_msgHandler.sendMessage(msg);*/
                                setCanJoinFalse();
                                setMessage("该圆桌已满");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //判断该用户是否已加入圆桌
    private void isJoin(final String tid){
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String info = WebService.isJoined(tid);
                    if (!TextUtils.isEmpty(info)) {
                        try {
                            //JSONObject json = new JSONObject(info);
                            //String result = json.getString("result");
                            if (info.equals("true")) {
                                /*Message msg = join_msgHandler.obtainMessage();
                                msg.arg1 = R.string.msg_tableisJoined;
                                join_msgHandler.sendMessage(msg);*/
                                setCanJoinFalse();
                                setMessage("已经是该圆桌成员");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //该用户加入圆桌
    private void joinTable(final String tid) {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String info = WebService.joinRTable(tid);
                    if (!TextUtils.isEmpty(info)) {
                        try {
                            JSONObject json = new JSONObject(info);
                            String result = json.getString("result");
                            if (result.equals("false")) {
                                Message msg = msgHandler.obtainMessage();
                                msg.arg1 = R.string.msg_joinfail;
                                msgHandler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //点击join跳转至圆桌聊天界面
    private void transIntentChatting(View v,String tid,String tname,String tphoto){
        Intent intent=new Intent(this.getActivity(), ChatActivity.class);

        //创建Bundle打包数据
        Bundle bundle=new Bundle();
        bundle.putString("myaccount",uid);
        bundle.putString("deskaccount",tid);
        bundle.putString("desknick",tname);
        bundle.putString("mynick",unick);
        bundle.putString("myavatar",uphoto);
        bundle.putString("deskavatar",tphoto);

        intent.putExtras(bundle);

        startActivity(intent);


    }

    //模糊搜索
    private void searchCards(final String keyWord){

        search(keyWord);
        mAdapter.notifyDataSetChanged();


    }

    private void search(final String keyWord){
        try {
            Thread getCards = new Thread(new Runnable() {
                @Override
                public void run() {
                    resultFromdata = WebService.getRtables(keyWord);
                    //Message mes = new Message();
                    //mes.obj = result;
                    //mHandler.sendMessage(mes);

                }
            });
            getCards.start();
            getCards.join();

            cards.clear();

            JSONObject jsonObject = new JSONObject(resultFromdata);
            JSONArray jsonArray = jsonObject.getJSONArray("content");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                Card card = new Card();
                card.tid = jsonObject1.getString("rtableid");           //圆桌id
                card.cardName = jsonObject1.getString("rtablename");  //圆桌名
                card.cardLabel = jsonObject1.getString("first_tag"); //圆桌类别
                card.cardLabel_2 = jsonObject1.getString("rtablename"); //二级标签
                card.intro = jsonObject1.getString("detail");         //圆桌简介
                card.time = jsonObject1.getString("createtime").substring(0, 10) + "~" + jsonObject1.getString("ddl").substring(0, 10);           //时间
                int lastNum=jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber");
                if(lastNum>0)
                    card.lastNum = String.valueOf(lastNum);
                else
                    card.lastNum="0";
                //card.lastNum = String.valueOf(jsonObject1.getInt("maxNumber") - jsonObject1.getInt("nowNumber"));     //剩余席位
                card.avatarNo= jsonObject1.getString("photo");

                cards.add(card);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setRefresh(View view){
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setEnabled(true);
        //设置手指下滑多少触发刷新
        swipeRefreshLayout.setDistanceToTriggerSync(300);
        //设置小圆圈的偏移量 1、是否进行缩放  2、开始出现的位置  3、最远出现的位置
        swipeRefreshLayout.setProgressViewOffset(false, 200, 300);
        swipeRefreshLayout.setColorSchemeResources(R.color.text_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                refreshCards();
            }
        });
    }

    //刷新
    public void refreshCards(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(200);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        }).start();

    }

    //调用webservice刷新数据
    public void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //将重新获取的数据添加到cards中
                String result= WebService.getSchoolRtablesByDDL();
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray=jsonObject.getJSONArray("content");

                    //List<Card> newCards=new ArrayList<>();
                    cards.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        Card card=new Card();
                        card.tid=jsonObject1.getString("rtableid");           //圆桌id
                        card.cardName=jsonObject1.getString("rtablename");  //圆桌名
                        card.cardLabel=jsonObject1.getString("first_tag"); //圆桌类别
                        card.cardLabel_2=jsonObject1.getString("rtablename"); //二级标签
                        card.intro=jsonObject1.getString("detail");         //圆桌简介
                        int lastNum=jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber");
                        if(lastNum>0)
                            card.lastNum = String.valueOf(lastNum);
                        else
                            card.lastNum="0";
                        card.time=jsonObject1.getString("createtime").substring(0,10)+"~"+jsonObject1.getString("ddl").substring(0,10);           //时间
                        //card.lastNum=String.valueOf(jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber"));     //剩余席位
                        card.avatarNo= jsonObject1.getString("photo");

                        cards.add(card);
                    }

                    //cards.addAll(0,newCards);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();



        /*String[] tid={"004","005","006","007"};
        String[] cardName={"爬山","吃自助","打桌游","ACM竞赛"};
        String[] cardLabel={"学习","娱乐","学习","学习"};
        String[] cardLabel_2={"爬山","吃自助","打桌游","ACM竞赛"};
        String[] intro={"如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法那只说明你还不够了解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你解它。",
        };
        String[] time={"2017.2.13-2018.3.15","2017.2.13-2018.3.15","2017.2.13-2018.3.15",
                "2017.2.13-2018.3.15"};
        String[] lastNum={"2","3","5","5"};


        List<Card> newCards=new ArrayList<>();
        for(int i=0;i<4;i++){
            Card card=new Card();
            card.tid=tid[i];             //圆桌id
            card.cardName=cardName[i];   //圆桌名
            card.cardLabel=cardLabel[i]; //圆桌类别
            card.cardLabel_2=cardLabel_2[i]; //二级标签
            card.intro=intro[i];         //圆桌简介
            card.time=time[i];           //时间
            card.lastNum=lastNum[i];     //剩余席位
            //card.headUrl=head[i];

            newCards.add(card);
        }
        cards.addAll(0,newCards);*/
    }

    /*public void setLoad(View view){
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView,newState);

                int lastVisibleItem=0;

                if(mLayoutManager instanceof GridLayoutManager){
                    lastVisibleItem = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                }else if(mLayoutManager instanceof LinearLayoutManager) {
                    lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                }else if(mLayoutManager instanceof StaggeredGridLayoutManager){
                    //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                    //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                    int[] lastPositions = new int[((StaggeredGridLayoutManager) mLayoutManager).getSpanCount()];
                    ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(lastPositions);
                    lastVisibleItem = findMax(lastPositions);
                }

                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==mAdapter.getItemCount()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //loadCards();
                            mAdapter.notifyDataSetChanged();
                        }
                    },1000);
                }
            }
        });


    }*/




    //加载数据 假装有
    /*public void loadCards(){
        String[] tid={"004","005","006","007"};
        String[] cardName={"more1","more2","more3","more4"};
        String[] cardLabel={"学习","娱乐","学习","学习"};
        String[] cardLabel_2={"自习","吃","比赛","ACM"};
        String[] intro={"如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法那只说明你还不够了解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你解它。",
        };
        String[] time={"2017.2.13-2018.3.15","2017.2.13-2018.3.15","2017.2.13-2018.3.15",
                "2017.2.13-2018.3.15"};
        String[] lastNum={"2","3","5","5"};

        List<Card> newCards=new ArrayList<>();
        for(int i=0;i<4;i++){
            Card card=new Card();
            card.tid=tid[i];             //圆桌id
            card.cardName=cardName[i];   //圆桌名
            card.cardLabel=cardLabel[i]; //圆桌类别
            card.cardLabel_2=cardLabel_2[i]; //二级标签
            card.intro=intro[i];         //圆桌简介
            card.time=time[i];           //时间
            card.lastNum=lastNum[i];     //剩余席位
            //card.headUrl=head[i];

            newCards.add(card);
        }
        cards.addAll(newCards);
    }*/

    private int findMax(int[] lastPositions){
        int max=lastPositions[0];
        for(int i=1;i<lastPositions.length;i++)
            max=lastPositions[i]>max?lastPositions[i]:max;
        return max;
    }



    public void initData(){
        //在此处编写获取数据相关代码

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String result= WebService.getSchoolRtablesByDDL();
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray=jsonObject.getJSONArray("content");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        Card card=new Card();
                        card.tid=jsonObject1.getString("rtableid");           //圆桌id
                        card.cardName=jsonObject1.getString("rtablename");  //圆桌名
                        card.cardLabel=jsonObject1.getString("first_tag"); //圆桌类别
                        card.cardLabel_2=jsonObject1.getString("rtablename"); //二级标签
                        card.intro=jsonObject1.getString("detail");         //圆桌简介
                        //String time=jsonObject1.getString("createtime");
                        int lastNum=jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber");
                        if(lastNum>0)
                            card.lastNum = String.valueOf(lastNum);
                        else
                            card.lastNum="0";
                        card.time=jsonObject1.getString("createtime").substring(0,10)+"~"+jsonObject1.getString("ddl").substring(0,10);           //时间
                        //card.lastNum=String.valueOf(jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber"));     //剩余席位
                        card.avatarNo= jsonObject1.getString("photo");

                        cards.add(card);

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        try{
            thread.start();
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }





        //测试 假装有数据
        /*
        String[] tid={"000","001","002","003","004","005","006","007"};
        String[] cardName={"约学习","约食堂","ACM竞赛","ACM竞赛","ACM竞赛","ACM竞赛","ACM竞赛","ACM竞赛"};
        String[] cardLabel={"学习","娱乐","学习","学习","学习","学习","学习","学习"};
        String[] cardLabel_2={"自习","吃","比赛","ACM","ACM","ACM","ACM","ACM"};
        String[] intro={"如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法那只说明你还不够了解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法那只说明你还不够了解它。"};
        String[] time={"2017.2.13-2018.3.15","2017.2.13-2018.3.15","2017.2.13-2018.3.15",
                "2017.2.13-2018.3.15","2017.2.13-2018.3.15","2017.2.13-2018.3.15",
                "2017.2.13-2018.3.15","2017.2.13-2018.3.15"};
        String[] lastNum={"2","3","5","5","5","5","5","5"};
        String[] head={"E:\\AndroidProject\\Meizhi-master\\Waterfall\\app\\src\\main\\res\\mipmap-hdpi\\head.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524042047489&di=2e72d6ffb68c0c1bb62f9584ddd6f828&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0138a0565ed39532f875ae34c10c69.jpg%402o.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524042047489&di=2e72d6ffb68c0c1bb62f9584ddd6f828&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0138a0565ed39532f875ae34c10c69.jpg%402o.jpg"};

        for(int i=0;i<8;i++){
            Card card=new Card();
            card.tid=tid[i];             //圆桌id
            card.cardName=cardName[i];   //圆桌名
            card.cardLabel=cardLabel[i]; //圆桌类别
            card.cardLabel_2=cardLabel_2[i]; //二级标签
            card.intro=intro[i];         //圆桌简介
            card.time=time[i];           //时间
            card.lastNum=lastNum[i];     //剩余席位
            //card.headUrl=head[i];

            cards.add(card);
        }*/

    }

    //根据截止日期展示
    public void showCardsByDDL() {

        getCardsByDDL();
        mAdapter.notifyDataSetChanged();


    }
    public void getCardsByDDL(){
        try {
            Thread getCards = new Thread(new Runnable() {
                @Override
                public void run() {
                    resultFromdata = WebService.getSchoolRtablesByDDL();
                    //Message mes = new Message();
                    //mes.obj = result;
                    //mHandler.sendMessage(mes);

                }
            });
            getCards.start();
            getCards.join();

            cards.clear();

            JSONObject jsonObject = new JSONObject(resultFromdata);
            JSONArray jsonArray = jsonObject.getJSONArray("content");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                Card card = new Card();
                card.tid = jsonObject1.getString("rtableid");           //圆桌id
                card.cardName = jsonObject1.getString("rtablename");  //圆桌名
                card.cardLabel = jsonObject1.getString("first_tag"); //圆桌类别
                card.cardLabel_2 = jsonObject1.getString("rtablename"); //二级标签
                card.intro = jsonObject1.getString("detail");         //圆桌简介
                String time = jsonObject1.getString("createtime");
                card.time = jsonObject1.getString("createtime").substring(0, 10) + "~" + jsonObject1.getString("ddl").substring(0, 10);           //时间
                int lastNum=jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber");
                if(lastNum>0)
                    card.lastNum = String.valueOf(lastNum);
                else
                    card.lastNum="0";
                // card.lastNum = String.valueOf(jsonObject1.getInt("maxNumber") - jsonObject1.getInt("nowNumber"));     //剩余席位
                card.avatarNo= jsonObject1.getString("photo");

                cards.add(card);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据创建日期展示
    public void showCardsByCT() {

        getCardsByCT();
        mAdapter.notifyDataSetChanged();


    }
    public void getCardsByCT(){
        try {
            Thread getCards = new Thread(new Runnable() {
                @Override
                public void run() {
                    resultFromdata = WebService.getSchoolRtablesByCT();
                    //Message mes = new Message();
                    //mes.obj = result;
                    //mHandler.sendMessage(mes);

                }
            });
            getCards.start();
            getCards.join();

            cards.clear();

            JSONObject jsonObject = new JSONObject(resultFromdata);
            JSONArray jsonArray = jsonObject.getJSONArray("content");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                Card card = new Card();
                card.tid = jsonObject1.getString("rtableid");           //圆桌id
                card.cardName = jsonObject1.getString("rtablename");  //圆桌名
                card.cardLabel = jsonObject1.getString("first_tag"); //圆桌类别
                card.cardLabel_2 = jsonObject1.getString("rtablename"); //二级标签
                card.intro = jsonObject1.getString("detail");         //圆桌简介
                String time = jsonObject1.getString("createtime");
                card.time = jsonObject1.getString("createtime").substring(0, 10) + "~" + jsonObject1.getString("ddl").substring(0, 10);           //时间
                int lastNum=jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber");
                if(lastNum>0)
                    card.lastNum = String.valueOf(lastNum);
                else
                    card.lastNum="0";
                // card.lastNum = String.valueOf(jsonObject1.getInt("maxNumber") - jsonObject1.getInt("nowNumber"));     //剩余席位
                card.avatarNo= jsonObject1.getString("photo");

                cards.add(card);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}
