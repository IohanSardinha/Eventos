package br.com.sardinha.iohan.eventos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class EventosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ProgressBar progress;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String userID;

    ArrayList<Evento> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        setTitle("Eventos");
        progress = (ProgressBar)findViewById(R.id.progressBar2);
        progress.setVisibility(View.VISIBLE);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userID = auth.getCurrentUser().getUid();
        reference = database.getReference("Events").child(userID);


        list = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(list,this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_eventos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.signout:
                auth.signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showData(DataSnapshot dataSnapshot)
    {
        list = new ArrayList<>();
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {
            list.add(ds.getValue(Evento.class));
        }
        //list.add(evento);
        recyclerView.setAdapter(new Adapter(list,this));
        progress.setVisibility(View.GONE);
    }

    public void novoEvento(View view) {
        startActivity(new Intent(this,NovoEventoActivity.class));
    }
}
