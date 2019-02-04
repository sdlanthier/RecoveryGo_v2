package ca.recoverygo.recoverygo.system;

import ca.recoverygo.recoverygo.models.Note;

public interface IDataInputActivity {

    void createNewNote(String title, String content);

    void updateNote(Note note);

    void onNoteSelected(Note note);

    void deleteNote(Note note);
}
