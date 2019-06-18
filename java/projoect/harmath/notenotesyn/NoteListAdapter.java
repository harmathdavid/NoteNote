package projoect.harmath.notenotesyn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harmath on 2017. 09. 14..
 */

public class NoteListAdapter extends ArrayAdapter<Note> implements View.OnCreateContextMenuListener {
    private Context context;
    private int resID;
    private ArrayList<Note> list;



    public NoteListAdapter(Context context1, int resID, ArrayList<Note> list) {
        super(context1, resID, list);
        this.context = context1;
        this.resID = resID;
        this.list = list;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        convertView=inflater.inflate(resID,parent,false);

        TextView tv_title=(TextView) convertView.findViewById(R.id.note_title);
        ImageView imageView=(ImageView) convertView.findViewById(R.id.img);


        String s=list.get(position).getTitle();
        tv_title.setText(s);
        imageView.setBackgroundResource(list.get(position).getIcon());


        return convertView;


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }
}
