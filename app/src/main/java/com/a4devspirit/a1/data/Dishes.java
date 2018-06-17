package com.a4devspirit.a1.data;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.a4devspirit.a1.data.Adapter.DishesAdapter;
import com.a4devspirit.a1.data.Product.DishesProduct;
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

public class Dishes extends AppCompatActivity {
    ListView list_food;
    DishesAdapter adapter;
    ArrayList<DishesProduct> dishes;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference database = databaseReference.child("Category");
    DatabaseReference database1 = FirebaseDatabase.getInstance().getReference();
    EditText search;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes);
        list_food = (ListView)findViewById(R.id.list_food);
        search = (EditText)findViewById(R.id.searchdish);
        dishes = new ArrayList<>();
        adapter = new DishesAdapter(getApplicationContext(), dishes);
        this.setTitle(getIntent().getStringExtra("nameofcategory"));
        list_food.setAdapter(adapter);
        database1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("DishId")){
                    database1.child("DishId").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            id = dataSnapshot.getValue(int.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.child(getIntent().getStringExtra("nameofcategory")).child("Dishes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DishesProduct product = new DishesProduct();
                product.setId(1);
                product.setName(dataSnapshot.child("name").getValue(String.class));
                product.setPrice(dataSnapshot.child("price").getValue(String.class));
                product.setPhoto(dataSnapshot.child("photo").getValue(String.class));
                product.setDestnumber(getIntent().getStringExtra("destnumber"));
                product.setAccess(getIntent().getStringExtra("nameofcategory"));
                product.setUid(dataSnapshot.getKey());
                dishes.add(product);
                adapter.notifyDataSetChanged();
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
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final  CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basketmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(this, Basket.class);
                intent.putExtra("destnumber", getIntent().getStringExtra("destnumber"));
                startActivity(intent);
                return true;
            case R.id.item2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Вызов официанта")
                        .setMessage("Вы хотите вызвать официанта?")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                databaseReference.child("Call").child(getIntent().getStringExtra("destnumber")).setValue(timeStamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(Dishes.this, "Запрос успешно отправлен. Ожидайтесь официанта", Toast.LENGTH_LONG).show();
                                            dialogInterface.cancel();
                                        }else{
                                            Toast.makeText(Dishes.this, "Произошла ошибка", Toast.LENGTH_LONG).show();
                                            dialogInterface.cancel();
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

