package com.texcel.t;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class account extends Fragment {

    View view;
    Spinner spinner,host_data;
    Context context;
    Button exit;
    RecyclerView host;
    String ph_number;
    DatabaseReference databaseReference;
    SharedPreferences preferences;
    ProgressDialog progressDialog;

    public account() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_accunt, container, false);

        context=getActivity();

        preferences=getActivity().getSharedPreferences(""+R.string.app_name,Context.MODE_PRIVATE);

        ph_number=preferences.getString(""+R.string.phone,"");
        spinner=view.findViewById(R.id.manage_);
        host=view.findViewById(R.id.host_view);

        host_data=view.findViewById(R.id.host_data);

        exit=view.findViewById(R.id.exit_);

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading.....");

        List<String> stringList=new ArrayList<>();
        stringList.add("Host");
        stringList.add("Joined");

        ArrayAdapter<String> adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,stringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        display_host();

        host.setHasFixedSize(true);
        host.setLayoutManager(new LinearLayoutManager(context));


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    LayoutInflater layoutInflater=LayoutInflater.from(context);
                    View confirm_view=layoutInflater.inflate(R.layout.confirm_deliver,null);

                   final AlertDialog alertDialog=new AlertDialog.Builder(context).setView(confirm_view).create();

                   CardView b=confirm_view.findViewById(R.id.confirm_deliver);

                     TextView data=confirm_view.findViewById(R.id.display_text);
                     if(spinner.getSelectedItemPosition()==1)
                    data.setText("LEAVE ROOM ");
                     else
                     {
                         data.setText("DELETE ROOM ");
                     }
                   alertDialog.show();
                   b.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           alertDialog.cancel();
                           try {
                               if (spinner.getSelectedItemPosition() == 1)
                               {
                                    remove_user();
                               }
                               else
                               {
                                  delete_room();
                               }
                           }catch (Exception e){
                            //   Toast.makeText(context,e.getMessage() ,Toast.LENGTH_LONG).show();
                               progressDialog.cancel();}
                       }
                   });

                }catch (Exception e){progressDialog.cancel();}
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    {

                        display_host();

                        exit.setText("Delete Room");

                        host_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(spinner.getSelectedItem().toString().equals("Host"))
                                {
                                    host.setAdapter(null);
                                    String code=host_data.getSelectedItem().toString();
                                    code=code.substring(code.length()-9,code.length()-1);

                                    databaseReference=FirebaseDatabase.getInstance().getReference(code).child("Users");
                                    final String finalCode = code;
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            ArrayList<Users> users=new ArrayList<>();

                                            for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                            {
                                                Users data=snapshot.getValue(Users.class);

                                                if(!data.phone.equals(ph_number))
                                                users.add(data);
                                            }
                                            Adapterhost adapterhost=new Adapterhost(context,users, finalCode);
                                            host.setAdapter(adapterhost);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        host.setVisibility(View.VISIBLE);

                    }
                }
                else {
                    exit.setText("Exit Room");
                    display_user();
                    host.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;

    }

    public void display_user(){

        progressDialog.show();

        host_data.setAdapter(null);

        final ArrayList<String> list=new ArrayList<>();
        String phone=preferences.getString(""+R.string.phone,"");

        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()>0)
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    genuser user=snapshot.getValue(genuser.class);

                    try {

                        for (String s : user.user) {

                            final String code = s;

                            Query query = FirebaseDatabase.getInstance().getReference(s).child("Event");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String s1 = dataSnapshot.getValue(String.class);
                                    list.add(s1 + " [" + code + "]");
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    host_data.setAdapter(adapter);
                                    progressDialog.cancel();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }catch (Exception e)

                    {
                       // Toast.makeText(context,"User "+e.getMessage() ,Toast.LENGTH_LONG).show();

                        progressDialog.cancel();
                    }

                }
                else
                {
                    progressDialog.cancel();
                    Toast.makeText(context,"No record found",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public  void display_host()
    {
        progressDialog.show();

        host_data.setAdapter(null);
        final ArrayList<String> list=new ArrayList<>();
        String phone=preferences.getString(""+R.string.phone,"");

         Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);

         query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try{
                    if(dataSnapshot.getChildrenCount()>0)
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                     genuser user=snapshot.getValue(genuser.class);

                    for(String s:user.host)
                    {
                        final String code=s;
                         Query  query=FirebaseDatabase.getInstance().getReference(s).child("Event");
                         query.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String s1=dataSnapshot.getValue(String.class);
                                list.add(s1+" ["+code+"]");
                                 ArrayAdapter<String> adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,list);
                                 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                 host_data.setAdapter(adapter);

                                 progressDialog.cancel();


                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                         });
                    }

                }
                    else
                    {
                        progressDialog.cancel();
                        Toast.makeText(context,"No record found",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    progressDialog.cancel();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void remove_user()
    {


        final String phone=preferences.getString(""+R.string.phone,"");

        String code=host_data.getSelectedItem().toString();
        code=code.substring(code.length()-9,code.length()-1);



        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("access",false);
        editor.commit();

        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.frame,new Home());
        if (getFragmentManager().getBackStackEntryCount() == 1)
            ft.addToBackStack(null);
        else {
            getFragmentManager().popBackStack();
            ft.addToBackStack(null);

        }

        ft.commit();

        Toast.makeText(context,"Left room",Toast.LENGTH_LONG).show();

        databaseReference=FirebaseDatabase.getInstance().getReference(code).child("Users");
        databaseReference.child(phone).removeValue();

     Query   databaseReference1=  FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);

        final String finalCode = code;
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {

                    genuser user=dataSnapshot1.getValue(genuser.class);

                    user.user.remove(finalCode);

                    databaseReference=FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(phone).setValue(user);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void delete_room()
    {
        String code=host_data.getSelectedItem().toString();
        code=code.substring(code.length()-9,code.length()-1);
        final String phone=preferences.getString(""+R.string.phone,"");

        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("access",false);
        editor.commit();

        FirebaseDatabase.getInstance().getReference(code).removeValue();
        final String finalCode = code;


        Query  databaseReference1=  FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {

                    genuser user=dataSnapshot1.getValue(genuser.class);

                    user.host.remove(finalCode);

                    databaseReference=FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(phone).setValue(user);
                    Toast.makeText(context,"Room deleted",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.frame,new Home());
        if (getFragmentManager().getBackStackEntryCount() == 1)
            ft.addToBackStack(null);
        else {
            getFragmentManager().popBackStack();
            ft.addToBackStack(null);

        }

        ft.commit();

    }


}
