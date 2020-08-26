package com.texcel.t;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    View view;
    Timer timer;
    Context context;
    int currentPage=0;
    ViewPager pager;
    TextView t_count,t_total,t_due;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        context=getActivity();

        pager=view.findViewById(R.id.image_);
        ImageAdapter adapter=new ImageAdapter(context);
        pager.setAdapter(adapter);
        DepthTransformation depthTransformation = new DepthTransformation();
        pager.setPageTransformer(true,depthTransformation);

        final SharedPreferences sharedPreferences=context.getSharedPreferences(""+R.string.app_name,Context.MODE_PRIVATE);
        final boolean access= sharedPreferences.getBoolean("access",false);

        TextView event_disp=view.findViewById(R.id.event_);

        event_disp.setText(sharedPreferences.getString(""+R.string.event,"Event"));

        t_count=view.findViewById(R.id.t_count);
        t_total=view.findViewById(R.id.t_total_amt);
        t_due=view.findViewById(R.id.t_total_due);


        String database=sharedPreferences.getString("event","D");
        String reg_no=sharedPreferences.getString(""+R.string.reg,"");
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        {
             Query query= FirebaseDatabase.getInstance().getReference(database).child("Users").orderByChild("reg_no").equalTo(reg_no);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        Users users=dataSnapshot1.getValue(Users.class);
                        editor.putBoolean("access",users.access);
                        editor.commit();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            query=FirebaseDatabase.getInstance().getReference(database).child("Price");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for(DataSnapshot snapshot:dataSnapshot.getChildren())
                   {
                       Costprice costprice=snapshot.getValue(Costprice.class);
                       editor.putInt(costprice.type,costprice.price);
                   }
                   editor.commit();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(access)
        {
            Query query = FirebaseDatabase.getInstance().getReference(database).child("Total_count");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        total_count count = dataSnapshot.getValue(total_count.class);
                        t_count.setText("" + count.count);
                        t_due.setText(""+count.due);
                        t_total.setText("" + count.total_amt);
                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        return view;
    }

}
