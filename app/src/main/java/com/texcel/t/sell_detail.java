package com.texcel.t;

public class sell_detail {

    public String regno;
    public String name;
    public String phone;
    public String t_shirt;
    public String size;
    public int amount_paid;
    public String mode_of_payment;
    public String delivery;
    public int due_amount;
    public String timestamp;
    public String code;
    public String date;
    public String location;
    public String reg_name;

    public sell_detail(){}

    public sell_detail(String regno,String name,String phone,String t_shirt,String size,int amount_paid,String mode_of_payment,String delivery,int due_amount,String timestamp,String date,String code,String location,String reg_name)
    {
        this.name=name;
        this.regno=regno;
        this.phone=phone;
        this.location=location;
        this.reg_name=reg_name;
        this.t_shirt=t_shirt;
        this.size=size;
        this.amount_paid=amount_paid;
        this.mode_of_payment=mode_of_payment;
        this.delivery=delivery;
        this.due_amount=due_amount;
        this.timestamp=timestamp;
        this.date=date;
        this.code=code;
    }

}
