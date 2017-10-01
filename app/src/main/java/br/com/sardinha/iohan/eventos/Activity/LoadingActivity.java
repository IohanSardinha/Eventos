package br.com.sardinha.iohan.eventos.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String eventID = intent.getStringExtra("eventID");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(userID).child(eventID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Evento evento = dataSnapshot.getValue(Evento.class);
                Intent intent1 = new Intent(LoadingActivity.this,DetalhesEventoActivity.class);
                intent1.putExtra("evento",evento);
                startActivity(intent1);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
