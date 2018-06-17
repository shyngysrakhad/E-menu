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

import com.a4devspirit.a1.data.Description;
import com.a4devspirit.a1.data.Product.DishesProduct;
import com.a4devspirit.a1.data.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DishesAdapter extends BaseAdapter implements Filterable{
    private ValueFilter valueFilter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference database = databaseReference;
    private DatabaseReference data2 = FirebaseDatabase.getInstance().getReference();
    private Context context;
    private int a;
    private ArrayList<DishesProduct> _Contacts;
    private ArrayList<DishesProduct> mStringFilterList;
    private LayoutInflater inflater;
    public DishesAdapter(Context context, ArrayList<DishesProduct> _Contacts) {
        super();
        this.context = context;
        this._Contacts = _Contacts;
        mStringFilterList = _Contacts;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getFilter();
    }

    @Override
    public int getCount() {
        return _Contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return _Contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    public class ViewHolder {
        TextView name, price, amount;
        Button add, remove, basket, indetail;
        LinearLayout count, layout1;
        ImageView photo;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.custom_dishes, null);
            holder.name = (TextView)convertView.findViewById(R.id.text_name);
            holder.price = (TextView)convertView.findViewById(R.id.text_price);
            holder.amount = (TextView)convertView.findViewById(R.id.amount);
            holder.add = (Button)convertView.findViewById(R.id.btnadd);
            holder.remove = (Button)convertView.findViewById(R.id.btnremovecategory);
            holder.basket = (Button)convertView.findViewById(R.id.basket);
            holder.indetail = (Button)convertView.findViewById(R.id.indetail);
            holder.count = (LinearLayout)convertView.findViewById(R.id.count);
            holder.layout1 = (LinearLayout)convertView.findViewById(R.id.firstlayout);
            holder.photo = (ImageView)convertView.findViewById(R.id.foodphoto);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();
            holder.indetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Description.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("photourl", _Contacts.get(position).getPhoto());
                    intent.putExtra("name", _Contacts.get(position).getName());
                    intent.putExtra("price", _Contacts.get(position).getPrice());
                    intent.putExtra("access", _Contacts.get(position).getAccess());
                    intent.putExtra("uid", _Contacts.get(position).getUid());
                    context.startActivity(intent);
                }
            });
            data2.child("Category").child(_Contacts.get(position).getAccess()).child("Dishes").child(_Contacts.get(position).getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.name.setText(dataSnapshot.child("name").getValue(String.class));
                    holder.price.setText(dataSnapshot.child("price").getValue(String.class) + " тенге");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    holder.name.setText("Данной продукт был удалён");
                }
            });
        holder.layout1.post(new Runnable(){
            public void run(){
                int height = holder.layout1.getHeight();
                Picasso.with(context).load(_Contacts.get(position).getPhoto()).placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .resize(height, height)
                        .centerCrop()
                        .into(holder.photo, new com.squareup.picasso.Callback() {
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
            holder.count.setVisibility(View.GONE);
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(_Contacts.get(position).getDestnumber())){
                        String s = dataSnapshot.child(_Contacts.get(position).getDestnumber()).child(_Contacts.get(position).getName()).child(_Contacts.get(position).getPrice()).getValue(String.class);
                        holder.amount.setText(s);
                        if (holder.amount.getText().toString().equals("")){
                            holder.basket.setVisibility(View.VISIBLE);
                            holder.count.setVisibility(View.GONE);
                        }else{
                            holder.basket.setVisibility(View.GONE);
                            holder.count.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                            p.weight = 1;
                            holder.indetail.setLayoutParams(p);
                            holder.count.setLayoutParams(p);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            holder.basket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    database.child(_Contacts.get(position).getDestnumber()).child(_Contacts.get(position).getName()).child(_Contacts.get(position).getPrice()).setValue("1");
                    holder.basket.setVisibility(View.GONE);
                    holder.count.setVisibility(View.VISIBLE);

                }
            });
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _Contacts.get(position).setCount(Integer.parseInt(holder.amount.getText().toString()));
                    a = _Contacts.get(position).getCount();
                    a++;
                    database.child(_Contacts.get(position).getDestnumber()).child(_Contacts.get(position).getName()).child(_Contacts.get(position).getPrice()).setValue(String.valueOf(a));
                }
            });
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _Contacts.get(position).setCount(Integer.parseInt(holder.amount.getText().toString()));
                    a = _Contacts.get(position).getCount();
                    a--;
                    if (a == 0){
                        holder.count.setVisibility(View.GONE);
                        holder.basket.setVisibility(View.VISIBLE);
                        database.child(_Contacts.get(position).getDestnumber()).child(_Contacts.get(position).getName()).child(_Contacts.get(position).getPrice()).removeValue();
                        holder.amount.setText("");
                    }else{
                        database.child(_Contacts.get(position).getDestnumber()).child(_Contacts.get(position).getName()).child(_Contacts.get(position).getPrice()).setValue(String.valueOf(a));
                    }
                }
            });
            return convertView;
        }
    @Override
    public Filter getFilter() {
        if(valueFilter==null) {

            valueFilter=new ValueFilter();
        }

        return valueFilter;
    }
    private class ValueFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0){
                List<DishesProduct> filterList = new ArrayList<>();
                for (int i = 0; i<mStringFilterList.size();i++){
                    if ((mStringFilterList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())){
                        DishesProduct dishesProduct = new DishesProduct();
                        dishesProduct.setName(mStringFilterList.get(i).getName());
                        dishesProduct.setId(mStringFilterList.get(i).getId());
                        dishesProduct.setUid(mStringFilterList.get(i).getUid());
                        dishesProduct.setAccess(mStringFilterList.get(i).getAccess());
                        dishesProduct.setDestnumber(mStringFilterList.get(i).getDestnumber());
                        dishesProduct.setPhoto(mStringFilterList.get(i).getPhoto());
                        dishesProduct.setPrice(mStringFilterList.get(i).getPrice());
                        filterList.add(dishesProduct);
                    }
                }
                results.count=filterList.size();
                results.values=filterList;
            }else{
                results.count=mStringFilterList.size();
                results.values=mStringFilterList;
            }
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            _Contacts=(ArrayList<DishesProduct>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}