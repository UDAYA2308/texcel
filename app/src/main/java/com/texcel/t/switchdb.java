package com.texcel.t;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
public class switchdb extends Fragment {

    Context context;
    View view;
    Spinner db;
    Button button;
    SharedPreferences preferences;
    ProgressDialog progressDialog ;


    public switchdb() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_switchdb, container, false);

        context=getActivity();


       progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading.....");


        preferences=getActivity().getSharedPreferences(""+R.string.app_name,Context.MODE_PRIVATE);

        db=view.findViewById(R.id.event_);

        switch_();
        button=view.findViewById(R.id.switch_);

        final FragmentTransaction ft=getFragmentManager().beginTransaction();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String code = db.getSelectedItem().toString();
                    String event = code.substring(0, code.length() - 10);
                    code = code.substring(code.length() - 9, code.length() - 1);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("event", code);
                    editor.putString("" + R.string.event, event);
                    editor.commit();


                    ft.replace(R.id.frame, new Home());
                    if (getFragmentManager().getBackStackEntryCount() == 1)
                        ft.addToBackStack(null);
                    else {
                        getFragmentManager().popBackStack();
                        ft.addToBackStack(null);

                    }

                    Toast.makeText(context, "Switched ", Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {

                }
            }
        });



        return  view;
    }

    public  void switch_()
    {

        progressDialog.show();
        final ArrayList<String> list=new ArrayList<>();
        String phone=preferences.getString(""+R.string.phone,"");

        Query query= FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()>0) {
                    final int[] i = {0};
                    button.setEnabled(true);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        genuser user = snapshot.getValue(genuser.class);

                        try {

                            for (String s : user.user) {
                                final String code = s;
                                Query query = FirebaseDatabase.getInstance().getReference(s).child("Event");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String s1 = dataSnapshot.getValue(String.class);
                                        list.add(s1 + " [" + code + "]");
                                        i[0]++;
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        db.setAdapter(adapter);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } catch (Exception e) {
                        }

                        try {

                            for (String s : user.host) {
                                final String code = s;
                                Query query = FirebaseDatabase.getInstance().getReference(s).child("Event");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String s1 = dataSnapshot.getValue(String.class);
                                        list.add(s1 + " [" + code + "]");
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        db.setAdapter(adapter);
                                        progressDialog.cancel();


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } catch (Exception e) {

                            progressDialog.cancel();
                        }


                    }


                }
                else
                {
                    progressDialog.cancel();
                    Toast.makeText(context,"No record Found",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
