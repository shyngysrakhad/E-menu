package com.a4devspirit.a1.data;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.a4devspirit.a1.data.Adapter.BasketAdapter;
import com.a4devspirit.a1.data.Product.BasketProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Basket extends AppCompatActivity {
    ListView orderlistview;
    private BasketAdapter adapter;
    private List<BasketProduct> list;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference database = databaseReference;
    String dest;
    TextView sum;
    int count, finalcount;
    int a,b;
    Button order, end;
    String s;
    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        this.setTitle("Корзина");
        orderlistview = (ListView)findViewById(R.id.orderlist);
        list = new ArrayList<>();
        adapter = new BasketAdapter(getApplicationContext(), list);
        orderlistview.setAdapter(adapter);
        dest = getIntent().getStringExtra("destnumber");
        sum = (TextView)findViewById(R.id.sum);
        order = (Button)findViewById(R.id.btnorder);
        end = (Button)findViewById(R.id.btnend);
        end.setVisibility(View.INVISIBLE);
        database.child(dest).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot under: dataSnapshot.getChildren()){
                    list.add(new BasketProduct(1, dataSnapshot.getKey(), under.getKey(), Integer.parseInt(under.getValue(String.class)),dest));
                    a  = Integer.parseInt(under.getKey());
                    b = Integer.parseInt(under.getValue(String.class));
                    count = a*b;
                    finalcount = finalcount + count;
                    adapter.notifyDataSetChanged();
                }
                database.child(dest).child("Total").setValue(String.valueOf(finalcount));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.child(dest).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Total")){
                    s = dataSnapshot.child("Total").getValue(String.class);
                    sum.setText("Общая сумма: " + dataSnapshot.child("Total").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Basket.this);
                builder.setTitle("Внимание!")
                        .setMessage("После нажатия на кнопку \"Ок\" вы не сможете редактировать вашу корзину")
                        .setCancelable(true)
                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                database.child("Done").child(dest).setValue("Заказано");
                                dialogInterface.cancel();
                                order.setVisibility(View.INVISIBLE);
                                end.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("Нет, продолжать покупку",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Done").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(dest)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Basket.this);
                            builder.setTitle("Подтверждение")
                                    .setMessage("Вы успешно получили ваш заказ?")
                                    .setCancelable(true)
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                            databaseReference2.child("income").push().child(s).setValue(timeStamp);
                                            databaseReference2.child("Стол").child(dest).child("state").setValue("свободно");
                                            databaseReference2.child("Done").child(dest).removeValue();
                                            databaseReference3.child(dest).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Intent intent = new Intent(Basket.this, DestNumber.class);
                                                        startActivity(intent);
                                                    }else{
                                                        Toast.makeText(Basket.this, "Problem", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    })
                                    .setNegativeButton("Нет",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }else{
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    public void onBackPressed(){
        databaseReference.child("Done").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(dest)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Basket.this);
                    builder.setTitle("Подтверждение")
                            .setMessage("Вы успешно получили ваш заказ?")
                            .setCancelable(true)
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                    databaseReference2.child("income").push().child(s).setValue(timeStamp);
                                    databaseReference2.child("Стол").child(dest).child("state").setValue("свободно");
                                    databaseReference2.child("Done").child(dest).removeValue();
                                    databaseReference3.child(dest).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(Basket.this, DestNumber.class);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(Basket.this, "Problem", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
