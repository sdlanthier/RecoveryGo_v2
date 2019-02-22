package ca.recoverygo.recoverygo.models;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.system.IMeetingInputActivity;

public class ViewMeetingDialog extends DialogFragment implements View.OnClickListener {

    EditText mGroup, mSite, mOrg, mNote;

    private IMeetingInputActivity mIMeetingInputActivity;
    private Meeting mMeeting;

    public static ViewMeetingDialog newInstance(Meeting meeting) {
        ViewMeetingDialog dialog = new ViewMeetingDialog();

        Bundle args = new Bundle();
        args.putParcelable("meeting", meeting);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(DialogFragment.STYLE_NORMAL, theme);

        mMeeting = Objects.requireNonNull(getArguments()).getParcelable("meeting");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_meeting, container, false);

        mGroup = view.findViewById(R.id.groupname);
        mSite = view.findViewById(R.id.site);
        mOrg = view.findViewById(R.id.org);
        mNote = view.findViewById(R.id.note);

        TextView mSave = view.findViewById(R.id.save);
        TextView mDelete = view.findViewById(R.id.delete);

        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        getDialog().setTitle("Edit This Meeting");

        setInitialProperties();

        return view;
    }

    private void setInitialProperties() {
        mGroup.setText(mMeeting.getGroupname());
        mSite.setText(mMeeting.getSite());
        mOrg.setText(mMeeting.getOrg());
        mNote.setText(mMeeting.getNote());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.save: {

                String groupname = mGroup.getText().toString();
                String site = mSite.getText().toString();
                String org = mOrg.getText().toString();
                String note = mNote.getText().toString();

                if (!groupname.equals("")) {

                    mMeeting.setGroupname(groupname);
                    mMeeting.setSite(site);
                    mMeeting.setOrg(org);
                    mMeeting.setNote(note);

                    mIMeetingInputActivity.updateMeeting(mMeeting);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Enter a title", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.delete: {
                mIMeetingInputActivity.deleteMeeting(mMeeting);
                getDialog().dismiss();
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMeetingInputActivity = (IMeetingInputActivity) getActivity();
    }


}