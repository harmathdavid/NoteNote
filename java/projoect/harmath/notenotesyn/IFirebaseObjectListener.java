package projoect.harmath.notenotesyn;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface IFirebaseObjectListener {

    void getDbObject(DataSnapshot dataSnapshot);
    void Cancel(DatabaseError e);
}
