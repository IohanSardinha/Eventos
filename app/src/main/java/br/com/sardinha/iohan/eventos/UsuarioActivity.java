package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsuarioActivity extends AppCompatActivity {

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        //Usuario user = (Usuario)intent.getSerializableExtra("Usuario");
        //((TextView)findViewById(R.id.nome_usuario_descricao)).setText(user.getNome());
        reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.nome_usuario_descricao)).setText(dataSnapshot.getValue(Usuario.class).getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UsuarioActivity.this, "Erro ao carregar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
