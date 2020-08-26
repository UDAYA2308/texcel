package com.texcel.t;

public class Day_count {

    public  int count;
    public long total_amt;
    public long cash;
    public int date;
    public long online;
    public Day_count(){

    }

    public Day_count(int count,long total_amt,long cash,long online,int date)
    {
        this.count=count;
        this.cash=cash;
        this.date=date;
        this.online=online;
        this.total_amt=total_amt;
    }

    public void reset()
    {
        total_amt=cash=online=date=count=0;
    }

}
