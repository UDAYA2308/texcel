package com.texcel.t;

public class total_count {
    public int count;
    public long total_amt;
    public long cash;
    public long online;
    public long due;

    public total_count(){}

    public total_count(int count,long total_amt,long cash,long online,long due)
    {
        this.count=count;
        this.cash=cash;
        this.due=due;
        this.online=online;
        this.total_amt=total_amt;
    }
}
