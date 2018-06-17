package com.a4devspirit.a1.data.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a4devspirit.a1.data.Dishes;
import com.a4devspirit.a1.data.Product.CategoryProduct;
import com.a4devspirit.a1.data.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PKTL on 6/14/17.
 */

public class CategoryAdapter extends BaseAdapter implements Filterable{
    private ValueFilter valueFilter;
    private Context context;
    private ArrayList<CategoryProduct> categorylist;
    private ArrayList<CategoryProduct> list;
    private LayoutInflater inflater;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    public CategoryAdapter(Context context, ArrayList<CategoryProduct> categorylist) {
        super();
        this.context = context;
        this.categorylist = categorylist;
        list = categorylist;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getFilter();
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
        Button next;
        ImageView categoryimage;
        TextView categoryname, dishcount;
        LinearLayout categorylayout;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view==null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.custom_category, null);
            holder.next = (Button) view.findViewById(R.id.category_next);
            holder.categoryimage = (ImageView) view.findViewById(R.id.photo_category);
            holder.categoryname = (TextView)view.findViewById(R.id.name_category);
            holder.dishcount = (TextView)view.findViewById(R.id.dishcount);
            holder.categorylayout = (LinearLayout)view.findViewById(R.id.layout_category);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        databaseReference.child("Category").child(categorylist.get(i).getAccess()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.categoryname.setText(dataSnapshot.child("name").getValue(String.class));
                if  (dataSnapshot.hasChild("Dishes")){
                        holder.dishcount.setText(String.valueOf(dataSnapshot.child("Dishes").getChildrenCount()) + " товаров");
                }else{
                    holder.dishcount.setText("Пока нет продуктов");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.categorylayout.post(new Runnable() {
            @Override
            public void run() {
                int height = holder.categorylayout.getHeight();
                Picasso.with(context).load(categorylist.get(i).getCategoryphoto()).placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .resize(height, height)
                        .centerCrop()
                        .into(holder.categoryimage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(context, "Connection problem", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        /*holder.categoryname.setText(categorylist.get(i).getCategoryname());
        holder.dishcount.setText(categorylist.get(i).getDishcount() + " товаров");
        holder.categorylayout.post(new Runnable() {
            @Override
            public void run() {
                int height = holder.categorylayout.getHeight();
                Picasso.with(context).load(categorylist.get(i).getCategoryphoto()).placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .resize(height, height)
                        .centerCrop()
                        .into(holder.categoryimage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(context, "Connection problem", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });*/
        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Dishes.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("nameofcategory", categorylist.get(i).getAccess());
                intent.putExtra("destnumber", categorylist.get(i).getDestnumber());
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter==null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }
    private class ValueFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0){
                List<CategoryProduct> filterList = new ArrayList<>();
                for (int i = 0; i<list.size();i++){
                    if ((list.get(i).getCategoryname().toUpperCase()).contains(constraint.toString().toUpperCase())){
                        CategoryProduct categoryProduct = new CategoryProduct();
                        categoryProduct.setId(list.get(i).getId());
                        categoryProduct.setCategoryname(list.get(i).getCategoryname());
                        categoryProduct.setAccess(list.get(i).getAccess());
                        categoryProduct.setCategoryphoto(list.get(i).getCategoryphoto());
                        categoryProduct.setDestnumber(list.get(i).getDestnumber());
                        filterList.add(categoryProduct);
                    }
                }
                results.count=filterList.size();
                results.values=filterList;
            }else{
                results.count=list.size();
                results.values=list;
            }
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            categorylist=(ArrayList<CategoryProduct>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
