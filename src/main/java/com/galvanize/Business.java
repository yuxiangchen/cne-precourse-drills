package com.galvanize;

import java.util.ArrayList;
import java.util.List;

public class Business implements Addressable {
    private final String name;
    private ArrayList<Address> addresses;

    public Business(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public List<Address> getAddresses() {

        return addresses;
//        return null;
    }

    @Override
    public void addAddress(Address address) {
        addresses.add(address);
//        List<Address> temp = new ArrayList<>();
//        temp.add(address);
    }
}

