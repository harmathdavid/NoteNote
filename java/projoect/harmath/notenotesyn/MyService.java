package projoect.harmath.notenotesyn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harmath on 2017. 09. 17..
 */

public class MyService extends Service implements IFirebaseListListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(this, "create service", Toast.LENGTH_SHORT).show();

        NoteDB db= new NoteDB();
        db.getAll(this);

        return Service.START_STICKY;
    }

    @Override
    public void getList(DataSnapshot dataSnapshot) {
        ArrayList<Note> list = new ArrayList<>();

        for (DataSnapshot item : dataSnapshot.getChildren()) {
            try {

                if (item.exists()) {
                    String id = item.getKey();
                    String title = item.child("title").getValue(String.class);
                    String desc = item.child("desc").getValue(String.class);
                    int icon = item.child("icon").getValue(Integer.class);
                    HashMap<String, String> map = (HashMap<String, String>) item.child("connectedPhones").getValue();

                    Note n = new Note(id, title, desc, icon);
                    n.setConnectedPhones(map);

                    list.add(n);

                }

            } catch (NullPointerException ex) {


            } catch (Exception e) {

            }
        }

        for (int j = 0; j < list.size(); j++) {
            if (list.get(j).getConnectedPhones()!=null && list.get(j).getConnectedPhones().containsKey(NoteHelper.getID(MyService.this))) {
                Notification noti = NoteHelper.noti(getApplicationContext(), list.get(j).getIcon(), list.get(j).getId(), list.get(j).getTitle());

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                manager.notify(list.get(j).getId(), 0, noti);

            }

        }
    }


    @Override
    public void Cancel(DatabaseError e) {

    }
}
