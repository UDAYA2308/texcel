package com.texcel.t;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class Due_list extends Fragment {


    View view;
    Context context;
    TableLayout tableLayout;
    Query query;
    SeekBar seekBar;
    TextView count;
    SharedPreferences sharedPreferences;
    String database;
    StringBuilder data;
    int price;
    TextView export;

    ProgressDialog progressDialog;

    public Due_list() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

            view = inflater.inflate(R.layout.fragment_due_list, container, false);

            context = getActivity();

        try {

            tableLayout = view.findViewById(R.id.table);
            tableLayout.setStretchAllColumns(true);
            seekBar = view.findViewById(R.id.filter);
            count = view.findViewById(R.id.count);
            export = view.findViewById(R.id.export_);

            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading.....");

            sharedPreferences = context.getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
            price = 500;
            database = sharedPreferences.getString("event", "D");

           data = new StringBuilder();

            seekBar.setMax(price);

            export.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    export_();
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                    TextView textView = view.findViewById(R.id.seek_value);
                    textView.setText("" + seekBar.getProgress());
                    textView.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    if (sharedPreferences.getBoolean("access", false))
                        get_values();
                    else {
                        Toast.makeText(context, "Access Denied", Toast.LENGTH_LONG).show();

                    }

                }
            });


            if (sharedPreferences.getBoolean("access", false))
                get_values();
            else {
                Toast.makeText(context, "Access Denied", Toast.LENGTH_LONG).show();

            }

        }
         catch (Exception e)
            {

                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

            }

        return view;
    }

    public  void get_values()
    {
        progressDialog.show();

        query=FirebaseDatabase.getInstance().getReference(database).child("Order").orderByChild("due");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;

                while(tableLayout.getChildCount()>1)
                {
                    tableLayout.removeView(tableLayout.getChildAt(tableLayout.getChildCount()-1));
                }

                data.append("Registration Number,Name,Phone Number,T-Shirt Model,Size,Due Amount");
                for(DataSnapshot data1:dataSnapshot.getChildren())
                {
                    sell_detail detail=data1.getValue(sell_detail.class);

                    {
                        if(detail.due_amount>=seekBar.getProgress())
                            try {

                                TextView name=new TextView(context);
                                TextView reg=new TextView(context);
                                TextView phone=new TextView(context);
                                TextView due_amt=new TextView(context);
                                TextView t_shirt=new TextView(context);
                                TextView size=new TextView(context);

                                TableRow  row=new TableRow(context);
                                if(i%2!=0)
                                    row.setBackgroundColor(Color.rgb(204,252,236));
                                else
                                    row.setBackgroundColor(Color.rgb(255,255,255));
                                row.setLayoutParams( new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                row.setId(i+20);
                                size.setText(detail.size);
                                size.setGravity(Gravity.CENTER);
                                reg.setText(detail.regno);
                                reg.setGravity(Gravity.CENTER);
                                name.setText(detail.name);
                                name.setGravity(Gravity.CENTER);
                                phone.setText(detail.phone);
                                phone.setGravity(Gravity.CENTER);
                                due_amt.setText(""+detail.due_amount);
                                due_amt.setGravity(Gravity.CENTER);
                                t_shirt.setText(detail.t_shirt);
                                t_shirt.setGravity(Gravity.CENTER);

                                data.append("\n"+detail.regno+","+detail.name+","+detail.phone+","+detail.t_shirt+","+detail.size+","+detail.due_amount+"");

                                row.addView(reg);
                                row.addView(name);
                                row.addView(phone);
                                row.addView(t_shirt);
                                row.addView(size);
                                row.addView(due_amt);



                                tableLayout.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                i++;
                            }
                            catch (Exception e)
                            {

                                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                    }


                }

                count.setText("Total Count : "+i);


                if(i==0)
                {
                   Toast.makeText(context,"No Record Found",Toast.LENGTH_LONG).show();
                }

                progressDialog.cancel();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public void export_(){

        try{
            //saving the file into device
            FileOutputStream out = context.openFileOutput("Due_List.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            File filelocation = new File(context.getFilesDir(), "Due_List.csv");
            Uri path = FileProvider.getUriForFile(context, "com.texcel.exportcsv.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send To"));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
