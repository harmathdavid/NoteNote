package projoect.harmath.notenotesyn;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;

public class NoteList extends AppCompatActivity implements IFirebaseListListener,IFirebaseDeleteListener {

    NoteDB db;
    ArrayList<Note> list;
    NoteListAdapter adapter;
    ListView listView;
    Context ctx=this;
    NotificationManager manager;
    Note note_item=null;
    final int menu_delete = 1;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        progressDialog=new ProgressDialog(NoteList.this);
        progressDialog.setMessage("Kérlek várj...");
        progressDialog.show();

        listView= (ListView) findViewById(R.id.listview);
        db= new NoteDB();

        list=new ArrayList<Note>();

        registerForContextMenu(listView);

        manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        db.getAll(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note n= (Note) listView.getItemAtPosition(position);

                //Toast.makeText(NoteList.this, n.getDesc()+n.getTitle(), Toast.LENGTH_SHORT).show();
                Intent i =new Intent(NoteList.this,Details.class);
                i.putExtra("id",n.getId());
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //1.AZ id ha kulonleges kinezetet akarok,2.hanyadik sorba,3."egyéb",4.nev


        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(list.get(((AdapterView.AdapterContextMenuInfo) menuInfo).position).getTitle());

        menu.add(Menu.NONE, menu_delete, Menu.NONE, "Töröl");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        note_item = list.get((int) info.id);
        if (item.getItemId() == menu_delete) {

            if ((note_item.getConnectedPhones().size() == 1 && note_item.getConnectedPhones().containsKey(NoteHelper.getID(ctx))) || (note_item.getConnectedPhones().size() == 0)) {
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
                                db.delete(note_item.getId(), NoteList.this);

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


        return super.onContextItemSelected(item);
    }

    private void refresh()
    {
        adapter= new NoteListAdapter(NoteList.this,R.layout.title_list,list);
        listView.setAdapter(adapter);
    }

    @Override
    public void getList(DataSnapshot dataSnapshot) {
        list.clear();



        for(DataSnapshot item : dataSnapshot.getChildren()){
            try{

                if(item.exists()) {
                    String id = item.getKey();
                    String title = item.child("title").getValue(String.class);
                    String desc = item.child("desc").getValue(String.class);
                    int icon = item.child("icon").getValue(Integer.class);
                    HashMap<String,String> map=(HashMap<String, String>) item.child("connectedPhones").getValue();

                    Note n=new Note(id, title, desc, icon);
                    n.setConnectedPhones(map);

                    list.add(n);

                }
                refresh();
            }
            catch (NullPointerException ex) {
                Toast.makeText(ctx, "Hiba történt(null)!"+ex.getMessage(), Toast.LENGTH_SHORT).show();

            }
            catch (Exception e){
                Toast.makeText(ctx, "Hiba történt!", Toast.LENGTH_SHORT).show();
            }
        }



        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if(list.size()==0){
           // Toast.makeText(ctx, "Nincs megjeleníthető elem", Toast.LENGTH_SHORT).show();
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
        manager.cancel(note_item.getId(),0);
        adapter.notifyDataSetChanged();
    }
}
