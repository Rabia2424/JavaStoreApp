package com.store.storeapp.Models;

public class AddressMapper {
    public static AddressSnapshot toSnapshot(UserAddress a){
        AddressSnapshot s = new AddressSnapshot();
        s.setFullName(a.getFullName());
        s.setLine1(a.getLine1());
        s.setLine2(a.getLine2());
        s.setDistrict(a.getDistrict());
        s.setCity(a.getCity());
        s.setCountry(a.getCountry());
        s.setPostalCode(a.getPostalCode());
        s.setPhone(a.getPhone());
        return s;
    }
}
