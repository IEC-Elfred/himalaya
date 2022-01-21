package com.uniqueAndroid.ximalaya;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.uniqueAndroid.ximalaya.adapters.IndicatorAdapter;
import com.uniqueAndroid.ximalaya.adapters.MainContentAdapter;
import com.uniqueAndroid.ximalaya.utils.LogUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "categories";
    private MagicIndicator indicator;
    private ViewPager contentViewPager;
    private IndicatorAdapter indicatorAdapter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initEvent() {
        indicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG,"click index is " + index);
                if (contentViewPager != null) {
                    contentViewPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView() {
        indicator = this.findViewById(R.id.main_indicator);
        indicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        indicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator((this));
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(indicatorAdapter);
        //ViewPager
        contentViewPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(fragmentManager);

        contentViewPager.setAdapter(mainContentAdapter);
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, contentViewPager);
    }
}
