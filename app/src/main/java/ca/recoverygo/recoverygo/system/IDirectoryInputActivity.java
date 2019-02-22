package ca.recoverygo.recoverygo.system;

import ca.recoverygo.recoverygo.models.Entry;

public interface IDirectoryInputActivity {

    void createNewEntry(String name, String street, String city, String prov, String pcode, String phone, String web,
                        String bedsttl, String bedsrepair, String bedspublic, String waittime,
                        String gender, String nextavail);

    void updateEntry(Entry entry);

    void onEntrySelected(Entry entry);

    void deleteEntry(Entry entry);
}
