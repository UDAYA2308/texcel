package com.texcel.t;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class Deliver extends Fragment {


    View view;
    CardView result,deliver;
    Query query;
    Context context;
    sell_detail value;
    SearchView searchView;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    int D_or_U;
    SharedPreferences sharedPreferences;
    String database;
    int price;
    boolean duplicate;
    int paid,due_amt;
    String strDate,search_String,send_to;
    TextInputLayout textInputLayout;
    AlertDialog alertDialog;
    TextView reg,phone,name,t_shirt,size,amt,due,time,date,mode,update,code;

    public Deliver() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_deliver, container, false);

        try {

            Bundle bundle=getArguments();
            D_or_U=bundle.getInt(""+R.string.app_name);

        searchView=view.findViewById(R.id.search_record);
        result=view.findViewById(R.id.card_view);
        deliver=view.findViewById(R.id.deliver);
        context=getActivity();
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setIconified(false);
        textInputLayout=view.findViewById(R.id.due_amt);

            sharedPreferences=context.getSharedPreferences(""+R.string.app_name,Context.MODE_PRIVATE);
            price=sharedPreferences.getInt(""+R.string.amt,0);
            database=sharedPreferences.getString("event","D");

            Date df = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            strDate= formatter.format(df);

            reg=view.findViewById(R.id.reg_no);
            phone=view.findViewById(R.id.phone);
            name=view.findViewById(R.id.name);
            size=view.findViewById(R.id.size);
            t_shirt=view.findViewById(R.id.t_shirt);
            amt=view.findViewById(R.id.amt);
            code=view.findViewById(R.id.code_);
            due=view.findViewById(R.id.due);
            date=view.findViewById(R.id.date);
            time=view.findViewById(R.id.time);
            mode=view.findViewById(R.id.mode);
            update=view.findViewById(R.id.d_or_u);

            progressDialog=new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading.....");

            textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textInputLayout.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });




            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String s) {
                    search_String=s;
                    if(sharedPreferences.getBoolean("access",false))
                        if(s.length()==9)
                                search_result();
                       else
                    {
                        Toast.makeText(context,"Invalid Register No",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context,"Access Denied", Toast.LENGTH_LONG).show();

                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    if(s.length()<9)
                        duplicate=true;

                    return false;
                }
            });
        }catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean entery_=false;
                if(D_or_U==0)
                {
                    entery_=true;
                }
                else
                {
                    entery_=validate_all();
                }
                if(entery_)
                try
                {
                LayoutInflater layoutInflater=LayoutInflater.from(context);
                View confirm_view=layoutInflater.inflate(R.layout.confirm_deliver,null);

                alertDialog=new AlertDialog.Builder(context).setView(confirm_view).create();

                final TextView data=confirm_view.findViewById(R.id.display_text);
                CardView cardView=confirm_view.findViewById(R.id.confirm_deliver);

                alertDialog.show();

                if(D_or_U==0)
                {
                    if(due_amt==0) {
                        data.setText("Confirm Deliver ?");
                        paid=0;

                    }
                    else
                        data.setText("Due amount : "+due_amt+" \nConfirm Deliver ? ");


                }
                else
                {
                    data.setText("Update Amount : "+textInputLayout.getEditText().getText());
                }

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.cancel();
                        deliver_();
                    }
                });

                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public void deliver_()
    {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference(database);

            if(D_or_U==0) {

                value.delivery = "Delivered";
                if(due_amt>0) {
                    value.amount_paid +=due_amt;
                    paid=due_amt;
                    value.due_amount = 0;
                    databaseReference.child("Order").child(value.regno).child("amount_paid").setValue(value.amount_paid);
                    databaseReference.child("Order").child(value.regno).child("due_amount").setValue(value.due_amount);
                    update_all();

                }
                databaseReference.child("Order").child(value.regno).child("delivery").setValue(value.delivery);
                openWhatsApp("91"+value.phone,"Delivered : Your T-shirt "+value.t_shirt+" was delivered!");
                duplicate=false;
                search_result();
                Toast.makeText(context,"Delivered", Toast.LENGTH_LONG).show();
            }
            else
            {
                paid=Integer.parseInt(textInputLayout.getEditText().getText().toString());
                value.due_amount-=paid ;
                value.amount_paid+=paid;
                databaseReference.child("Order").child(value.regno).child("amount_paid").setValue(value.amount_paid);
                databaseReference.child("Order").child(value.regno).child("due_amount").setValue(value.due_amount);
                openWhatsApp("91"+value.phone,"Payment : You have paid rs "+paid+" total amount paid : "+value.amount_paid);
                Toast.makeText(context,"Updated", Toast.LENGTH_LONG).show();
                textInputLayout.getEditText().setText("");
                update_all();
                search_result();
            }

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    public void update_all()
    {

        databaseReference.child("Day_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Day_count d=dataSnapshot.getValue(Day_count.class);
                if(d.date!=Integer.parseInt(strDate.substring(0,2)))
                {
                    d.reset();
                }
                d.date=Integer.parseInt(strDate.substring(0,2));
                d.total_amt+=paid;
                if(mode.getText().toString().contains("Online"))
                {
                    d.online+=paid;
                }
                else
                {
                    d.cash+=paid;
                }
                databaseReference.child("Day_count").setValue(d);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference.child("Total_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                total_count d=dataSnapshot.getValue(total_count.class);
                d.total_amt+=paid;
                d.due-=paid;
                if(mode.getText().toString().contains("Online"))
                {
                    d.online+=paid;
                }
                else
                {
                    d.cash+=paid;
                }
                databaseReference.child("Total_count").setValue(d);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void search_result(){

        progressDialog.show();
        query = FirebaseDatabase.getInstance().getReference(database).child("Order").orderByChild("regno").equalTo(search_String);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==0)
                {
                    progressDialog.cancel();

                    if(duplicate)
                    {
                        Toast.makeText(context,"No Record Found",Toast.LENGTH_LONG).show();
                    }

                }
                else
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    sell_detail detail = dataSnapshot1.getValue(sell_detail.class);
                    value=detail;
                    send_to=value.regno;
                    reg.setText("Register No"+" : "+value.regno);
                    name.setText("Name"+ " : "+value.name);
                    phone.setText("Phone"+" : "+value.phone);
                    t_shirt.setText("T-shirt model"+" : "+value.t_shirt);
                    size.setText("Size"+" : "+value.size);
                    amt.setText("Amount paid"+" : "+value.amount_paid);
                    due.setText("Due"+" : "+value.due_amount);
                    due_amt=value.due_amount;
                    time.setText("Time"+" : "+value.timestamp);
                    date.setText("Date"+" : "+value.date);
                    mode.setText("Mode of Payment"+" : "+value.mode_of_payment);

                    if(D_or_U==1) {
                        update.setText("Update");
                        textInputLayout.setVisibility(View.VISIBLE);
                        textInputLayout.requestFocus();
                    }else {
                        code.setText("Code  :  "+value.code);
                        code.setVisibility(View.VISIBLE);
                        update.setText("Deliver");

                    }
                    deliver.setVisibility(View.VISIBLE);
                    result.setVisibility(View.VISIBLE);
                    if(detail.delivery.equals("Delivered")&&D_or_U==0&&duplicate) {
                     try{
                         AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        TextView textView=new TextView(context);
                        textView.setText("Already Delivered");
                        textView.setPadding(20,20,20,20);
                        textView.setGravity(Gravity.CENTER);
                        textView.setTextAppearance(context,android.R.style.TextAppearance_Large);
                        builder.setView(textView);
                        builder.show();
                     } catch (Exception e) {
                         Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                    }

                    progressDialog.cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean validate_all()
    {


         if(textInputLayout.getEditText().getText().length()==0)
        {
            textInputLayout.setError("Field Can't be Empty");
            return  false;

        }
        int net_Amt= Integer.parseInt(textInputLayout.getEditText().getText().toString());
        if(net_Amt>due_amt)
        {
            textInputLayout.setError("Invalid Amount");
            return false;
        }

        return true;
    }

    private void openWhatsApp(String toNumber,String body) {
        {

            BackgroundMail bm = new BackgroundMail(context);
            bm.setGmailUserName("escanorelon9@gmail.com");
            bm.setGmailPassword("Escanor_09_");
            bm.setMailTo(send_to+"@sastra.ac.in");
            bm.setFormSubject("Order Confirmation");
            bm.setFormBody(body);
            bm.send();

            if(permisson())
            {
                mess(toNumber,body);
            }
            else
            {
                request();
            }

        }
    }

    public boolean permisson()
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED)
            return true;

        else
            return false;

    }

    public void request()
    {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},100);
    }

    public void mess(String number,String body)
    {
        SmsManager smsManager=SmsManager.getDefault();
         smsManager.sendTextMessage(number.substring(2),null,body,null,null);
    }

}
