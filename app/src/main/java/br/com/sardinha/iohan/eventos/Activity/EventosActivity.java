package br.com.sardinha.iohan.eventos.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.sardinha.iohan.eventos.Adapter.ListaEventosAdapter;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.OneShotClickListener;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;

public class EventosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ProgressBar progress;
    private TextView noEventToShow;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference followingsReference;
    private DatabaseReference invitationReference;
    private String userID;
    private DatabaseReference eventsReference;

    private Usuario currentUser;

    private Set<String> listIDs = new HashSet<>();

    ArrayList<Evento> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        progress = (ProgressBar)findViewById(R.id.progressBar2);
        progress.setVisibility(View.VISIBLE);
        noEventToShow = (TextView)findViewById(R.id.nenhum_evento);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        try
        {
            userID = auth.getCurrentUser().getUid();
        }
        catch(Exception ex)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        findViewById(R.id.novoEventoButtunEventosActivity).setOnClickListener(new OneShotClickListener() {
            @Override
            public void performClick(View v) {
                startActivity(new Intent(EventosActivity.this,NovoEventoActivity.class));
            }
        });

        followingsReference = database.getReference("Followings").child(userID);
        invitationReference = database.getReference("EventsInvited").child(userID);

        eventsReference = database.getReference("Events");

        list = new ArrayList<>();

        database.getReference("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        retrieveData();

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ListaEventosAdapter(list,this));
    }

    private void retrieveData()
    {

        Calendar calendar = Calendar.getInstance();
        final double dateNow = Double.parseDouble(
                formatador(calendar.get(Calendar.YEAR))
                        +formatador(calendar.get(Calendar.MONTH)+1)
                        +formatador(calendar.get(Calendar.DAY_OF_MONTH))
                        +formatador(calendar.get(Calendar.HOUR_OF_DAY))
                        +formatador(calendar.get(Calendar.MINUTE)));

        followingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot followersSnapshot) {
                Query eventsQuery = eventsReference.orderByChild("userID").startAt(userID).limitToFirst(10);
                eventsQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userEventSnapshot) {
                        for(DataSnapshot eventSnapshot : userEventSnapshot.getChildren())
                        {
                            final Evento event = eventSnapshot.getValue(Evento.class);
                            if(event.getDataHora()<=dateNow)
                            {
                                new AlertDialog.Builder(EventosActivity.this)
                                        .setTitle(event.getTitulo())
                                        .setMessage("Parece que seu evento  já aconteceu ou está acontecendo. \n\n" +
                                                "Confirme quem realmente foi.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(EventosActivity.this,confirmarPresentesActivity.class).putExtra("event",event));
                                                //finish();
                                            }
                                        })
                                        .setNegativeButton("Agora não", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                eventsReference.child(event.getId()).removeValue();
                                                final DatabaseReference usersParticipatingReference = database.getReference("UsersParticipating").child(event.getId());
                                                final DatabaseReference eventsParticipatingReference = database.getReference("EventsParticipating");
                                                usersParticipatingReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot ds : dataSnapshot.getChildren())
                                                        {
                                                            eventsParticipatingReference.child(ds.getKey()).removeValue();
                                                        }
                                                        usersParticipatingReference.removeValue();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                                list.clear();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                            if(!listIDs.contains(event.getId()))
                            {
                                list.add(event);
                                listIDs.add(event.getId());
                            }

                        }

                        invitationReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot eventsInvitedSnapshot) {
                                progress.setVisibility(View.VISIBLE);
                                for(DataSnapshot follower : followersSnapshot.getChildren())
                                {
                                    Query queryFollower = eventsReference.orderByChild("userID").startAt(follower.getValue(Usuario.class).getId()).limitToFirst(10);
                                    queryFollower.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot followerEventSnapshot) {
                                            for(DataSnapshot eventSnapshot : followerEventSnapshot.getChildren())
                                            {
                                                Evento event = eventSnapshot.getValue(Evento.class);
                                                if(!listIDs.contains(event.getId()))
                                                {
                                                    if(!event.getPrivacidade().equals("Privado") || eventsInvitedSnapshot.hasChild(event.getId()))
                                                    {
                                                        list.add(event);
                                                        listIDs.add(event.getId());
                                                    }

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                Collections.sort(list);
                                recyclerView.setAdapter(new ListaEventosAdapter(list,EventosActivity.this));
                                progress.setVisibility(View.GONE);
                                if(list.size() <= 0)
                                {
                                    noEventToShow.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    noEventToShow.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getData()
    {
        Calendar calendar = Calendar.getInstance();
        final double dateNow = Double.parseDouble(
                         formatador(calendar.get(Calendar.YEAR))
                        +formatador(calendar.get(Calendar.MONTH)+1)
                        +formatador(calendar.get(Calendar.DAY_OF_MONTH))
                        +formatador(calendar.get(Calendar.HOUR_OF_DAY))
                        +formatador(calendar.get(Calendar.MINUTE)));

        eventsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    final Evento e = ds.getValue(Evento.class);
                    if(e.getDataHora()<=dateNow)
                    {
                        new AlertDialog.Builder(EventosActivity.this)
                                .setTitle(e.getTitulo())
                                .setMessage("Parece que seu evento  já aconteceu ou está acontecendo. \n\n" +
                                        "Confirme quem realmente foi.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(EventosActivity.this,confirmarPresentesActivity.class).putExtra("event",e));
                                        //finish();
                                    }
                                })
                                .setNegativeButton("Agora não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        eventsReference.child(e.getId()).removeValue();
                                        final DatabaseReference usersParticipatingReference = database.getReference("UsersParticipating").child(e.getId());
                                        final DatabaseReference eventsParticipatingReference = database.getReference("EventsParticipating");
                                        usersParticipatingReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds : dataSnapshot.getChildren())
                                                {
                                                    eventsParticipatingReference.child(ds.getKey()).removeValue();
                                                }
                                                usersParticipatingReference.removeValue();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        list.clear();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        list.clear();
        followingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    DatabaseReference ref = database.getReference("Events").child(ds.getKey());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            showData(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        eventsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot)
    {
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {
            Evento evento = ds.getValue(Evento.class);
            if(evento.getPrivacidade().equals("Público"))
            {
                //list.add(ds.getValue(Evento.class));
            }
            list.add(ds.getValue(Evento.class));
        }
        Collections.sort(list);
        progress.setVisibility(View.VISIBLE);
        SystemClock.sleep(250);
        recyclerView.setAdapter(new ListaEventosAdapter(list,this));
        progress.setVisibility(View.GONE);
        if(list.size() <= 0)
        {
            noEventToShow.setVisibility(View.VISIBLE);
            return;
        }
        noEventToShow.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_eventos, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_eventos).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(EventosActivity.this,ResultadoPesquisaActivity.class);
                intent.putExtra("Query",query.toLowerCase());
                startActivity(intent);
                //finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.signout:
                auth.signOut();
                DatabaseReference notificationGroupReference = database.getReference("NotificationGroups").child(userID);
                notificationGroupReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                        firebaseMessaging.unsubscribeFromTopic(userID);
                        for(DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            firebaseMessaging.unsubscribeFromTopic(ds.getKey()+"-WhenCreateEvent");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                finish();
                startActivity(new Intent(this,MainActivity.class));
                return true;
            case R.id.usuario:
                Intent intent = new Intent(this,UsuarioActivity.class);
                intent.putExtra("Usuario",currentUser);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*public void novoEvento(View view) {
        startActivity(new Intent(this,NovoEventoActivity.class));
        finish();
    }*/

    private String formatador(int x){
        if (x < 10){
            return "0"+String.valueOf(x);
        }
        return String.valueOf(x);
    }
}
