package com.texcel.t;

public class Users {

    public String name;
    public String reg_no;
    public String phone;
    public int count;
    public boolean access;

    public Users(){}

    public Users(String name,String reg_no,String phone,int count,Boolean access)
    {
        this.name=name;
        this.reg_no=reg_no;
        this.phone=phone;
        this.count=count;
        this.access=access;
    }

}
