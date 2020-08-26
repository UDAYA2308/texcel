package com.texcel.t;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.MyHolder>{

    Context context;
    ArrayList<Order_detail> list;


    public AdapterOrder(Context context,ArrayList<Order_detail> list)
    {
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v= LayoutInflater.from(context).inflate(R.layout.order_detail,viewGroup,false);
        return new MyHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {


        myHolder.type.setText(myHolder.type.getText()+" : "+list.get(i).t_shirt);
        myHolder.total.setText(myHolder.total.getText()+" : "+list.get(i).total);
        myHolder.S.setText(myHolder.S.getText()+" : "+list.get(i).S);
        myHolder.M.setText(myHolder.M.getText()+" : "+list.get(i).M);
        myHolder.L.setText(myHolder.L.getText()+" : "+list.get(i).L);
        myHolder.XL.setText(myHolder.XL.getText()+" : "+list.get(i).XL);
        myHolder.XXL.setText(myHolder.XXL.getText()+" : "+list.get(i).XXL);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class MyHolder extends RecyclerView.ViewHolder
    {

        TextView type,total,S,M,L,XL,XXL;

        public MyHolder(View v)
        {
            super(v);

            type=v.findViewById(R.id.t_type);
            total=v.findViewById(R.id.t1_tcount);
            S=v.findViewById(R.id.t1_S);
            M=v.findViewById(R.id.t1_M);
            L=v.findViewById(R.id.t1_L);
            XL=v.findViewById(R.id.t1_XL);
            XXL=v.findViewById(R.id.t1_XXL);
        }
    }
}
