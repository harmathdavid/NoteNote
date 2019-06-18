package projoect.harmath.notenotesyn;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateNote extends AppCompatActivity implements IFirebaseAddListener {

    EditText title;
    EditText desc;
    Context ctx=this;
    GridView gridView;
    int[] imagesO;
    int[] imagesW;
    Vibrator vibrate;
    MediaPlayer player;
    NotificationManager manager;
    NoteDB db;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        title= (EditText) findViewById(R.id.edit_title);
        desc= (EditText) findViewById(R.id.edit_desc);



        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        vibrate=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        player=MediaPlayer.create(this,R.raw.trill);

        manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        db=new NoteDB();

        uploadArrays();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_lay,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.notify){

            ShowColorPickerDialog();

        }

        else if(item.getItemId()==R.id.confirm){

            SaveWithoutIcon();


        }



        return super.onOptionsItemSelected(item);
    }



    void ShowColorPickerDialog(){
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.choose_color_dial);
        dialog.setTitle("Válassz ikon színt");

        Button white= (Button) dialog.findViewById(R.id.btn_white);
        Button orange= (Button) dialog.findViewById(R.id.btn_orange);

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowGridDialog(imagesW);
                dialog.dismiss();
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowGridDialog(imagesO);
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    void ShowGridDialog(final int[] dataset){

        dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.choose_dial);
        dialog.setTitle("Válassz egy ikont");


        gridView=(GridView) dialog.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(ctx,dataset));

        Button dismiss= (Button) dialog.findViewById(R.id.button);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "teszt" + position,
                //       Toast.LENGTH_SHORT).show();

                String t=title.getText().toString();
                String d=desc.getText().toString();
                int i=dataset[position];




                SimpleDateFormat simple = new SimpleDateFormat("MM-dd HH:mm");
                Date date = new Date();


                //if (title.getText().length() == 0)
                title.setText(title.getText()+" [ "+simple.format(date)+" ]");

                Note n = new Note(title.getText().toString(), desc.getText().toString(),i);
                n.addConnectedPhonesItem(NoteHelper.getID(ctx));

                String retId=db.Add(n,CreateNote.this,false);
                n.setId(retId);

                player.start();
                vibrate.vibrate(100);

                manager.notify(n.getId(),0,NoteHelper.noti(ctx,n.getIcon(),n.getId(),n.getTitle()));

            }


        });


        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if(desc.length()>0)
        {
            SaveWithoutIcon();
        }
        else
        {
            super.onBackPressed();
        }

    }

    void uploadArrays(){
        TypedArray ar = ctx.getResources().obtainTypedArray(R.array.images_oorange);
        int len = ar.length();

        imagesO = new int[len];

        for (int i = 0; i < len; i++)
            imagesO[i] = ar.getResourceId(i, 0);

        ar.recycle();

        TypedArray arr = ctx.getResources().obtainTypedArray(R.array.imgages_white);
        len = arr.length();

        imagesW = new int[len];

        for (int i = 0; i < len; i++)
            imagesW[i] = arr.getResourceId(i, 0);

        arr.recycle();
    }

    void SaveWithoutIcon() {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date();

        if (desc.getText().length() > 0) {

            //if (title.getText().length() == 0)
            title.setText(title.getText()+" [ "+simple.format(d)+" ]");

            Note n = new Note(title.getText().toString(), desc.getText().toString(), R.drawable.default_icon);
            db.Add(n,CreateNote.this,true);
        }
    }


    @Override
    public void onFirebaseAddSucces(boolean exit) {
        try
        {
            if(dialog!=null && dialog.isShowing())
            {
                dialog.dismiss();
            }
            if(this.hasWindowFocus())
            {
                this.closeOptionsMenu();
            }
            if(exit)
            {
                super.onBackPressed();
            }
            else
            {

                Intent intent = new Intent(CreateNote.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        catch (Exception e)
        {

        }

    }
}
