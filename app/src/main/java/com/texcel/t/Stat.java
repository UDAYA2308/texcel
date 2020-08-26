package com.texcel.t;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Stat extends Fragment {

    View view;
    Context context;
    Query query;
    ProgressDialog progressDialog;
    TextView t_count,t_cash,t_total,t_online,d_count,d_total,d_online,d_cash;
    SharedPreferences sharedPreferences;
    String database;
    int price;
    RecyclerView recyclerView,location_view;
    AdapterOrder adapter;
    AdapterLocation adapterLocation;
    ArrayList<Order_detail> list;
    ArrayList<Location_detail> loc;
    RecyclerView.LayoutManager layoutManager;

    public Stat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_stat, container, false);
        context=getActivity();

        sharedPreferences=context.getSharedPreferences(""+R.string.app_name,Context.MODE_PRIVATE);
        price=sharedPreferences.getInt(""+R.string.amt,0);
        database=sharedPreferences.getString("event","D");

        progressDialog=new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading.....");

        list=new ArrayList<>();
        loc=new ArrayList<>();

        adapter=new AdapterOrder(context,list);
        recyclerView=view.findViewById(R.id.recycler_view);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapterLocation=new AdapterLocation(context,loc);
        location_view=view.findViewById(R.id.recycler_view_location);
        location_view.setHasFixedSize(true);
        location_view.setLayoutManager(new LinearLayoutManager(context));
        location_view.setAdapter(adapterLocation);

        t_count=view.findViewById(R.id.t_count);
        t_cash=view.findViewById(R.id.t_cash);
        t_total=view.findViewById(R.id.t_total_amt);
        t_online=view.findViewById(R.id.t_online);

        d_count=view.findViewById(R.id.d_count);
        d_cash=view.findViewById(R.id.d_cash);
        d_total=view.findViewById(R.id.d_total_amt);
        d_online=view.findViewById(R.id.d_online);

        if(sharedPreferences.getBoolean("access",false)) {
            query = FirebaseDatabase.getInstance().getReference(database).child("Total_count");

            progressDialog.show();

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        total_count count = dataSnapshot.getValue(total_count.class);
                        t_count.setText(t_count.getText().toString() + " : " + count.count);
                        t_cash.setText(t_cash.getText().toString() + " : " + count.cash);
                        t_online.setText(t_online.getText().toString() + " : " + count.online);
                        t_total.setText(t_total.getText().toString() + " : " + count.total_amt);
                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            query =FirebaseDatabase.getInstance().getReference(database).child("Location").orderByChild("location");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loc.clear();
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        Location_detail detail=dataSnapshot1.getValue(Location_detail.class);
                        loc.add(detail);
                    }

                    adapterLocation.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            query =FirebaseDatabase.getInstance().getReference(database).child("Order_detail").orderByChild("t_shirt");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        Order_detail detail=dataSnapshot1.getValue(Order_detail.class);
                        list.add(detail);
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            query = FirebaseDatabase.getInstance().getReference(database).child("Day_count");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                    Day_count count = dataSnapshot.getValue(Day_count.class);
                    d_count.setText(d_count.getText().toString() + " : " + count.count);
                    d_cash.setText(d_cash.getText().toString() + " : " + count.cash);
                    d_online.setText(d_online.getText().toString() + " : " + count.online);
                    d_total.setText(d_total.getText().toString() + " : " + count.total_amt);
                }catch (Exception e){}
                    progressDialog.cancel();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(context,"Access Denied", Toast.LENGTH_LONG).show();

        }


        return view;
    }

}
