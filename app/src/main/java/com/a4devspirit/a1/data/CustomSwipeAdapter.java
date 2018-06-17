package com.a4devspirit.a1.data;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 1 on 31.07.2017.
 */

public class CustomSwipeAdapter extends PagerAdapter{
    private int[] image_resources = {R.drawable.ic_emenu, R.drawable.ic_time, R.drawable.ic_detail, R.drawable.ic_choose, R.drawable.ic_interface};
    private String[] image_title = {"Почему электронное меню?", "Быстрое обслуживание","Фотографий и описаний, просмотр отзывов и рекомендации к каждому артикулу", "Легкий и быстрый выбор вида и количества данного артикула", "Интуитивный интерфейс"};
    private Context context;
    private LayoutInflater layoutInflater;
    public CustomSwipeAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        ImageView imageView = (ImageView)item_view.findViewById(R.id.imageView);
        TextView textView = (TextView) item_view.findViewById(R.id.image_count);
        TextView title = (TextView)item_view.findViewById(R.id.image_title);
        title.setText(image_title[position]);
        imageView.setImageResource(image_resources[position]);
        textView.setText(position+1 + "/5");
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
