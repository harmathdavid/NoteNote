package projoect.harmath.notenotesyn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Harmath on 2017. 09. 17..
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, MyService.class);
        context.startService(service);
    }
}
