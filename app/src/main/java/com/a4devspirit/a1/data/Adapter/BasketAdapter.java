package com.a4devspirit.a1.data.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.a4devspirit.a1.data.Product.BasketProduct;
import com.a4devspirit.a1.data.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by 1 on 02.07.2017.
 */

public class BasketAdapter extends BaseAdapter{
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference database = databaseReference;
    private Context context;
    private List<BasketProduct> orderlist;
    private int a;
    private int count;
    private int cocount;
    public BasketAdapter(Context context, List<BasketProduct> orderlist){
        this.context = context;
        this.orderlist = orderlist;
    }
    @Override
    public int getCount() {
        return orderlist.size();
    }

    @Override
    public Object getItem(int i) {
        return orderlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.custom_basket,null);
        final TextView amount = (TextView)v.findViewById(R.id.amountoforder);
        final Button add = (Button)v.findViewById(R.id.addorder);
        amount.setText(String.valueOf(orderlist.get(i).getCount()));
        TextView name = (TextView)v.findViewById(R.id.nameoffood);
        final Button remove = (Button)v.findViewById(R.id.removeorder);
        final TextView price = (TextView)v.findViewById(R.id.priceoforder);
        final TextView cancel = (TextView)v.findViewById(R.id.cancel);
        database.child("Done").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(orderlist.get(i).getDestnumber())){
                    add.setEnabled(false);
                    remove.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.child(orderlist.get(i).getDestnumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Total")){
                    cocount = Integer.parseInt(dataSnapshot.child("Total").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderlist.get(i).setCount(Integer.parseInt(amount.getText().toString()));
                a = orderlist.get(i).getCount();
                a++;
                database.child(orderlist.get(i).getDestnumber()).child(orderlist.get(i).getName()).child(orderlist.get(i).getPrice()).setValue(String.valueOf(a));
                amount.setText(String.valueOf(a));
                count = cocount + Integer.parseInt(orderlist.get(i).getPrice());
                database.child(orderlist.get(i).getDestnumber()).child("Total").setValue(String.valueOf(count));
                remove.setEnabled(true);
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderlist.get(i).setCount(Integer.parseInt(amount.getText().toString()));
                a = orderlist.get(i).getCount();
                a--;
                database.child(orderlist.get(i).getDestnumber()).child(orderlist.get(i).getName()).child(orderlist.get(i).getPrice()).setValue(String.valueOf(a));
                count = cocount - Integer.parseInt(orderlist.get(i).getPrice());
                database.child(orderlist.get(i).getDestnumber()).child("Total").setValue(String.valueOf(count));
                amount.setText(String.valueOf(a));
                if (a==0){
                    database.child(orderlist.get(i).getDestnumber()).child(orderlist.get(i).getName()).child(orderlist.get(i).getPrice()).removeValue();
                    add.setVisibility(View.GONE);
                    remove.setVisibility(View.GONE);
                    amount.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                }else{
                    add.setVisibility(View.VISIBLE);
                    remove.setVisibility(View.VISIBLE);
                    amount.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                }
            }
        });
        name.setText(orderlist.get(i).getName());
        price.setText(orderlist.get(i).getPrice() + " тенге");
        v.setTag(orderlist.get(i).getId());
        return v;
    }
}
