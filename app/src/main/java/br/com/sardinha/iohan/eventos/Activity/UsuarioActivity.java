package br.com.sardinha.iohan.eventos.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Adapter.ListaEventosAdapter;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;

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
            final ImageButton notificateButton = (ImageButton)findViewById(R.id.notification_button);
            final DatabaseReference notificationGroups = database.getReference("NotificationGroups").child(UID);
            notificationGroups.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user.getId()))
                    {
                        notificateButton.setColorFilter(Color.WHITE);
                        notificateButton.setBackgroundResource(R.color.buttonPressed);
                        notificateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notificationGroups.child(user.getId()).removeValue();
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getId()+"-WhenCreateEvent");
                            }
                        });
                    }
                    else
                    {
                        notificateButton.setColorFilter(Color.BLACK);
                        notificateButton.setBackgroundResource(R.color.button);
                        notificateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notificationGroups.child(user.getId()).setValue(user.getId());
                                FirebaseMessaging.getInstance().subscribeToTopic(user.getId()+"-WhenCreateEvent");
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            final DatabaseReference authFollowers;
            authFollowers = database.getReference("Followings").child(UID);
            authFollowers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user.getId()))
                    {
                        followButton.setText("Seguindo");
                        followButton.setBackgroundResource(R.color.buttonPressed);
                        followButton.setTextColor(Color.WHITE);
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
                        followButton.setTextColor(Color.BLACK);
                        followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                authFollowers.child(user.getId()).setValue(user);
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
                recyclerView.setAdapter(new ListaEventosAdapter(list,UsuarioActivity.this,user));
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
                recyclerView.setAdapter(new ListaEventosAdapter(list,UsuarioActivity.this,user));
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
                recyclerView.setAdapter(new ListaEventosAdapter(list,UsuarioActivity.this,user));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
