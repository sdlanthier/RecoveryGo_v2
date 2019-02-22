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

import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.system.IDataInputActivity;


public class NewNoteDialog extends DialogFragment implements View.OnClickListener {

    private EditText mTitle, mContent;

    private IDataInputActivity mIDataInputActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(DialogFragment.STYLE_NORMAL, theme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_note, container, false);
        mTitle = view.findViewById(R.id.note_title);
        mContent = view.findViewById(R.id.note_content);
        TextView mCreate = view.findViewById(R.id.create);
        TextView mCancel = view.findViewById(R.id.cancel);

        mCancel.setOnClickListener(this);
        mCreate.setOnClickListener(this);

        getDialog().setTitle("New Note");

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.create: {

                // insert the new note

                String title = mTitle.getText().toString();
                String content = mContent.getText().toString();

                if (!title.equals("")) {
                    mIDataInputActivity.createNewNote(title, content);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Enter a title", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.cancel: {
                getDialog().dismiss();
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIDataInputActivity = (IDataInputActivity) getActivity();
    }
}





















