package com.cikuu.pigai.activity.uiutils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.cikuu.pigai.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-05-16
 * Time: 15:18
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, final int position, String data) {
        imageView.setImageResource(R.drawable.ic_default_adimage);
        ImageLoader.getInstance().displayImage(data, imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击事件
                mOnClickCallBack.onViewPagerItemClick(position);
            }
        });
    }

    public static OnClickCallBack mOnClickCallBack;

    public interface OnClickCallBack {
        void onViewPagerItemClick(int position);
    }

}
