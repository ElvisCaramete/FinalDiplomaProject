package com.project.diploma.elvis.diplomaproject.Whitelist;

/**
 * Created by Elvis on 1/1/2018.
 */

public class Whitelist {

    public long id;
    public String name;
    public String phoneNumber;

    public Whitelist() {

    }

    public Whitelist(final String name, final String phoneMumber) {
        this.phoneNumber = phoneMumber;
        this.name=name;
    }

    public Whitelist(final String phoneMumber) {
        this.phoneNumber = phoneMumber;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj.getClass().isInstance(new Whitelist()))
        {

            final Whitelist wl = (Whitelist) obj;
            if(wl.phoneNumber.equalsIgnoreCase(this.phoneNumber))
                return true;
        }
        return false;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
}
