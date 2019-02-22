package ca.recoverygo.recoverygo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Entry implements Parcelable {
    private String name, street, city, prov, pcode, phone, web, nextavail, gender;
    private String bedsttl, bedsrepair, bedspublic, waittime;
    private @ServerTimestamp
    Date timestamp;
    private String entry_id;

    public Entry(String name, String street, String city, String prov, String pcode, String phone, String web,
                 String bedsttl, String bedsrepair, String bedspublic, String waittime,
                 String gender, String nextavail,
                 Date timestamp, String entry_id) {

        this.name = name;
        this.street = street;
        this.city = city;
        this.prov = prov;
        this.pcode = pcode;
        this.phone = phone;
        this.web = web;
        this.bedsttl = bedsttl;
        this.bedsrepair = bedsrepair;
        this.bedspublic = bedspublic;
        this.waittime = waittime;
        this.gender = gender;
        this.nextavail = nextavail;

        this.timestamp = timestamp;
        this.entry_id = entry_id;
    }

    public Entry() {

    }

    private Entry(Parcel in) {
        name = in.readString();
        street = in.readString();
        city = in.readString();
        prov = in.readString();
        pcode = in.readString();
        phone = in.readString();
        web = in.readString();
        bedsttl = in.readString();
        bedsrepair = in.readString();
        bedspublic = in.readString();
        waittime = in.readString();
        gender = in.readString();
        nextavail = in.readString();
        entry_id = in.readString();
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getBedsttl() {
        return bedsttl;
    }

    public void setBedsttl(String bedsttl) {
        this.bedsttl = bedsttl;
    }

    public String getBedsrepair() {
        return bedsrepair;
    }

    public void setBedsrepair(String bedsrepair) {
        this.bedsrepair = bedsrepair;
    }

    public String getBedspublic() {
        return bedspublic;
    }

    public void setBedspublic(String bedspublic) {
        this.bedspublic = bedspublic;
    }

    public String getWaittime() {
        return waittime;
    }

    public void setWaittime(String waittime) {
        this.waittime = waittime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNextavail() {
        return nextavail;
    }

    public void setNextavail(String nextavail) {
        this.nextavail = nextavail;
    }

    public String getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(String entry_id) {
        this.entry_id = entry_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(street);
        parcel.writeString(city);
        parcel.writeString(prov);
        parcel.writeString(pcode);
        parcel.writeString(phone);
        parcel.writeString(web);
        parcel.writeString(bedsttl);
        parcel.writeString(bedsrepair);
        parcel.writeString(bedspublic);
        parcel.writeString(waittime);
        parcel.writeString(gender);
        parcel.writeString(nextavail);
        parcel.writeString(entry_id);
    }
}