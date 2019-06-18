package projoect.harmath.notenotesyn;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface IFirebaseListListener {

    void getList(DataSnapshot dataSnapshot);
    void Cancel(DatabaseError e);
}
