package com.texcel.t;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    FrameLayout frameLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Snackbar snackbar;
    DatabaseReference databaseReference;
    String database;
    String Event_name;
    HashMap<String,Integer> price_table;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        frameLayout = findViewById(R.id.frame);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(getSupportFragmentManager().getBackStackEntryCount()==0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home(), "Home").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
        }


        if(!permisson())
            request();

        drawerLayout=findViewById(R.id.draw_layout);
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        navigationView=findViewById(R.id.navigation);

        sharedPreferences=getSharedPreferences(""+R.string.app_name,MODE_PRIVATE);

        snackbar= Snackbar.make(drawerLayout,"Press Again To Exit..",Snackbar.LENGTH_SHORT);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                boolean connectivity=false;

                ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                        || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {

                    // notify user you are online
                    connectivity=true;

                }
                else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                        || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

                    // notify user you are not online
                    connectivity=false;
                    AlertDialog.Builder alertDialog= new AlertDialog.Builder(Dashboard.this);
                    ImageView imageView=new ImageView(Dashboard.this);
                    imageView.setImageResource(R.drawable.no_int);
                    alertDialog.setView(imageView);
                    alertDialog.create().show();

                }

                drawerLayout.closeDrawer(GravityCompat.START);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                if(connectivity)
               try {
                   switch (menuItem.getItemId()) {
                       case R.id.sell: {
                           ft.replace(R.id.frame, new Sell());
                           if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                               ft.addToBackStack(null);
                           else {
                               getSupportFragmentManager().popBackStack();
                               ft.addToBackStack(null);
                           }
                       }   break;
                       case R.id.report:
                       {  ft.replace(R.id.frame, new Report());
                           if(getSupportFragmentManager().getBackStackEntryCount()==1)
                    ft.addToBackStack(null);
                    else
                    {
                        getSupportFragmentManager().popBackStack();
                        ft.addToBackStack(null);
                    }
                       }break;
                       case R.id.home_: {
                           ft.replace(R.id.frame, new Home());
                           if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                               ft.addToBackStack(null);
                           else {
                               getSupportFragmentManager().popBackStack();
                               ft.addToBackStack(null);
                           }
                       }break;
                       case R.id.due: {
                           ft.replace(R.id.frame, new Due_list());
                           if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                               ft.addToBackStack(null);
                           else {
                               getSupportFragmentManager().popBackStack();
                               ft.addToBackStack(null);
                           }
                       }break;
                       case R.id.deliver_: {
                           ft.replace(R.id.frame, choose(0));
                           if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                               ft.addToBackStack(null);
                           else {
                               getSupportFragmentManager().popBackStack();
                               ft.addToBackStack(null);
                           }
                       }break;
                       case R.id.stats_:
                       {  ft.replace(R.id.frame, new Stat());
                           if(getSupportFragmentManager().getBackStackEntryCount()==1)
                    ft.addToBackStack(null);
                    else
                    {
                        getSupportFragmentManager().popBackStack();
                        ft.addToBackStack(null);
                    }
                       }break;
                       case R.id.update_:
                       {    ft.replace(R.id.frame, choose(1));
                           if(getSupportFragmentManager().getBackStackEntryCount()==1)
                    ft.addToBackStack(null);
                    else
                    {
                        getSupportFragmentManager().popBackStack();
                        ft.addToBackStack(null);
                    }
                       }break;
                       case R.id.account:
                       {    ft.replace(R.id.frame,new account());
                           if(getSupportFragmentManager().getBackStackEntryCount()==1)
                               ft.addToBackStack(null);
                           else
                           {
                               getSupportFragmentManager().popBackStack();
                               ft.addToBackStack(null);
                           }
                       }break;

                       case R.id.switch_:
                       {    ft.replace(R.id.frame,new switchdb());
                           if(getSupportFragmentManager().getBackStackEntryCount()==1)
                               ft.addToBackStack(null);
                           else
                           {
                               getSupportFragmentManager().popBackStack();
                               ft.addToBackStack(null);
                           }
                       }break;




                   }

                   ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                   ft.commit();
               }
               catch (Exception e)
               {
                   Toast.makeText(Dashboard.this,e.getMessage(),Toast.LENGTH_LONG).show();
               }

                return true;
            }
        });

    }

    public Deliver choose(int a)
    {
        Bundle bundle=new Bundle();
        Deliver deliver=new Deliver();
        bundle.putInt(""+R.string.app_name,a);
        deliver.setArguments(bundle);
        return deliver;
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if(getSupportFragmentManager().findFragmentByTag("Home")!=null&&getSupportFragmentManager().findFragmentByTag("Home").isVisible())
            if(snackbar.isShown())
            {
                this.finishAffinity();
            }
            else
            {
                snackbar.show();
            }
        else
        {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.side,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        LayoutInflater layoutInflater=LayoutInflater.from(this);
        View confirm_view=layoutInflater.inflate(R.layout.join,null);

        final AlertDialog alertDialog=new AlertDialog.Builder(this).setView(confirm_view).create();

        Button b=confirm_view.findViewById(R.id.join_);

        final LinearLayout layout;


        if(item.getItemId()==R.id.create_)
        {

            confirm_view=layoutInflater.inflate(R.layout.create,null);
            final AlertDialog alert=new AlertDialog.Builder(this).setView(confirm_view).create();
            layout=confirm_view.findViewById(R.id.extra_tshirts);

            b=confirm_view.findViewById(R.id.create_);

            final TextInputLayout ename=confirm_view.findViewById(R.id.event_);
            final TextInputLayout etype=confirm_view.findViewById(R.id.t_shirt);
            final TextInputLayout eprice=confirm_view.findViewById(R.id.price_);
            final TextView add_shirt=confirm_view.findViewById(R.id.add_tshirt);

            price_table=new HashMap<>();
            add_shirt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(etype.getEditText().getText().length() == 0 || etype.getEditText().getText().length() == 0)) {

                        String price = eprice.getEditText().getText().toString();
                        String type = etype.getEditText().getText().toString();

                        TextView details = new TextView(Dashboard.this);

                        details.setText("T-shirt : " + type + "  Price : " + price);
                        details.setPadding(20, 20, 20, 20);
                        details.setGravity(Gravity.CENTER);
                        details.setTextAppearance(Dashboard.this, android.R.style.TextAppearance_Large);
                        details.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        layout.addView(details);
                        eprice.getEditText().setText("");
                        etype.getEditText().setText("");

                        price_table.put(type, Integer.parseInt(price));
                    }
                    else
                    {
                        Toast.makeText(Dashboard.this,"Enter All Details..",Toast.LENGTH_LONG).show();
                    }


                }

            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!(ename.getEditText().getText().length()==0||etype.getEditText().getText().length()==0||etype.getEditText().getText().length()==0))
                    {
                    Event_name=ename.getEditText().getText().toString();
                    String price=eprice.getEditText().getText().toString();
                    String type=etype.getEditText().getText().toString();
                    price_table.put(type,Integer.parseInt(price));

                    database=new RandomString().getAlphaNumericString(8);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("event",database);
                    editor.putString(""+R.string.event,Event_name);

                    AlertDialog.Builder builder=new AlertDialog.Builder(Dashboard.this);
                    TextView textView=new TextView(Dashboard.this);
                    textView.setText("Your Room code is : "+database);
                    textView.setPadding(20,20,20,20);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextAppearance(Dashboard.this,android.R.style.TextAppearance_Large);
                    builder.setView(textView);
                    builder.show();

                    editor.commit();

                    create_Data();
                    final String name=sharedPreferences.getString(""+R.string.name,"d");
                    String regno=sharedPreferences.getString(""+R.string.reg,"d");
                    final String phone=sharedPreferences.getString(""+R.string.phone,"d");
                    add_user(phone,name,database,regno,0);



                    alert.cancel();
                }
                    else
                    {
                        Toast.makeText(Dashboard.this,"Enter All Details..",Toast.LENGTH_LONG).show();
                    }
                    }
            });

            alert.show();

        }
        else
        {
            alertDialog.show();


            final View finalConfirm_view = confirm_view;
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final TextInputLayout code= finalConfirm_view.findViewById(R.id.join_code);

                    final String s=code.getEditText().getText().toString();

                    if(s.length()>0) {
                        Query query = FirebaseDatabase.getInstance().getReference(s);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    final String name = sharedPreferences.getString("" + R.string.name, "d");
                                    String regno = sharedPreferences.getString("" + R.string.reg, "d");
                                    final String phone = sharedPreferences.getString("" + R.string.phone, "d");

                                    add_user(phone, name, code.getEditText().getText().toString(), regno, 1);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("event", s);
                                    String event=dataSnapshot.child("Event").getValue(String.class);
                                    editor.putString(""+R.string.event,event);
                                    editor.commit();

                                    FragmentTransaction ft=getSupportFragmentManager().beginTransaction();

                                    ft.replace(R.id.frame,new Home());
                                    if (getFragmentManager().getBackStackEntryCount() == 1)
                                        ft.addToBackStack(null);
                                    else {
                                        getFragmentManager().popBackStack();
                                        ft.addToBackStack(null);

                                    }
                                    ft.commit();


                                    Toast.makeText(Dashboard.this, "Joined ", Toast.LENGTH_LONG).show();

                                    alertDialog.cancel();
                                } else {
                                    Toast.makeText(Dashboard.this, " Code not found . Please check the code and try again", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
            });



        }

        return super.onOptionsItemSelected(item);
    }

    public void add_user(final String phone, final String name, final String code, final String regno, final int from)
    {


        databaseReference=FirebaseDatabase.getInstance().getReference(code).child("Users");

        Query query1=FirebaseDatabase.getInstance().getReference(code).child("Users").orderByChild("phone").equalTo(phone);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0)
                {
                    if(from==0)
                        databaseReference.child(phone).setValue(new Users(name,regno,phone,0,true));
                    else
                        databaseReference.child(phone).setValue(new Users(name,regno,phone,0,false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> host=new ArrayList<>();
                List<String> user=new ArrayList<>();

                if(dataSnapshot.getChildrenCount()!=0)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        genuser genuser=snapshot.getValue(com.texcel.t.genuser.class);

                        user=genuser.user;
                        host=genuser.host;

                        if(from==0)
                        try {
                            host.add(code);
                        }
                        catch (Exception e)
                        {
                            host=new ArrayList<>();
                            host.add(code);
                        }
                        else
                            try {
                                if(!host.contains(code)) {

                                    try{
                                        if(!user.contains(code))
                                    user.add(code);
                                  }
                                    catch (Exception c)
                                    {
                                        user=new ArrayList<>();
                                        user.add(code);
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                try{
                                    if(!user.contains(code))
                                        user.add(code);
                                }
                                catch (Exception c)
                                {
                                    user=new ArrayList<>();
                                    user.add(code);
                                }
                            }
                    }
                }
                else
                {
                    if(from==0)
                        host.add(code);
                    else {

                              user.add(code);
                    }

                }
                databaseReference=FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(phone).setValue(new genuser(name,phone,host,user));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public  void  create_Data()
    {
        Query query= FirebaseDatabase.getInstance().getReference(database);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

         {
             String name=sharedPreferences.getString(""+R.string.name,"");

                    databaseReference=FirebaseDatabase.getInstance().getReference(database);
                    databaseReference.child("Admin").setValue(new String(name));
                    databaseReference.child("Event").setValue(new String(Event_name));

             String []array=getResources().getStringArray(R.array.location);

                    for(String data:array)
                    {
                        databaseReference.child("Location").child(data).setValue(new Location_detail(data,0));
                    }

             Iterator iterator=price_table.entrySet().iterator();

                    while (iterator.hasNext())
                    {
                        Map.Entry pair = (Map.Entry)iterator.next();
                        databaseReference.child("Price").child((String) pair.getKey()).setValue(new Costprice(pair.getKey().toString(),Integer.parseInt(pair.getValue().toString())));
                        databaseReference.child("Order_detail").child((String) pair.getKey()).setValue(new Order_detail(pair.getKey().toString(),0,0,0,0,0,0,0));
                        iterator.remove();
                    }

                    databaseReference.child("Day_count").setValue(new Day_count(0,0,0,0,0));
                    databaseReference.child("Total_count").setValue(new total_count(0,0,0,0,0));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean permisson()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED)
            return true;

        else
            return false;

    }

    public void request()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},100);
    }

}
