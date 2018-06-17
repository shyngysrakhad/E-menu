package com.a4devspirit.a1.data;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.R.attr.delay;

public class DestNumber extends AppCompatActivity {
    EditText secretcode;
    Spinner spinner;
    Button reg;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference database = databaseReference;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> updated = new ArrayList<>();
    String ss;
    ViewPager viewPager;
    CustomSwipeAdapter customSwipeAdapter;
    private Handler handler;
    private int page = 0;
    private int delay = 3000; //milliseconds
    Runnable runnable = new Runnable() {
        public void run() {
            if (customSwipeAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dest_number);
        this.setTitle("Регистрация стола");
        secretcode = (EditText)findViewById(R.id.secretcode);
        spinner = (Spinner)findViewById(R.id.spinner1);
        reg = (Button)findViewById(R.id.reg);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        customSwipeAdapter = new CustomSwipeAdapter(this);
        viewPager.setAdapter(customSwipeAdapter);
        handler = new Handler();
        final ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(areasAdapter);
        spinner.setPrompt("Title");
        spinner.setSelection(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        database.child("Стол").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    list.add(dataSnapshot.getKey() + " (" + dataSnapshot.child("state").getValue(String.class)+")");
                    updated.add(dataSnapshot.getKey());
                    areasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index = updated.indexOf(dataSnapshot.getKey());
                list.set(index, dataSnapshot.getKey() +" (" + dataSnapshot.child("state").getValue(String.class) + ")");
                areasAdapter.notifyDataSetChanged();
                if (dataSnapshot.child("state").getValue(String.class).equals("свободно")){
                    secretcode.setEnabled(true);
                }else{
                    secretcode.setEnabled(false);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                list.remove(dataSnapshot.getValue(String.class) + " (" + dataSnapshot.child("state").getValue(String.class)+")");
                areasAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = adapterView.getSelectedItem().toString();
                ss = s.substring(0,2);
                if (s.contains("занято")){
                    Toast.makeText(DestNumber.this, "Вы выбрали занятой стол, пожалуйста, выберите другой", Toast.LENGTH_SHORT).show();
                    secretcode.setEnabled(false);
                    reg.setEnabled(false);
                }else secretcode.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        secretcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()<=5){
                    reg.setEnabled(false);
                } else {
                    reg.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()<=5){
                    reg.setEnabled(false);
                } else {
                    reg.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().length()<=5){
                    reg.setEnabled(false);
                } else {
                    reg.setEnabled(true);
                }
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner.getSelectedItem().toString().contains("занято")){
                    Toast.makeText(DestNumber.this, "Извините, но вы выбрали занятой стол", Toast.LENGTH_SHORT).show();
                }else {
                    final ProgressDialog progressdialog = new ProgressDialog(DestNumber.this);
                    progressdialog.setTitle("Loading");
                    progressdialog.show();
                    database.child("Стол").child(ss.trim()).child("secretcode").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class).equals(secretcode.getText().toString())){
                                progressdialog.dismiss();
                                database.child("Стол").child(ss.trim()).child("state").setValue("занято");
                                Intent intent = new Intent(DestNumber.this, Category.class);
                                intent.putExtra("destnumber", ss.trim());
                                startActivity(intent);
                            }else{
                                progressdialog.dismiss();
                                Toast.makeText(DestNumber.this, "Не удалось", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
