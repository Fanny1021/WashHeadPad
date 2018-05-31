package com.fanny.washheadpad.util;

import android.content.Context;
import android.media.tv.TvTrackInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanny.washheadpad.MyApplication;
import com.fanny.washheadpad.R;

import butterknife.BindView;

/**
 * Created by Fanny on 18/4/18.
 */

public class ToastUtil {


    public static void show(Context context ,Object object) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.diy_toast, null, false);
        TextView tvToast=view.findViewById(R.id.tv_toast);
        tvToast.setText(object.toString());

        Toast toast=new Toast(context);
//        toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.BOTTOM,0,0);
        toast.setGravity(Gravity.BOTTOM,0,10);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
