package projoect.harmath.notenotesyn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton add;
    ImageButton todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add= (ImageButton) findViewById(R.id.btn_add);
        todo=(ImageButton) findViewById(R.id.btn_list);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,CreateNote.class);
                startActivity(i);
                //finish();

            }
        });

        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,NoteList.class);
                startActivity(i);
                //finish();
            }
        });
    }
}
