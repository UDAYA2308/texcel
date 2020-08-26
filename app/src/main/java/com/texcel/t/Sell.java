package com.texcel.t;


import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Sell extends Fragment {

    View view;
    TextInputLayout name,regno,phone,amount;
    Spinner location,size,mode,t_shirt;
    DatabaseReference databaseReference;
    Button submit;
    Context context;
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    String database,body;
    String reg_name;
    String send_to;
    ArrayList<String> size_list;
    TextView list,add_size;
    String strDate;
    int price,amt;

    public Sell() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_sell, container, false);
        context=getActivity();

        name=view.findViewById(R.id.name);
        regno=view.findViewById(R.id.reg_no);
        phone=view.findViewById(R.id.phone);
        t_shirt=view.findViewById(R.id.t_shirt);
        amount=view.findViewById(R.id.amt);
        location=view.findViewById(R.id.loc);
        size=view.findViewById(R.id.size);
        mode=view.findViewById(R.id.mode);
        submit=view.findViewById(R.id.submit);
        list=view.findViewById(R.id.size_list);
        add_size=view.findViewById(R.id.add_tshirt);

        size_list=new ArrayList<>();

        add_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size_list.add(size.getSelectedItem().toString());
                list.setText(list.getText().toString()+size.getSelectedItem().toString()+" ");
            }
        });

        sharedPreferences=context.getSharedPreferences(""+R.string.app_name,Context.MODE_PRIVATE);
         database=sharedPreferences.getString("event","D");
         reg_name=sharedPreferences.getString(""+R.string.name,"");


         DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference(database).child("Price");

         databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 List<String> list=new ArrayList<>();
                 for(DataSnapshot snapshot:dataSnapshot.getChildren())
                 {
                     Costprice costprice=snapshot.getValue(Costprice.class);
                     list.add(costprice.type);
                 }
                 ArrayAdapter<String> adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,list);
                 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                 t_shirt.setAdapter(adapter);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


        if(savedInstanceState!=null)
        {
            name.getEditText().setText(savedInstanceState.getString(""+R.string.name));
            regno.getEditText().setText(savedInstanceState.getString(""+R.string.reg));
            phone.getEditText().setText(savedInstanceState.getString(""+R.string.phone));
            amount.getEditText().setText(savedInstanceState.getString(""+R.string.amt));
        }

        name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    name.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        amount.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                amount.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        regno.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                regno.setErrorEnabled(false);
                submit.setEnabled(true);

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(regno.getEditText().getText().length()==9)
                {
                    Query query=FirebaseDatabase.getInstance().getReference(database).child("Order").orderByChild("regno").equalTo(regno.getEditText().getText().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                            {
                                sell_detail detail=dataSnapshot1.getValue(sell_detail.class);
                                if(detail.regno.equals(regno.getEditText().getText().toString())) {
                                    Toast.makeText(context, "User Already Registered", Toast.LENGTH_LONG).show();
                                    submit.setEnabled(false);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference(database);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sharedPreferences.getBoolean("access",false))
                try{



               LayoutInflater layoutInflater=LayoutInflater.from(  context);
                View confirm_view=layoutInflater.inflate(R.layout.sell_confirm,null);

                alertDialog=new AlertDialog.Builder(context).setView(confirm_view).create();


                CardView cardView=confirm_view.findViewById(R.id.confirm_dialog);
                TextView _reg,_phone,_name,_t_shirt,_size,_amt,_time,_date,_mode;

                    _reg=confirm_view.findViewById(R.id.reg_no);
                    _phone=confirm_view.findViewById(R.id.phone);
                    _name=confirm_view.findViewById(R.id.name);
                    _size=confirm_view.findViewById(R.id.size);
                    _t_shirt=confirm_view.findViewById(R.id.t_shirt);
                    _amt=confirm_view.findViewById(R.id.amt);
                    _date=confirm_view.findViewById(R.id.date);
                    _time=confirm_view.findViewById(R.id.time);
                    _mode=confirm_view.findViewById(R.id.mode);

                    if(validate_all()) {
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        strDate= formatter.format(date);
                        String time=sdf.format(date);

                        _reg.setText(_reg.getText()+" : "+regno.getEditText().getText().toString());
                        _name.setText(_name.getText()+" : "+name.getEditText().getText().toString());
                        _phone.setText(_phone.getText()+" : "+phone.getEditText().getText().toString());
                        _size.setText(_size.getText()+" : "+size.getSelectedItem().toString()+" "+list.getText().toString());
                        _t_shirt.setText(_t_shirt.getText()+" : "+t_shirt.getSelectedItem().toString());
                        _amt.setText(_amt.getText()+" : "+amount.getEditText().getText().toString());
                        _date.setText(_date.getText()+" : "+strDate);
                        _time.setText(_time.getText()+" : "+time);
                        _mode.setText(_mode.getText()+" : "+mode.getSelectedItem().toString());

                       alertDialog.show();

                    }


                 cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.cancel();
                   /*     for(int i=1;i<150;i++) {
                            final int  con=i;
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    test_input(con);
                                }
                            }, 300);
                        }*/
                   post_data();

                    }
                });
                }catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context,"Access Denied", Toast.LENGTH_LONG).show();

                }

            }
        });


        return  view;
    }

    public void post_data()
    {

        try{

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        strDate= formatter.format(date);
        String time=sdf.format(date);

        {


            String _name = name.getEditText().getText().toString();
            String _regno = regno.getEditText().getText().toString();
            send_to=_regno;
            String _t_shirt = t_shirt.getSelectedItem().toString();
            int _amount = Integer.parseInt(amount.getEditText().getText().toString());
            amt = _amount;
            String _location = location.getSelectedItem().toString();
            String _size = size.getSelectedItem().toString()+" "+list.getText().toString();
            String _mode = mode.getSelectedItem().toString();
            String _phone = phone.getEditText().getText().toString();
            price=sharedPreferences.getInt(_t_shirt,0);
            int count=size_list.size()+1;
            int due = price*count - _amount;
            RandomString randomString=new RandomString();
            String code=randomString.getAlphaNumericString(6);
            databaseReference.child("Order").child(_regno).setValue(new sell_detail(_regno, _name, _phone, _t_shirt, _size, _amount, _mode, "-", due, time, strDate,code,_location,reg_name));
            submit.setEnabled(false);
            Toast.makeText(context,"Submitted",Toast.LENGTH_LONG).show();
            update_all();
            body="Your order has been successfully placed.\nOrder detail\nSize : "+_size+"\nT-shirt : "+_t_shirt+"\nAmount paid : "+amt+"\nPurchase code : "+code;
            openWhatsApp("91"+_phone);
            name.getEditText().setText("");
            regno.getEditText().setText("");
            phone.getEditText().setText("");
            amount.getEditText().setText("0");
            regno.requestFocus();

        }

        }catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void update_all()
    {

        final int final_count=size_list.size();
        final ArrayList <String> copy_list=size_list;


        Query query1=FirebaseDatabase.getInstance().getReference(database).child("Location").orderByChild("location").equalTo(location.getSelectedItem().toString());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Location_detail detail=dataSnapshot1.getValue(Location_detail.class);
                    if(detail.location.equals(location.getSelectedItem().toString()))
                    {
                        detail.count+=1+final_count;
                        databaseReference.child("Location").child(location.getSelectedItem().toString()).setValue(detail);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         databaseReference.child("Day_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    Day_count d = dataSnapshot.getValue(Day_count.class);
                    if (d.date != Integer.parseInt(strDate.substring(0, 2))) {
                        d.reset();
                    }
                    d.date = Integer.parseInt(strDate.substring(0, 2));
                    d.count += 1+final_count;
                    d.total_amt += amt;
                    if (mode.getSelectedItem().toString().equals("Online")) {
                        d.online += amt;
                    } else {
                        d.cash += amt;
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

                try{
                total_count d=dataSnapshot.getValue(total_count.class);
                d.count+=1+final_count;
                d.total_amt+=amt;
                d.due+=price*(1+final_count)-amt;
                if(mode.getSelectedItem().toString().equals("Online"))
                {
                    d.online+=amt;
                }
                else
                {
                    d.cash+=amt;
                }
                databaseReference.child("Total_count").setValue(d);
                }catch (Exception e) {
                    Toast.makeText(context,"Total count"+ e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SharedPreferences sharedPreferences = context.getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("" +R.string.phone, "");
        Query query=FirebaseDatabase.getInstance().getReference(database).child("Users").orderByChild("phone").equalTo(user);
         query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    SharedPreferences sharedPreferences = context.getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
                    String user = sharedPreferences.getString("" + R.string.phone, "");
                    Users d = dataSnapshot1.getValue(Users.class);
                    if (d.phone.equals(user)) {
                        d.count += 1+final_count;
                        databaseReference.child("Users").child(user).setValue(d);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         databaseReference.child("Order_detail").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                 {
                     Order_detail detail=dataSnapshot1.getValue(Order_detail.class);
                     if(detail.t_shirt.equals(t_shirt.getSelectedItem().toString()))
                     {

                         switch (size.getSelectedItem().toString())
                         {
                             case "S":detail.S+=1;break;
                             case "M":detail.M+=1;break;
                             case "L":detail.L+=1;break;
                             case "XL":detail.XL+=1;break;
                             case "XXL":detail.XXL+=1;break;
                             case "XXXL":detail.XXXL+=1;break;
                         }


                         for(String c:copy_list)
                         {
                             switch (c)
                             {
                                 case "S":detail.S+=1;break;
                                 case "M":detail.M+=1;break;
                                 case "L":detail.L+=1;break;
                                 case "XL":detail.XL+=1;break;
                                 case "XXL":detail.XXL+=1;break;
                                 case "XXXL":detail.XXXL+=1;break;
                             }
                         }

                         detail.total+=1+final_count;
                         databaseReference.child("Order_detail").child(detail.t_shirt).setValue(detail);
                     }
                 }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        size_list=new ArrayList<>();
        list.setText("");

    }


    public boolean validate_all()
    {

        if(regno.getEditText().length()==0)
        {
            regno.setError("Field can't be Empty");
            regno.requestFocus();
            return false;
        }
        else if (regno.getEditText().getText().length()!=9)
        {
            regno.setError("Invalid Register No");
            regno.requestFocus();
            return  false;
        }

        if(name.getEditText().length()==0)
        {
            name.setError("Field can't be Empty");
            name.requestFocus();
            return false;
        }

        if(phone.getEditText().length()==0)
        {
            phone.setError("Field can't be Empty");
            phone.requestFocus();
            return false;
        }
        else if (phone.getEditText().getText().length()!=10)
        {
            phone.setError("Invalid Phone No");
            phone.requestFocus();
            return  false;
        }


        if(amount.getEditText().length()==0)
        {
            amount.setError("Field can't be Empty");
            amount.requestFocus();
            return false;
        }
        else if (Integer.parseInt(amount.getEditText().getText().toString())>sharedPreferences.getInt(t_shirt.getSelectedItem().toString(),0)*(size_list.size()+1))
        {
            amount.setError("Limit exceeded ");
            amount.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(""+R.string.name,name.getEditText().getText().toString());
        outState.putString(""+R.string.phone,phone.getEditText().getText().toString());
        outState.putString(""+R.string.reg,regno.getEditText().getText().toString());
        outState.putString(""+R.string.amt,amount.getEditText().getText().toString());
     }

    private void openWhatsApp(String toNumber) {
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
                 mess(toNumber);
              }
             else
             {
                 request();
             }

        }
    }

    public boolean permisson()
    {
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED)
            return true;

        else
            return false;

    }

    public void request()
    {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},100);
    }

    public void mess(String number)
    {
        SmsManager smsManager=SmsManager.getDefault();
         smsManager.sendTextMessage(number.substring(2),null,body,null,null);
    }

    public void test_input(int i)
    {

        try{

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            strDate= formatter.format(date);
            String time=sdf.format(date);

            {


                String _name = "test_alias";
                String _regno = ""+i;
                send_to=_regno;
                String _t_shirt = t_shirt.getSelectedItem().toString();
                int _amount = Integer.parseInt(amount.getEditText().getText().toString());
                amt = _amount;
                String _location = location.getSelectedItem().toString();
                String _size = size.getSelectedItem().toString()+" "+list.getText().toString();
                String _mode = mode.getSelectedItem().toString();
                String _phone = "9789196347";
                price=sharedPreferences.getInt(_t_shirt,0);
                int count=size_list.size()+1;
                int due = price*count - _amount;
                RandomString randomString=new RandomString();
                String code=randomString.getAlphaNumericString(6);
                databaseReference.child("Order").child(_regno).setValue(new sell_detail(_regno, _name, _phone, _t_shirt, _size, _amount, _mode, "-", due, time, strDate,code,_location,reg_name));
                submit.setEnabled(false);
                Toast.makeText(context,"Submitted",Toast.LENGTH_LONG).show();
                update_all();
                body="Your order has been successfully placed.\nOrder detail\nSize : "+_size+"\nT-shirt : "+_t_shirt+"\nAmount paid : "+amt+"\nPurchase code : "+code;
                //openWhatsApp("91"+_phone);
                name.getEditText().setText("");
                regno.getEditText().setText("");
                phone.getEditText().setText("");
                amount.getEditText().setText("0");
                regno.requestFocus();

            }

        }catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }



}
