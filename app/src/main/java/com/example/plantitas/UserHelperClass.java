package com.example.plantitas;

public class UserHelperClass {
   public String email,fullname,address,contact,gender;
    public UserHelperClass() {

    }

    public UserHelperClass(String email, String fullname, String address, String contact, String gender) {
        this.email = email;
        this.fullname = fullname;
        this.address = address;
        this.contact = contact;
        this.gender = gender;

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }



    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


}
