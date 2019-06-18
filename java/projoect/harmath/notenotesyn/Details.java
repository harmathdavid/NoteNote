package projoect.harmath.notenotesyn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;

public class Details extends AppCompatActivity implements IFirebaseObjectListener,IFirebaseDeleteListener,IFirebaseModifyListener {

    NoteDB db;
    Note n;
    EditText title;
    EditText desc;
    Context ctx = this;
    GridView gridView;
    int[] imagesO;
    int[] imagesW;
    Vibrator vibrate;
    MediaPlayer player;
    Notification noti;
    NotificationManager manager;
    Intent intent;
    ImageButton keyboard;
    boolean focusable=true;
    ProgressDialog progressDialog;
    String noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        intent = getIntent();
        db = new NoteDB();
        noteId=intent.getExtras().getString("id");
        db.getNoteByID(noteId,this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        title = (EditText) findViewById(R.id.edit_title2);
        desc = (EditText) findViewById(R.id.edit_desc2);
        keyboard=(ImageButton) findViewById(R.id.keyboard);


        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        player = MediaPlayer.create(this, R.raw.trill);

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        uploadArrays();
        desc.setFocusable(false);
        desc.setFocusableInTouchMode(false);
        desc.setClickable(false);

        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(focusable){

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.showSoftInput(desc, InputMethodManager.SHOW_FORCED);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                    focusable=false;
                    desc.setFocusable(true);
                    desc.setFocusableInTouchMode(true);
                    desc.setClickable(true);
                    desc.setSelection(desc.getText().length());

                }
                else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(desc.getWindowToken(), 0);
                    focusable=true;
                    desc.setFocusable(false);
                    desc.setFocusableInTouchMode(false);
                    desc.setClickable(false);

                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.notify) {
            manager.cancel(n.getId(),0);
            ShowColorPickerDialog();
        }
        else if (item.getItemId() == R.id.notify_delete) {
            player.start();
            vibrate.vibrate(100);
            manager.cancel(n.getId(),0);

            //db.DeleteRegister(n.getId(),NoteHelper.getID(ctx));
            n.setDesc(desc.getText().toString());
            n.setTitle(title.getText().toString());
            n.removeConnectedPhonesItem(NoteHelper.getID(ctx));


            db.updateNote(n,false,Details.this);


        }else if(item.getItemId()==R.id.note_delete){
            if((n.getConnectedPhones().size()==1 && n.getConnectedPhones().containsKey(NoteHelper.getID(ctx))) || (n.getConnectedPhones().size()==0))
            {
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ctx);
                }
                builder.setTitle("Törlés")
                        .setMessage("Biztos, hogy törölni szeretnéd?")
                        .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                manager.cancel(n.getId(),0);
                                db.delete(n.getId(),Details.this);
                            }
                        })
                        .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                Toast.makeText(ctx, "Az értesítést nem lehet törölni mert más készülékeken aktív.", Toast.LENGTH_SHORT).show();
            }
        }


        else {
           RegistMyNote(n.getIcon());
        }


        return super.onOptionsItemSelected(item);
    }

    void ShowColorPickerDialog() {
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.choose_color_dial);
        dialog.setTitle("Válassz ikon színt");

        Button white = (Button) dialog.findViewById(R.id.btn_white);
        Button orange = (Button) dialog.findViewById(R.id.btn_orange);

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

    void ShowGridDialog(final int[] dataset) {

        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.choose_dial);
        dialog.setTitle("Válassz egy ikont");


        gridView = (GridView) dialog.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(ctx, dataset));

        Button dismiss = (Button) dialog.findViewById(R.id.button);
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

                n.setDesc(desc.getText().toString());
                n.setTitle(title.getText().toString());
                n.setIcon(dataset[position]);

                db.updateNote(n,false, Details.this);
                if(n.getConnectedPhones().containsKey(NoteHelper.getID(ctx)))
                    RegistMyNote(dataset[position]);

                dialog.dismiss();
            }


        });


        dialog.show();
    }

    @Override
    public void onBackPressed() {
        //db.updateTitle(noteId, title.getText().toString(),this);
        //db.updateDesc(noteId, desc.getText().toString(),this);
        n.setTitle(title.getText().toString());
        n.setDesc(desc.getText().toString());
        db.updateNote(n,true,this);
    }


    void RegistMyNote(int icon) {

        player.start();
        vibrate.vibrate(100);

        n.setTitle(title.getText().toString());
        n.setDesc(desc.getText().toString());
        n.addConnectedPhonesItem(NoteHelper.getID(ctx));

        db.updateNote(n,false, Details.this);
        Notification notification=NoteHelper.noti(ctx,icon,n.getId(), n.getTitle());

        manager.notify(n.getId(),0,notification);
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


    @Override
    public void getDbObject(DataSnapshot dataSnapshot) {
        if(!isFinishing()) {
            progressDialog = new ProgressDialog(Details.this);
            progressDialog.setMessage("Kérlek várj...");
            progressDialog.show();

            boolean wasException = false;

            String oid = noteId;
            String otitle = "";
            String odesc = "";
            int oicon = 0;
            HashMap<String, String> omap = new HashMap<>();
            for (DataSnapshot item : dataSnapshot.getChildren()) {
                try {

                    if (item.exists()) {


                        switch (item.getKey()) {
                            case "title": {
                                otitle = item.getValue(String.class);
                                break;
                            }
                            case "desc": {
                                odesc = item.getValue(String.class);
                                break;
                            }
                            case "icon": {
                                oicon = item.getValue(Integer.class);
                                break;
                            }
                            case "connectedPhones": {
                                omap = (HashMap<String, String>) item.getValue();
                                break;
                            }
                        }

                    }
                } catch (NullPointerException ex) {
                    Toast.makeText(ctx, "Hiba történt(null)!", Toast.LENGTH_SHORT).show();
                    wasException = true;
                } catch (Exception e) {
                    Toast.makeText(ctx, "Hiba történt!", Toast.LENGTH_SHORT).show();
                    wasException = true;
                }
            }
            if (wasException || dataSnapshot.getChildrenCount() < 1) {
                n = null;
            } else {
                n = new Note(oid, otitle, odesc, oicon);
                if (omap != null) {
                    n.setConnectedPhones(omap);
                }
            }



            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (n != null) {
                title.setText(n.getTitle());
                desc.setText(n.getDesc());

                title.setSelection(title.getText().length());
                desc.setSelection(desc.getText().length());
            }
        }

    }


    @Override
    public void Cancel(DatabaseError e) {
        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onFirebaseDeleteSucces() {

        Intent in=new Intent(Details.this,NoteList.class);
        startActivity(in);
        finish();
    }

    @Override
    public void onFirebaseModifySucces(boolean updateAndExit) {

        if(!isFinishing() && updateAndExit) {
            Intent i = new Intent(Details.this, NoteList.class);
            startActivity(i);
            finish();
        }
    }
}
