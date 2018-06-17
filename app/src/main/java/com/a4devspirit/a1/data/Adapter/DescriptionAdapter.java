package com.a4devspirit.a1.data.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a4devspirit.a1.data.Product.DescriptionProduct;
import com.a4devspirit.a1.data.R;

import java.util.ArrayList;

/**
 * Created by 1 on 26.07.2017.
 */

public class DescriptionAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<DescriptionProduct> categorylist;
    private LayoutInflater inflater;
    public DescriptionAdapter(Context context, ArrayList<DescriptionProduct> categorylist){
        super();
        this.context = context;
        this.categorylist = categorylist;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return categorylist.size();
    }

    @Override
    public Object getItem(int i) {
        return categorylist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    public class ViewHolder {
        TextView user, comment;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view==null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.custom_description, null);
            holder.user = (TextView) view.findViewById(R.id.username);
            holder.comment = (TextView) view.findViewById(R.id.usercomment);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.comment.setText(categorylist.get(i).getComment());
        holder.user.setText(categorylist.get(i).getUsername());
        return view;
    }
}
