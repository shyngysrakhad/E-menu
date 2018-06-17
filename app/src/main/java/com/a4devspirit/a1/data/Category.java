package com.a4devspirit.a1.data;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.a4devspirit.a1.data.Adapter.CategoryAdapter;
import com.a4devspirit.a1.data.Product.CategoryProduct;
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

public class Category extends AppCompatActivity {
    ListView categorylistView;
    private CategoryAdapter adapter;
    private ArrayList<CategoryProduct> list;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference database = databaseReference.child("Category");
    EditText search;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        categorylistView = (ListView)findViewById(R.id.categorylistview);
        search = (EditText)findViewById(R.id.searchcategory);
        list = new ArrayList<>();
        adapter = new CategoryAdapter(getApplicationContext(), list);
        categorylistView.setAdapter(adapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("CategoryId")){
                    id = dataSnapshot.child("CategoryId").getValue(int.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this.setTitle("Категории. Ваш стол №" + getIntent().getStringExtra("destnumber"));
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CategoryProduct product = new CategoryProduct();
                product.setId(1);
                product.setAccess(dataSnapshot.getKey());
                product.setDestnumber(getIntent().getStringExtra("destnumber"));
                product.setCategoryname(dataSnapshot.child("name").getValue(String.class));
                product.setCategoryphoto(dataSnapshot.child("photo").getValue(String.class));
                product.setDishcount(String.valueOf(dataSnapshot.child("Dishes").getChildrenCount()));
                list.add(product);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                list.remove(id);
                adapter.notifyDataSetChanged();
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
                                            Toast.makeText(Category.this, "Запрос успешно отправлен. Ожидайтесь официанта", Toast.LENGTH_LONG).show();
                                            dialogInterface.cancel();
                                        }else{
                                            Toast.makeText(Category.this, "Произошла ошибка", Toast.LENGTH_LONG).show();
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
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Вы завершили покупку?")
                .setMessage("Вы закончили вашу покупку?")
                .setCancelable(true)
                .setPositiveButton("Да, я завершил покупку", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Category.this, DestNumber.class);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Стол").child(getIntent().getStringExtra("destnumber")).child("state").setValue("свободно");
                        databaseReference.child(getIntent().getStringExtra("destnumber")).removeValue();
                        databaseReference.child("Done").child(getIntent().getStringExtra("destnumber")).removeValue();
                        startActivity(intent);
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
}
