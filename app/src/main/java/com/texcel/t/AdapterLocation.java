package com.texcel.t;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterLocation extends RecyclerView.Adapter<AdapterLocation.MyHolder>{

    Context context;
    ArrayList<Location_detail> list;


    public AdapterLocation(Context context,ArrayList<Location_detail> list)
    {
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v= LayoutInflater.from(context).inflate(R.layout.location_detail,viewGroup,false);
        return new MyHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {


        myHolder.location.setText(list.get(i).location+" : ");
        myHolder.count.setText(""+list.get(i).count);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class MyHolder extends RecyclerView.ViewHolder
    {

        TextView location,count;

        public MyHolder(View v)
        {
            super(v);

            location=v.findViewById(R.id.location_);
            count=v.findViewById(R.id.l_count);
        }
    }
}
