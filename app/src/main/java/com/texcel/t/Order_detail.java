package com.texcel.t;

public class Order_detail {

    public String t_shirt;
    public int S;
    public int M;
    public int L;
    public int XL;
    public int total;
    public int XXL;
    public int XXXL;

    public Order_detail()
    {

    }

    public Order_detail(String t_shirt,int S,int M,int L,int XL,int XXL,int XXXL,int total)
    {
        this.t_shirt=t_shirt;
        this.S=S;
        this.M=M;
        this.L=L;
        this.XL=XL;
        this.total=total;
        this.XXL=XXL;
        this.XXXL=XXXL;
    }

}
