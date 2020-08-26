package com.texcel.t;

import java.util.List;

public class genuser {

    public List<String> host;
    public List<String > user;
    public String phone;
    public String name;

    public  genuser()
    {

    }

    public genuser(String name,String phone,List<String> host,List<String> user)
    {
        this.name=name;
        this.phone=phone;
        this.host=host;
        this.user=user;
    }

}
