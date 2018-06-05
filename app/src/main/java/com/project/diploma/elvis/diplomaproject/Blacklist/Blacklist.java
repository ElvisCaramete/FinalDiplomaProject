package com.project.diploma.elvis.diplomaproject.Blacklist;

/**
 * Created by Elvis on 1/1/2018.
 */

public class Blacklist {

    public long id;
    public String name;
    public String phoneNumber;

    public Blacklist() {

    }

    public Blacklist(final String name, final String phoneMumber) {
        this.phoneNumber = phoneMumber;
        this.name=name;
    }

    public Blacklist(final String phoneMumber) {
        this.phoneNumber = phoneMumber;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj.getClass().isInstance(new Blacklist()))
        {

            final Blacklist bl = (Blacklist) obj;
            if(bl.phoneNumber.equalsIgnoreCase(this.phoneNumber))
                return true;
        }
        return false;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
}
