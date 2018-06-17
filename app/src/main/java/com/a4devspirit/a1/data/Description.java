package com.a4devspirit.a1.data;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a4devspirit.a1.data.Adapter.DescriptionAdapter;
import com.a4devspirit.a1.data.Product.DescriptionProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class Description extends AppCompatActivity {
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference database = databaseReference;
    TextView description, recommendation, price;
    ImageView photo;
    Button recommend, comment;
    ListView listcomments;
    DescriptionAdapter adapter;
    ArrayList<DescriptionProduct> list;
    String access;
    String uid;
    TextView nocomment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        description = (TextView) findViewById(R.id.txtdescription);
        photo = (ImageView)findViewById(R.id.fullphoto);
        access = getIntent().getStringExtra("access");
        nocomment = (TextView)findViewById(R.id.nocomment);
        uid = getIntent().getStringExtra("uid");
        recommend = (Button)findViewById(R.id.buttonrecommend);
        price = (TextView)findViewById(R.id.txtprice);
        recommendation = (TextView)findViewById(R.id.txtrecommendation);
        comment = (Button)findViewById(R.id.commentdialog);
        listcomments = (ListView) findViewById(R.id.listcomments);
        list = new ArrayList<>();
        adapter = new DescriptionAdapter(this, list);
        listcomments.setAdapter(adapter);
        database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).child("description").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stringrdescription = dataSnapshot.getValue(String.class);
                description.setText(stringrdescription);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Recommend")){
                    database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).child("Recommend").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final int i = Integer.valueOf(dataSnapshot.getValue(String.class));
                            recommend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).child("Recommend").setValue(Integer.toString(i+1));
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    recommend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).child("Recommend").setValue("1");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Recommend")){
                    database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).child("Recommend").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.getValue(String.class);
                            recommendation.setText("Рекомендовано: "+s+" раз");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else recommendation.setText("Рекомендовано: 0 раз");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        description.post(new Runnable() {
            @Override
            public void run() {
                int width = description.getWidth();
                int height = description.getHeight();
                Picasso.with(Description.this).load(getIntent().getStringExtra("photourl")).placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .resize(width, height*2)
                        .centerInside()
                        .into(photo, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(Description.this, "Connection problem", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).child("description").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                description.setText("Описание: " + s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        price.setText("Цена: " + getIntent().getStringExtra("price") + " тенге");
                    database.child("Category").child(getIntent().getStringExtra("access")).child("Dishes").child(getIntent().getStringExtra("uid")).child("Comments").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.getChildrenCount()==0){
                                nocomment.setVisibility(View.VISIBLE);
                                listcomments.setVisibility(View.GONE);
                            }else{
                                nocomment.setVisibility(View.GONE);
                                listcomments.setVisibility(View.VISIBLE);
                            }
                            for (DataSnapshot under: dataSnapshot.getChildren()){
                                DescriptionProduct list2 = new DescriptionProduct();
                                list2.setId(1);
                                list2.setComment(under.getValue(String.class));
                                list2.setUsername(under.getKey());
                                list.add(list2);
                                adapter.notifyDataSetChanged();
                            }
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
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Description.this);
                dialog.setContentView(R.layout.description_custom_dialog);
                final EditText name = (EditText)dialog.findViewById(R.id.edit_username);
                final EditText editcomment = (EditText)dialog.findViewById(R.id.editcomment1);
                Button comment1 = (Button) dialog.findViewById(R.id.buttoncomment);
                dialog.show();
                comment1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (name.getText().toString().equals("") || editcomment.getText().toString().equals("")){
                            Toast.makeText(Description.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                        }else{
                            databaseReference.child("Category").child(access).child("Dishes").child(uid).child("Comments").push().child(name.getText().toString()).setValue(editcomment.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(Description.this, "Ваш отзыв успешно добавлен", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(Description.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        this.setTitle(getIntent().getStringExtra("name"));
    }
}
