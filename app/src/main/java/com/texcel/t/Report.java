package com.texcel.t;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class Report extends Fragment {

    View view;
    Context context;
    TableLayout tableLayout;
    ProgressDialog progressDialog;
    Query query;
    TextView count,export;
    Spinner record,_location;
    Button get;
    String date_pick;
    SharedPreferences sharedPreferences;
    String database;
    int price;
    StringBuilder data;
    DatePickerDialog.OnDateSetListener dateSetListener;


    public Report() {
        // Required empty public constructor


    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_report, container, false);

        context=getActivity();


        progressDialog=new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading.....");

        data=new StringBuilder();

        sharedPreferences=context.getSharedPreferences(""+R.string.app_name,Context.MODE_PRIVATE);
        price=sharedPreferences.getInt(""+R.string.amt,0);
        database=sharedPreferences.getString("event","D");


        if(savedInstanceState!=null)
        {
            progressDialog.show();
            if(sharedPreferences.getBoolean("access",false))
            get_record();
            else
            {
                Toast.makeText(context,"Access Denied", Toast.LENGTH_LONG).show();

            }
        }

        tableLayout=view.findViewById(R.id.table);
        tableLayout.setStretchAllColumns(true);
        count=view.findViewById(R.id.count);
        record=view.findViewById(R.id.record);
        export=view.findViewById(R.id.export_);
        get=view.findViewById(R.id.get_record);
        _location=view.findViewById(R.id.location_);

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export();
            }
        });

        record.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(dayOfMonth<10)
                date_pick="0"+dayOfMonth+"/";
                else
                    date_pick=dayOfMonth+"/";



                if(month+1<10)
                    date_pick+="0"+(month+1);
                else
                    date_pick+=month+1+"/";

                date_pick+=year;
            }
        };

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sharedPreferences.getBoolean("access",false)) {
                    progressDialog.show();
                    get_record();
                }
                else
                {
                    Toast.makeText(context,"Access Denied", Toast.LENGTH_LONG).show();

                }
            }
        });

        return view;
    }

    private void get_record() {


        if(_location.getSelectedItem().toString().equals("All"))
        query= FirebaseDatabase.getInstance().getReference(database).child("Order").orderByChild("date");
        else
            query= FirebaseDatabase.getInstance().getReference(database).child("Order").orderByChild("location").equalTo(_location.getSelectedItem().toString());
            query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;

                while(tableLayout.getChildCount()>1)
                {
                    tableLayout.removeView(tableLayout.getChildAt(tableLayout.getChildCount()-1));
                }
                data=new StringBuilder();
                data.append("S.no,Registration Number,Name,Phone Number,T-Shirt Model,Quantity,Size,Amount Paid,Due Amount,Mode of Payment,Date,Time,Location,Seller,Delivered");

                for(DataSnapshot data1:dataSnapshot.getChildren()) {
                    sell_detail detail = data1.getValue(sell_detail.class);

                    if (record.getSelectedItem().toString().equals("Total")) {
                        try {

                            add_row(detail,i);
                            i++;

                        } catch (Exception e) {

                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                    else if(record.getSelectedItem().toString().equals("Date"))
                    {
                            if(detail.date.equals(date_pick))
                                try {
                                        add_row(detail,i);
                                        i++;
                            } catch (Exception e) {

                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    }
                    else
                    {

                        Date dd = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                       String strDate= formatter.format(dd);
                        if(detail.date.equals(strDate))
                            try {

                                add_row(detail,i);
                                i++;

                            } catch (Exception e) {

                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position",record.getSelectedItemPosition());
    }

    public void add_row(sell_detail detail,int i)
    {
        TextView amt_paid = new TextView(context);
        TextView date = new TextView(context);
        TextView delivery = new TextView(context);
        TextView due_amt = new TextView(context);
        TextView mode = new TextView(context);
        TextView name = new TextView(context);
        TextView phone = new TextView(context);
        TextView reg = new TextView(context);
        TextView t_shirt = new TextView(context);
        TextView size = new TextView(context);
        TextView time = new TextView(context);
        TextView location=new TextView(context);
        TextView reg_name=new TextView(context);


        TableRow row = new TableRow(context);
        if(i%2!=0)
            row.setBackgroundColor(Color.rgb(204,252,236));
        else
            row.setBackgroundColor(Color.rgb(255,255,255));
        row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setId(i + 20);
        size.setText(detail.size);
        size.setGravity(Gravity.CENTER);
        reg.setText(detail.regno);
        reg.setGravity(Gravity.CENTER);
        name.setText(detail.name);
        name.setGravity(Gravity.CENTER);
        phone.setText(detail.phone);
        phone.setGravity(Gravity.CENTER);
        due_amt.setText("" + detail.due_amount);
        due_amt.setGravity(Gravity.CENTER);
        t_shirt.setText(detail.t_shirt);
        t_shirt.setGravity(Gravity.CENTER);
        amt_paid.setText("" + detail.amount_paid);
        amt_paid.setGravity(Gravity.CENTER);
        time.setText("" + detail.timestamp);
        time.setGravity(Gravity.CENTER);
        date.setText("" + detail.date);
        date.setGravity(Gravity.CENTER);
        mode.setText("" + detail.mode_of_payment);
        mode.setGravity(Gravity.CENTER);
        delivery.setText("" + detail.delivery);
        delivery.setGravity(Gravity.CENTER);
        location.setText(detail.location);
        location.setGravity(Gravity.CENTER);
        reg_name.setText(detail.reg_name);
        reg_name.setGravity(Gravity.CENTER);

        String count=detail.size;
        int cc=count.length()-count.replace(" ","").length();


        data.append("\n"+(i+1)+","+detail.regno+","+detail.name+","+detail.phone+","+detail.t_shirt+","+cc+","+detail.size+","+detail.amount_paid+","+detail.due_amount+","+detail.mode_of_payment+","+detail.date+","+detail.timestamp+","+detail.location+","+detail.reg_name+","+detail.delivery);

        row.addView(reg);
        row.addView(name);
        row.addView(phone);
        row.addView(t_shirt);
        row.addView(size);
        row.addView(amt_paid);
        row.addView(due_amt);
        row.addView(mode);
        row.addView(date);
        row.addView(time);
        row.addView(location);
        row.addView(reg_name);
        row.addView(delivery);

        tableLayout.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }



    public void export(){

        try{
            //saving the file into device
            FileOutputStream out = context.openFileOutput("Report.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            File filelocation = new File(context.getFilesDir(), "Report.csv");
            Uri path = FileProvider.getUriForFile(context, "com.texcel.exportcsv.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

}
