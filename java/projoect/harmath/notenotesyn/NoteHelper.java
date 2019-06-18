package projoect.harmath.notenotesyn;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;


public class NoteHelper {

    public static String getID(Context ctx)
    {
       return Secure.getString(ctx.getContentResolver(),
                Secure.ANDROID_ID);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Notification noti(Context ctx,int picture, String id, String title)
    {
        Intent intent= new Intent(ctx,Details.class);
        intent.putExtra("id",id);
        PendingIntent pindent=PendingIntent.getActivity(ctx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti= new Notification.Builder(ctx)
                .setContentTitle(title)
                .setSmallIcon(picture)
                .setContentIntent(pindent)
                .build();

        noti.flags|= Notification.FLAG_ONGOING_EVENT;

        return noti;
    }
}
