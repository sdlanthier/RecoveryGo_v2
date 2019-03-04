package ca.recoverygo.recoverygo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Meeting implements Parcelable {

    private String groupname, site, org, note, user;
    private GeoPoint location;
    private String address, city, location_id;

    public Meeting(String groupname, String site, String org, String note, String user, GeoPoint location, String address,String city) {
        this.groupname = groupname;
        this.site = site;
        this.org = org;
        this.note = note;
        this.user = user;
        this.location = location;
        this.address = address;
        this.city = city;
    }

    public Meeting() {

    }

    private Meeting(Parcel in) {
        groupname   = in.readString();
        site        = in.readString();
        org         = in.readString();
        note        = in.readString();
        user        = in.readString();
        location_id = in.readString();
        address     = in.readString();
        city        = in.readString();
        int[] data  = new int[2];
        in.readIntArray(data);
        location    = new GeoPoint(data[0], data[1]);
    }

    public static final Creator<Meeting> CREATOR = new Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupname);
        parcel.writeString(site);
        parcel.writeString(org);
        parcel.writeString(note);
        parcel.writeString(user);
        parcel.writeString(location_id);
    }
}