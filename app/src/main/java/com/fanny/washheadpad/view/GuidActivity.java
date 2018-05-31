package com.fanny.washheadpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanny.washheadpad.R;
import com.fanny.washheadpad.util.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuidActivity extends AppCompatActivity {

    @BindView(R.id.vp_guid)
    ViewPager vpGuid;
    @BindView(R.id.ll_item)
    LinearLayout llItem;
    @BindView(R.id.blue_iv)
    ImageView blueIv;
    @BindView(R.id.btn_enter)
    Button btn_enter;
    @BindView(R.id.ll_lastpage)
    LinearLayout llLastpage;
    @BindView(R.id.tv_protocal)
    TextView tvProtocal;
    @BindView(R.id.cb_agreePro)
    CheckBox cbAgreePro;

    private ArrayList<LinearLayout> mLayoutList;
    int[] layoutResIds = {
            R.layout.guid_page_one, R.layout.guid_page_two, R.layout.guid_page_three

    };
    private int pointWidth;//小灰点的距离
    int position;//当前界面数（从0开始）
    private WebView webviewPro;
    private View proView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guid);
        ButterKnife.bind(this);

        initGuidView();

    }

    private void initGuidView() {
        initData();

        VpAdapter adapter = new VpAdapter();
        vpGuid.setAdapter(adapter);
        vpGuid.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            /**
             * 界面滑动时回调此方法
             * arg0:当前界面数
             * positionOffset:界面滑动过的百分数（0.0-1.0）
             * positionOffsetPixels:当前界面偏移的像素位置
             */
            public void onPageScrolled(int arg0, float positionOffset, int positionOffsetPixels) {
                //小蓝点当前滑动距离
                int width;
                //1个界面就要一个小灰点的距离，再加上滑动过的百分比距离就是当前蓝点的位置
                width = (int) (positionOffset * pointWidth + arg0 * pointWidth);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) blueIv.getLayoutParams();
                //设置蓝点的左外边距
                lp.leftMargin = width;
                blueIv.setLayoutParams(lp);
                /**
                 * 开始体验按钮只能出现在最后一页，
                 * 并且在滑动的过程中保持消失
                 */
                if (position == layoutResIds.length - 1 && positionOffset == 0) {
                    llLastpage.setVisibility(View.VISIBLE);
                } else {
                    llLastpage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            //当前选中第几个界面
            public void onPageSelected(int arg0) {
                position = arg0;
            }

            @Override
            //状态改变时调用：arg0=0还没滑动,arg0=1正在滑动,arg0=2滑动完毕
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        mLayoutList = new ArrayList<>();
        for (int i = 0; i < layoutResIds.length; i++) {
            LinearLayout ll = (LinearLayout) ViewGroup.inflate(this, layoutResIds[i], null);
            mLayoutList.add(ll);

            /**
             * 绘制灰点
             */
            ImageView points = new ImageView(this);
            points.setImageResource(R.drawable.white_point);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            //给第一个以外的小灰点儿设置左边距，保证三个灰点水平居中
            if (i > 0) {
                lp.leftMargin = 30;//设置左外边距，像素
            }

            points.setLayoutParams(lp);
            llItem.addView(points);
        }

        /**
         * 为了完成蓝点在界面滑动时的动画效果，
         * 必须获取到灰点的边距，通过动态的给蓝点设置边距来完成动画效果
         * 由于在执行onCreate方法时，界面还没有绘制完成，无法获取pointWidth，设定小蓝点绘制完成的事件监听，当小蓝点绘制完成再获取
         */
        blueIv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                /**
                 * 获取小灰点圆心间的距离，第1个灰点和第二个灰点的距离
                 */
                pointWidth = llItem.getChildAt(1).getLeft() - llItem.getChildAt(0).getLeft();
            }
        });
    }

    @OnClick({R.id.btn_enter, R.id.tv_protocal,R.id.cb_agreePro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_enter:
                /**
                 * 首先要同意协议
                 */
                if(!cbAgreePro.isChecked()){
//                    Toast.makeText(GuidActivity.this,"请先阅读并同意软件许可协议",Toast.LENGTH_SHORT).show();
                    ToastUtil.show(getBaseContext(),"请先阅读并同意软件许可协议");
                    break;
                }
                Intent intent = new Intent(GuidActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_protocal:
                proView = View.inflate(this, R.layout.layout_protocal, null);
                webviewPro = proView.findViewById(R.id.webview_pro);
                webviewPro.loadUrl("file:///android_asset/products.html");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(proView).show();
                break;
            case R.id.cb_agreePro:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webviewPro!=null){
            webviewPro.clearCache(true);
        }

    }

    class VpAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mLayoutList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LinearLayout ll = (LinearLayout) ViewGroup.inflate(GuidActivity.this, layoutResIds[position], null);
            container.addView(ll);
            return ll;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
