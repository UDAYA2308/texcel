package com.texcel.t;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Adapterhost extends RecyclerView.Adapter<Adapterhost.MyHolder>{

    Context context;
    String code;
    ArrayList<Users> list;


    public Adapterhost(Context context,ArrayList<Users> list,String code)
    {
        this.context=context;
        this.list=list;
        this.code=code;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v= LayoutInflater.from(context).inflate(R.layout.host_view,viewGroup,false);
        return new MyHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {


        final String[] s = {"" + list.get(i).name + "\n" + list.get(i).phone +"\ncount : "+list.get(i).count+ "\naccess : " + list.get(i).access};
        myHolder.event.setText(s[0]);

        final int loc=i;
        myHolder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(loc).access)
                {
                    myHolder.check.setImageResource(R.drawable.access_request);
                    list.get(i).access=false;
                    s[0] =""+list.get(i).name+"\n"+list.get(i).phone+"\ncount : "+list.get(i).count+"\naccess : "+list.get(i).access;
                    myHolder.event.setText(s[0]);
                    FirebaseDatabase.getInstance().getReference(code).child("Users").child(list.get(i).phone).setValue(list.get(i));
                }
                else {
                    myHolder.check.setImageResource(R.drawable.access_granted);
                    list.get(i).access=true;
                    s[0] =""+list.get(i).name+"\n"+list.get(i).phone+"\ncount : "+list.get(i).count+"\naccess : "+list.get(i).access;
                    myHolder.event.setText(s[0]);

                    FirebaseDatabase.getInstance().getReference(code).child("Users").child(list.get(i).phone).setValue(list.get(i));
                }
            }

        });

        if(list.get(i).access)
        {
            myHolder.check.setImageResource(R.drawable.access_granted);
        }
        else
        {
            myHolder.check.setImageResource(R.drawable.access_request);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class MyHolder extends RecyclerView.ViewHolder
    {

        TextView event;
        ImageView check;

        public MyHolder(View v)
        {
            super(v);

            event=v.findViewById(R.id.user_details);
            check=v.findViewById(R.id.access_);

        }
    }
}
