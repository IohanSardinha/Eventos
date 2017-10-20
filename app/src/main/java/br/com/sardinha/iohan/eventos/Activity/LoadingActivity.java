package br.com.sardinha.iohan.eventos.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String eventID = intent.getStringExtra("eventID");
        String actiontype = intent.getStringExtra("actionType");
        try
        {
            FirebaseAuth.getInstance().getCurrentUser();
        }
        catch (Exception ex)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        if(actiontype.equals("NOVO_EVENTO")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(eventID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Evento evento = dataSnapshot.getValue(Evento.class);
                    Intent intent1 = new Intent(LoadingActivity.this, DetalhesEventoActivity.class);
                    intent1.putExtra("evento", evento);
                    startActivity(intent1);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(actiontype.equals("SEGUIDO"))
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario user = dataSnapshot.getValue(Usuario.class);
                    Intent intent1 = new Intent(LoadingActivity.this,UsuarioActivity.class);
                    intent1.putExtra("Usuario",user);
                    startActivity(intent1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
}
