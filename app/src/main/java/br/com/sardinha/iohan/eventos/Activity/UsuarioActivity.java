package br.com.sardinha.iohan.eventos.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.sardinha.iohan.eventos.Adapter.ListaEventosAdapter;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.NotificationSender;
import br.com.sardinha.iohan.eventos.Class.OneShotClickListener;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioActivity extends AppCompatActivity {

    private String UID;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private Button followButton;
    private ArrayList<Evento> list;
    private CircleImageView userImage;
    RecyclerView recyclerView;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        database = FirebaseDatabase.getInstance();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        user = (Usuario)intent.getSerializableExtra("Usuario");
        String a = user.getId();
        followButton = (Button)findViewById(R.id.seguir_usuario_descricao);
        ((TextView)findViewById(R.id.nome_usuario_descricao)).setText(user.getNome());
        userReference = database.getReference("Users").child(user.getId());
        userImage = (CircleImageView)findViewById(R.id.foto_usuario_descricao);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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

        if(!user.getImagem().isEmpty())
        {
            Glide.with(UsuarioActivity.this).load(Uri.parse(user.getImagem())).into(userImage);
        }

        if(user.getId().equals(UID)) {
            followButton.setVisibility(View.GONE);
            findViewById(R.id.notification_button).setVisibility(View.GONE);

            ImageView imageView = ((ImageView)findViewById(R.id.foto_usuario_descricao));
            imageView.setOnClickListener(new OneShotClickListener() {
                @Override
                public void performClick(View v) {
                    new AlertDialog.Builder(UsuarioActivity.this).setTitle("Mudar foto de perfil?")
                            .setMessage("Deseja escolher uma nova foto de perfil?")
                            .setCancelable(false)
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent,2);
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
            });
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
                                database.getReference("Users").child(UID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        new NotificationSender().sendFollowMessage(UsuarioActivity.this,null,user,dataSnapshot.getValue(Usuario.class));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        final Map<String,Evento> eventsSet = new HashMap<>();
        final boolean[] done = {false,false,false};

        recyclerView = (RecyclerView)findViewById(R.id.eventos_usuario_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference eventsParticipatedReference = database.getReference("EventsParticipated").child(user.getId());
        eventsParticipatedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Evento e = ds.getValue(Evento.class);
                    if((!e.getPrivacidade().equals("Privado") && !user.getId().equals(UID)))
                    {
                        eventsSet.put(e.getId(),e);
                    }
                    else if(user.getId().equals(UID))
                    {
                        eventsSet.put(e.getId(),e);
                    }
                }
                done[0] = true;
                doneGettingEvents(done,eventsSet);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query userEventsReference = database.getReference("Events").orderByChild("userID").equalTo(user.getId()).limitToFirst(10);
        userEventsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Evento e = ds.getValue(Evento.class);
                    if((!e.getPrivacidade().equals("Privado") && !user.getId().equals(UID)))
                    {
                        eventsSet.put(e.getId(),e);
                    }
                    else if(user.getId().equals(UID))
                    {
                        eventsSet.put(e.getId(),e);
                    }
                }
                done[1] = true;
                doneGettingEvents(done,eventsSet);
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
                    Evento e = ds.getValue(Evento.class);
                    if((!e.getPrivacidade().equals("Privado") && !user.getId().equals(UID)))
                    {
                        eventsSet.put(e.getId(),e);
                    }
                    else if(user.getId().equals(UID))
                    {
                        eventsSet.put(e.getId(),e);
                    }
                }
                done[2] = true;
                doneGettingEvents(done,eventsSet);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    public void doneGettingEvents(boolean[] done, Map<String,Evento> map)
    {
        if(done[0] && done[1] && done[2])
        {
            list = new ArrayList<>();
            list.addAll(map.values());
            Collections.sort(list);
            recyclerView.setAdapter(new ListaEventosAdapter(list,UsuarioActivity.this,user));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            final Uri image;
            image = data.getData();
            userImage.setImageURI(image);
            final ProgressDialog progress = ProgressDialog.show(this,"Salvando","Um momento por favor...",true);
            StorageReference reference = FirebaseStorage.getInstance().getReference("Users").child(user.getId());
            reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final boolean[] done = {false,false,false};
                    user.setImagem(taskSnapshot.getDownloadUrl().toString());
                    final Map<String,Object> updateMap = new HashMap<String, Object>();
                    updateMap.put("Users/"+user.getId(),user);

                    DatabaseReference eventsParticipating = database.getReference("EventsParticipating").child(user.getId());
                    eventsParticipating.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                updateMap.put("UsersParticipating/"+ds.getKey()+"/"+user.getId(),user);
                            }
                            done[0] = true;
                            doneUpdatingImage(done,updateMap,progress);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    DatabaseReference eventsInvited = database.getReference("EventsInvited").child(user.getId());
                    eventsInvited.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds:dataSnapshot.getChildren())
                            {
                                updateMap.put("UsersInvited/"+ds.getKey()+"/"+user.getId(),user);
                            }
                            done[1] = true;
                            doneUpdatingImage(done,updateMap,progress);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Query followings = database.getReference("Followings").orderByChild(user.getId()).startAt(user.getId()).endAt(user.getId());
                    followings.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds:dataSnapshot.getChildren())
                            {
                                System.out.println(ds.getKey());
                                updateMap.put("Followings/"+ds.getKey()+"/"+user.getId(),user);
                            }
                            done[2] = true;
                            doneUpdatingImage(done,updateMap,progress);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    }

    private void doneUpdatingImage(boolean[] done, Map<String,Object> updateMap, final ProgressDialog progressDialog)
    {
        if(done[0] && done[1] && done[2])
        {
            database.getReference().updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                }
            });
        }
    }

}
