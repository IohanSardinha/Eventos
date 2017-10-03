package br.com.sardinha.iohan.eventos.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Adapter.confirmarPresentesAdapter;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;

public class confirmarPresentesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Usuario> list = new ArrayList<>();
    private Evento event;
    private DatabaseReference eventsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_presentes);
        setTitle("Confirmar presentes");

        recyclerView = (RecyclerView)findViewById(R.id.lista_confirmar_presenca);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        event = (Evento)intent.getSerializableExtra("event");
        eventsReference = FirebaseDatabase.getInstance().getReference("Events").child(event.getId());
        ((TextView)findViewById(R.id.confirmar_presentes_titulo)).setText("Confirme os presentes em "+event.getTitulo());
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("UsersParticipating").child(event.getId());
         usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Usuario.class));
                }
                recyclerView.setAdapter(new confirmarPresentesAdapter(list,event,confirmarPresentesActivity.this));
                if(list.size() == 0)
                {
                    final AlertDialog alertDialog = new AlertDialog.Builder(confirmarPresentesActivity.this)
                            .setTitle("Obrigado por contribuir")
                            .setMessage("Sua contribuição é muito importante para o funcionamento da rede!")
                            .setPositiveButton("Ok",new AlertDialog.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(confirmarPresentesActivity.this,EventosActivity.class));
                                    finish();
                                }
                            })
                            .show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    eventsReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
