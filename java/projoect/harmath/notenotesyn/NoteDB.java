package projoect.harmath.notenotesyn;

import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Harmath on 2017. 09. 14..
 */

public class NoteDB {

    private static final String NODE="notes";

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase;

    public NoteDB() {
        mFirebaseInstance=FirebaseDatabase.getInstance();
        //mFirebaseInstance.setPersistenceEnabled(true);
        mFirebaseDatabase=mFirebaseInstance.getReference(NODE);
        mFirebaseDatabase.keepSynced(true);
    }


    public String Add(Note note, final IFirebaseAddListener listener, final boolean exit)
    {
        String reference= mFirebaseDatabase.push().getKey();
        mFirebaseDatabase.child(reference).setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onFirebaseAddSucces(exit);
            }
        });

        return reference;
    }

    public void getAll(final IFirebaseListListener listener)
    {
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.getList(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.Cancel(databaseError);
            }
        });
    }

    public void getNoteByID(String id, final IFirebaseObjectListener obj)
    {
        mFirebaseDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                obj.getDbObject(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                obj.Cancel(databaseError);
            }
        });
    }

    public void delete(String id, final IFirebaseDeleteListener listener)
    {
        mFirebaseDatabase.child(id).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onFirebaseDeleteSucces();
            }
        });
    }

    public void updateDesc(String id, String text, final IFirebaseModifyListener listener)
    {
        mFirebaseDatabase.child(id).child("desc").setValue(text).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onFirebaseModifySucces(true);
            }
        });
    }

    public void updateTitle(String id, String text, final IFirebaseModifyListener listener)
    {
        mFirebaseDatabase.child(id).child("title").setValue(text).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onFirebaseModifySucces(false);
            }
        });
    }

    public void updateNote(Note note,final boolean updateAndExit,final IFirebaseModifyListener listener)
    {
        try {
            mFirebaseDatabase.child(note.getId()).setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listener.onFirebaseModifySucces(updateAndExit);
                }
            });
        }
        catch (WindowManager.BadTokenException e){}
    }

    public void updateIcon(String id,int ico)
    {
        mFirebaseDatabase.child(id).child("icon").setValue(ico);
    }

    public void Register(String id,String phoneId)
    {
        //String reference= mFirebaseDatabase.child(id).child("connectedPhones").push().getKey();
        HashMap<String,String>map= new HashMap<>();
        map.put(phoneId,phoneId);

        mFirebaseDatabase.child(id).child("connectedPhones").child(phoneId).setValue(phoneId);
    }

    public void DeleteRegister(String id,String connectedPhoneId)
    {
        mFirebaseDatabase.child(id).child("connectedPhones").child(connectedPhoneId).setValue(null);
    }

}
