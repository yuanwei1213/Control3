package com.gengy.control.Entity;

import java.io.Serializable;

/**
 * @date on 2020/1/3
 * 描述       17:15
 * com.gengy.control.Entity
 */
public class PhoneEntity implements Serializable {

    public String name;        //联系人姓名
//    public String telPhone;    //电话号码
    public String   phone;
    public Boolean check;
    public  String  id;
    public  int  type;
    public PhoneEntity(String name, String telPhone,Boolean check) {
        this.name = name;
        this.phone = telPhone;
        this.check=check;

    }
}
