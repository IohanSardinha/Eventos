package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsuarioActivity extends AppCompatActivity {

    private String UID;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private Button followButton;
    private ArrayList<Evento> list;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        database = FirebaseDatabase.getInstance();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        final Usuario user = (Usuario)intent.getSerializableExtra("Usuario");
        followButton = (Button)findViewById(R.id.seguir_usuario_descricao);
        ((TextView)findViewById(R.id.nome_usuario_descricao)).setText(user.getNome());
        userReference = database.getReference("Users").child(user.getId());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                float comp = usuario.getEventosComparecidos();
                float conf = usuario.getEventosConfirmados();
                System.out.println(comp+"   "+conf);
                float confiabilidade =  Math.round(comp/conf*100);
                ((TextView)findViewById(R.id.indice_de_presenca_usuario)).setText("Presença: "+String.valueOf(confiabilidade)+"%");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(user.getId().equals(UID)) {
            followButton.setVisibility(View.GONE);
            findViewById(R.id.notification_button).setVisibility(View.GONE);
        }
        else
        {
            final DatabaseReference authFollowers;
            authFollowers = database.getReference("Followings").child(UID);
            authFollowers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user.getId()))
                    {
                        followButton.setText("Seguindo");
                        followButton.setBackgroundResource(R.color.colorPrimary);
                        followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                authFollowers.child(user.getId()).removeValue();
                            }
                        });
                    }
                    else
                    {
                        followButton.setText("Seguir");
                        followButton.setBackgroundResource(R.color.button);
                        followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                authFollowers.child(user.getId()).setValue(user.getId());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        list = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.eventos_usuario_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference eventsParticipatedReference = database.getReference("EventsParticipated").child(user.getId());
        eventsParticipatedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Evento.class));

                }
                recyclerView.setAdapter(new ListaEventosAdapter(list,UsuarioActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference userEventsReference = database.getReference("Events").child(user.getId());
        userEventsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Evento.class));

                }
                recyclerView.setAdapter(new ListaEventosAdapter(list,UsuarioActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference userParticipatingEventsReference = database.getReference("EventsParticipating").child(user.getId());
        userParticipatingEventsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Evento.class));
                }
                recyclerView.setAdapter(new ListaEventosAdapter(list,UsuarioActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setNotificate(View view) {

    }
}
