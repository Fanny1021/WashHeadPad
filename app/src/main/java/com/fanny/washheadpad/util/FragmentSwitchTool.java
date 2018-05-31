package com.fanny.washheadpad.util;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.fanny.washheadpad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fanny on 18/4/17.
 */

public class FragmentSwitchTool implements View.OnClickListener{
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    //  private View currentClickableView;
    private View[] currentSelectedView;
    private View[] clickableViews; //传入用于被点击的view,比如是一个LinearLayout
    private List<View[]> selectedViews; //传入用于被更改资源selected状态的view[], 比如一组View[]{TextView, ImageView}
    private Class<? extends Fragment>[] fragments;
    private Bundle[] bundles;
    private int containerId;
    private boolean showAnimator;


    public FragmentSwitchTool(FragmentManager fragmentManager, int containerId) {
        super();
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void setClickableViews(View... clickableViews) {
        this.clickableViews = clickableViews;
        for (View view : clickableViews) {
            view.setOnClickListener(this);
        }
    }

    public void setSelectedViews(List<View[]> selectedViews) {
        this.selectedViews = selectedViews;
    }

    public void setShowAnimator(boolean b){
        this.showAnimator=b;
    }

    public FragmentSwitchTool addSelectedViews(View... views){
        if (selectedViews == null) {
            selectedViews = new ArrayList<View[]>();
        }
        selectedViews.add(views);
        return this;
    }

    public void setFragments(Class<? extends Fragment>... fragments) {
        this.fragments = fragments;
    }

    public void setBundles(Bundle... bundles) {
        this.bundles = bundles;
    }

    public void changeTag(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(v.getId()));

        for (int i = 0; i < clickableViews.length; i++) {
            if (v.getId() == clickableViews[i].getId()) {

                //设置当前选中按钮
                for(int j = 0; j < clickableViews.length; j++){
                    if(j==i){
                        //设置选中view为选中色
                        clickableViews[j].setBackgroundResource(R.color.green);
//                        TextView view = (TextView) selectedViews.get(j)[j];
//                        view.setTextColor(Color.parseColor("#000000"));
                    }else {
                        clickableViews[j].setBackgroundResource(R.color.white);
                    }
                }

                //过渡动画
                if (showAnimator) {
                    int exitIndex = selectedViews.indexOf(currentSelectedView);
//                  Log.e("yao", "enter : " + i + "   exit: " + exitIndex);
                    if (i > exitIndex){
                        fragmentTransaction.setCustomAnimations(R.anim.slide_down_in, R.anim.slide_up_out);
                    } else if (i < exitIndex) {
                        fragmentTransaction.setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out);
                    }
                }

                //显示当前fragment
                if (fragment == null) {
                    if (currentFragment != null) {
                        fragmentTransaction.hide(currentFragment);
                        for (View view : currentSelectedView) {
                            view.setSelected(false);
                        }
                    }
                    try {
                        fragment = fragments[i].newInstance();

                        if (bundles != null && bundles[i] != null) {
                            fragment.setArguments(bundles[i]);
                        }

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    fragmentTransaction.add(containerId, fragment, String.valueOf(clickableViews[i].getId()));
                } else if (fragment == currentFragment) {
                } else {
                    fragmentTransaction.hide(currentFragment);
                    for (View view : currentSelectedView) {
                        view.setSelected(false);
                    }
                    fragmentTransaction.show(fragment);
                }

                fragmentTransaction.commit();
                currentFragment = fragment;
                for (View view : selectedViews.get(i)) {
                    view.setSelected(true);
                }
                currentSelectedView = selectedViews.get(i);
                break;
            }

        }
    }

    @Override
    public void onClick(View v)
    {
        changeTag(v);
    }
}
