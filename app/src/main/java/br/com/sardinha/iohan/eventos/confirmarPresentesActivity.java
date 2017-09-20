package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class confirmarPresentesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Usuario> list = new ArrayList<>();
    private String eventID;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_presentes);
        setTitle("Confirmar presentes");

        recyclerView = (RecyclerView)findViewById(R.id.lista_confirmar_presenca);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        usersReference = FirebaseDatabase.getInstance().getReference("UsersParticipating").child(eventID);
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Usuario.class));
                }
                recyclerView.setAdapter(new confirmarPresentesAdapter(list,eventID,confirmarPresentesActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
