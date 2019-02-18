package ca.recoverygo.recoverygo.system;

import com.google.firebase.firestore.GeoPoint;

import ca.recoverygo.recoverygo.models.Meeting;

public interface IMeetingInputActivity {

    void createNewMeeting(String group, String site, String org, String note, String user, GeoPoint marker, GeoPoint location, String location_id, String address);

    void updateMeeting(Meeting meeting);

    void onMeetingSelected(Meeting meeting);

    void deleteMeeting(Meeting meeting);
}
