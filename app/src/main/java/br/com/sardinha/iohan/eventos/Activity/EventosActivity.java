package br.com.sardinha.iohan.eventos.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

public class EventosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ProgressBar progress;
    private TextView noEventToShow;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference followingsReference;
    private DatabaseReference invitationReference;
    private DatabaseReference eventsParticipatingReference;
    private String userID;
    private DatabaseReference eventsReference;

    private Usuario currentUser;

    private Set<String> listIDs = new HashSet<>();

    private ArrayList<Evento> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        drawerLayout = (DrawerLayout)findViewById(R.id.navigation_eventos);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = (NavigationView)findViewById(R.id.navigation_view_eventos);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.meus_eventos:
                        Intent intent1 = new Intent(EventosActivity.this, meusEventosActivity.class);
                        startActivity(intent1);
                        return true;

                    case R.id.usuario:
                        Intent intent = new Intent(EventosActivity.this,UsuarioActivity.class);
                        intent.putExtra("Usuario",currentUser);
                        startActivity(intent);
                        return true;

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
                                firebaseMessaging.unsubscribeFromTopic(userID);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        finish();
                        startActivity(new Intent(EventosActivity.this,MainActivity.class));
                        return true;

                    default:
                        return false;

                }
            }
        });

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
        eventsParticipatingReference = database.getReference("EventsParticipating").child(userID);

        eventsReference = database.getReference("Events");

        list = new ArrayList<>();

        database.getReference("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(Usuario.class);
                CircleImageView userPhoto = (CircleImageView)findViewById(R.id.foto_usuario_header);
                if(userPhoto != null) { // ERROR TO BE FIXED
                    Glide.with(EventosActivity.this).load(currentUser.getImagem()).into(userPhoto);
                    userPhoto.setOnClickListener(new OneShotClickListener() {
                        @Override
                        public void performClick(View v) {
                            Intent intent = new Intent(EventosActivity.this, UsuarioActivity.class);
                            intent.putExtra("Usuario", currentUser);
                            startActivity(intent);
                        }
                    });
                    TextView tv = ((TextView) findViewById(R.id.nome_usuario_header));
                    tv.setText(currentUser.getNome());
                    tv.setOnClickListener(new OneShotClickListener() {
                        @Override
                        public void performClick(View v) {
                            Intent intent = new Intent(EventosActivity.this, UsuarioActivity.class);
                            intent.putExtra("Usuario", currentUser);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();

    }

    private void getData()
    {
        final Set<Evento> eventos = new HashSet<>();
        final long cont[] = new long[4];
        cont[0] = 0;
        cont[1] = 999999999;
        cont[2] = 0;
        cont[3] = 999999999;
        final boolean[] done = {false,false};

        //Eventos dos Seguidores
        invitationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot invitations) {
                followingsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cont[1] = dataSnapshot.getChildrenCount();
                        if(dataSnapshot.getChildrenCount() <= 0)
                        {
                            cont[0] = cont[1];
                            showData(cont,done,eventos);
                        }
                        for(DataSnapshot followerSnapshot : dataSnapshot.getChildren())
                        {
                            Query followerEventQuery = eventsReference.orderByChild("userID").equalTo(followerSnapshot.getKey()).limitToFirst(10);
                            followerEventQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot followerSnapshot) {
                                    for(DataSnapshot eventSnapshot : followerSnapshot.getChildren())
                                    {
                                        Evento e = eventSnapshot.getValue(Evento.class);
                                        DatabaseReference userInvitedReference = database.getReference("UsersInvited").child(e.getId());
                                        if(!listIDs.contains(e.getId()))
                                        {
                                            if(!e.getPrivacidade().equals("Privado") || invitations.hasChild(e.getId()))
                                            {
                                                listIDs.add(e.getId());
                                                eventos.add(eventSnapshot.getValue(Evento.class));
                                            }

                                        }
                                    }
                                    cont[0] += 1;
                                    showData(cont,done,eventos);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            showData(cont,done,eventos);
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

        Calendar calendar = Calendar.getInstance();
        final double dateNow = Double.parseDouble(
                formatador(calendar.get(Calendar.YEAR))
                        +formatador(calendar.get(Calendar.MONTH)+1)
                        +formatador(calendar.get(Calendar.DAY_OF_MONTH))
                        +formatador(calendar.get(Calendar.HOUR_OF_DAY))
                        +formatador(calendar.get(Calendar.MINUTE)));

        //Eventos do usuário
        Query userEventQuery = eventsReference.orderByChild("userID").equalTo(userID).limitToFirst(10);
        userEventQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot eventSnapshot : dataSnapshot.getChildren())
                {
                    final Evento e = eventSnapshot.getValue(Evento.class);
                    if(!e.getDataEncerramento().isEmpty())
                    {
                        double dataHoraEncerramento;
                        String temp1;
                        String[] temp = e.getDataEncerramento().split("/");
                        temp1 = temp[2]+temp[1]+temp[0];
                        temp = e.getHoraEncerramento().split(":");
                        temp1 += temp[0] + temp[1];
                        dataHoraEncerramento = Double.parseDouble(temp1);
                        if(dataHoraEncerramento <= dateNow)
                        {
                            new AlertDialog.Builder(EventosActivity.this)
                                    .setTitle(e.getTitulo())
                                    .setMessage(R.string.parece_que_ja_esta_acontecendo)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(EventosActivity.this,confirmarPresentesActivity.class).putExtra("event",e));
                                            //finish();
                                        }
                                    })
                                    .setNegativeButton(R.string.agora_nao, new DialogInterface.OnClickListener() {
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
                        else if(!listIDs.contains(e.getId()))
                        {
                            listIDs.add(e.getId());
                            eventos.add(eventSnapshot.getValue(Evento.class));
                        }
                    }
                    else if(e.getDataHora()<=dateNow)
                    {
                        new AlertDialog.Builder(EventosActivity.this)
                                .setTitle(e.getTitulo())
                                .setMessage(R.string.parece_que_ja_esta_acontecendo)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(EventosActivity.this,confirmarPresentesActivity.class).putExtra("event",e));
                                        //finish();
                                    }
                                })
                                .setNegativeButton(R.string.agora_nao, new DialogInterface.OnClickListener() {
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
                    else if(!listIDs.contains(e.getId()))
                    {
                        listIDs.add(e.getId());
                        eventos.add(eventSnapshot.getValue(Evento.class));
                    }
                }
                done[0] = true;
                showData(cont,done,eventos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Eventos convidado
        invitationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cont[3] = dataSnapshot.getChildrenCount();
                if(dataSnapshot.getChildrenCount() <= 0)
                {
                    cont[2] = cont[3];
                    showData(cont,done,eventos);
                }
                for(DataSnapshot eventIdSnapshot: dataSnapshot.getChildren())
                {
                    DatabaseReference eventQuery = eventsReference.child(eventIdSnapshot.getKey());
                    eventQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot eventSnapshot) {
                            Evento e = eventSnapshot.getValue(Evento.class);
                            if(e != null && !listIDs.contains(e.getId()))
                            {
                                listIDs.add(e.getId());
                                eventos.add(eventSnapshot.getValue(Evento.class));
                            }
                            cont[2] += 1;
                            showData(cont,done,eventos);
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

        //Eventos confirmado
        eventsParticipatingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot eventSnapshot : dataSnapshot.getChildren())
                {
                    Evento e = eventSnapshot.getValue(Evento.class);
                    if(e != null && !listIDs.contains(e.getId()))
                    {
                        listIDs.add(e.getId());
                        eventos.add(eventSnapshot.getValue(Evento.class));
                    }
                }
                done[1] = true;
                showData(cont,done,eventos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TODO Eventos Eventos que os seguidores vão
    }

    private void showData(long[] cont, boolean[] done, Set<Evento> events)
    {
        if(cont[0] >= cont[1] && cont[2] >= cont[3] && done[0] && done[1])
        {
            list.clear();
            list.addAll(events);
            Collections.sort(list);
            recyclerView.setAdapter(new ListaEventosAdapter(list,this));
            progress.setVisibility(View.GONE);
            if(list.size()<= 0)
            {
                noEventToShow.setVisibility(View.VISIBLE);
            }
        }

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
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private String formatador(int x){
        if (x < 10){
            return "0"+String.valueOf(x);
        }
        return String.valueOf(x);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        finish();
    }
}
