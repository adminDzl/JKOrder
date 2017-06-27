package com.example.ayou.jk_takeout.firstpage.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ayou.jk_takeout.R;
import com.example.ayou.jk_takeout.firstpage.adapter.class_shop_adapter;
import com.example.ayou.jk_takeout.firstpage.config.netConfig;
import com.example.ayou.jk_takeout.firstpage.expandtabview.SearchTabMiddle;
import com.example.ayou.jk_takeout.firstpage.expandtabview.SearchTabRight;
import com.example.ayou.jk_takeout.firstpage.expandtabview.SearchTabView;
import com.example.ayou.jk_takeout.firstpage.model.class_shopBean;
import com.example.ayou.jk_takeout.firstpage.shopcat.ShoppingCartActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class classShopActivity extends Activity implements class_shop_adapter.OnItemClickListener {
    @BindView(R.id.rv_class_shop)
    RecyclerView rvClassShop;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefreshWidget;
    @BindView(R.id.expandtab_view)
    SearchTabView expandtabView;


    private ArrayList<View> mViewArray = new ArrayList<View>();
    private SearchTabMiddle viewMiddle;
    private SearchTabRight viewRight;

    private String rank;
    private String specialty;
    private int page = 1;

    private class_shop_adapter shopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classshop);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        Intent intent = getIntent();
        rank = intent.getStringExtra("id");

        //获取数据
        getData(rank, page, false);
        //设置数据
        setData();

        shopAdapter.setListener(this);

    }


    /**
     * 设置店铺数据
     */
    private void setData() {
        shopAdapter = new class_shop_adapter(this);
        rvClassShop.setAdapter(shopAdapter);
        rvClassShop.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initData() {
        // tab设置
        mViewArray.add(viewMiddle);
        mViewArray.add(viewRight);

        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add(getResources().getString(R.string.rank_serach_title));
        mTextArray.add(getResources().getString(R.string.specialty_serach_title));
        expandtabView.setValue(mTextArray, mViewArray);
        expandtabView.setTitle(mTextArray.get(0), 0);
        expandtabView.setTitle(mTextArray.get(1), 1);


    }



    private void initView() {

        viewMiddle = new SearchTabMiddle(this);
        viewRight = new SearchTabRight(this);
        //下拉刷新
        swipeRefreshWidget.setColorSchemeResources(R.color.colorAccent,R.color.color_shop_text);
        swipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshWidget.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        getData(rank, page, false);
                        swipeRefreshWidget.setRefreshing(false);
                        Toast.makeText(classShopActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                }, 1500);
            }
        });

        //上拉加载
        rvClassShop.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) rvClassShop.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //最后一个item
                    int bottom = manager.findLastVisibleItemPosition();
                    //全部的item
                    int itemCount = manager.getItemCount();
                    if (bottom == itemCount - 1){
                        page = page+1;
                        getData(rank,page,true);
                    }

                }

            }
        });


    }


    /**
     * @Description: expandTab 监听
     */
    private void initListener() {

        //左侧监听
        viewMiddle.setOnSelectListener(new SearchTabMiddle.OnSelectListener() {

            @Override
            public void getValue(String itemLvTwocode, String showText) {
                //店铺id
                rank = itemLvTwocode;
                Log.i("jzq", "rank===" + rank);
                onRefresh(viewMiddle, showText);

                //获取店铺数据
                getData(rank, page, false);


            }

        });

        //右侧监听
        viewRight.setOnSelectListener(
                new SearchTabRight.OnSelectListener() {

                    @Override
                    public void getValue(String itemCode, String showText) {

                        specialty = itemCode;
                        onRefresh(viewRight, showText);
                    }
                });

    }

    /**
     * 获取数据
     */
    public void getData(String catalog, int page, final boolean isLoad) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(netConfig.BASIC_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        netConfig iservice = retrofit.create(netConfig.class);
        Call<class_shopBean> call = iservice.getClassShopData(catalog, page);
        call.enqueue(new Callback<class_shopBean>() {
            @Override
            public void onResponse(Call<class_shopBean> call, Response<class_shopBean> response) {
                class_shopBean bean = response.body();
                if (bean.getData().getList() != null) {
                    shopAdapter.setData(bean.getData().getList(), isLoad);
                }
            }

            @Override
            public void onFailure(Call<class_shopBean> call, Throwable t) {

            }
        });
    }


    /**
     * @param view
     * @param showText
     * @Description: 回调更新选中项
     */
    private void onRefresh(View view, String showText) {

        expandtabView.onPressBack();
        int position = getPositon(view);
        if (position >= 0 && !expandtabView.getTitle(position).equals(showText)) {
            expandtabView.setTitle(showText, position);
        }
        Toast.makeText(classShopActivity.this, showText, Toast.LENGTH_SHORT).show();

    }

    //获取选中项
    private int getPositon(View tView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (mViewArray.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }

    //关闭下拉菜单
    @Override
    public void onBackPressed() {
        if (!expandtabView.onPressBack()) {
            finish();
        }

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ShoppingCartActivity.class);
        class_shopBean.DataBean.ListBean shopData = shopAdapter.getShopData(position);
        Log.i("jzq", "点击"+shopData.getId());
        intent.putExtra("cartID",shopData.getId());
        intent.putExtra("lowPrice",shopData.getLowest_deliver_fee());
        startActivity(intent);
    }
}