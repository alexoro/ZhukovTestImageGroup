package com.example.testimagegroup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vk.imagesviewgroup.ImagesViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 20.06.2016.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        FrameLayout container = (FrameLayout) findViewById(R.id.container);

        ImagesViewGroup imagesViewGroup = new ImagesViewGroup(this);
        imagesViewGroup.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imagesViewGroup.setBackgroundColor(Color.YELLOW);
        imagesViewGroup.setSpace((int) convertDpToPixels(this, 3));
        container.addView(imagesViewGroup);

        imagesViewGroup.setMaxWidth(1080);
        imagesViewGroup.setMaxHeight(1058);
        //imagesViewGroup.setPadding(48, 48, 48, 48);
        imagesViewGroup.setViewFactory(new ImagesViewGroup.ViewFactory() {
            @Override
            public View onCreateView(ImagesViewGroup parent, Object tag) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(false);
                imageView.setBackgroundColor(Color.RED);
                return imageView;
            }

            @Override
            public void onBindView(ImagesViewGroup parent, Object tag, View view) {
                ImageView imageView = (ImageView) view;
                String imageUrl = (String) tag;
                Glide.with(parent.getContext()).load(imageUrl).into(imageView);
            }

            @Override
            public void onDestroyView(ImagesViewGroup parent, Object tag, View view) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(null);
            }

            @Override
            public void onGroupLayoutDone(ImagesViewGroup parent) {
                //ImagesViewGroup.EdgeView edgeView = new ImagesViewGroup.EdgeView();
                //parent.findEdgeViews(edgeView);
            }
        });

        List<ImagesViewGroup.ItemInfo> imagesInfo = new ArrayList<>(9);
        imagesInfo.add(new ImagesViewGroup.ItemInfo(721, 1080, "https://pp.vk.me/c630023/v630023555/39745/1gKjz1HVFUE.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(2560, 1709, "https://pp.vk.me/c630023/v630023555/39758/_aKdId3wUx4.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(1442, 2160, "https://pp.vk.me/c630521/v630521555/354fe/RaLil7TV10Y.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(721, 1080, "https://pp.vk.me/c630023/v630023555/3974e/9m7kE9xOIwM.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(1442, 2160, "https://pp.vk.me/c630023/v630023555/398f6/SpIxtpZzhf4.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(1441, 2160, "https://pp.vk.me/c630521/v630521555/354f4/9OEfjB8bXS0.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(1442, 2160, "https://pp.vk.me/c630023/v630023555/3990a/UXdbAqJaLXA.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(1618, 1080, "https://pp.vk.me/c630023/v630023555/39900/4QXJx8hQ_BM.jpg"));
        imagesInfo.add(new ImagesViewGroup.ItemInfo(1618, 1080, "https://pp.vk.me/c630023/v630023555/39914/-PBnLTqHyzE.jpg"));
        imagesViewGroup.setItems(imagesInfo);
    }

    public static float convertDpToPixels(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

}