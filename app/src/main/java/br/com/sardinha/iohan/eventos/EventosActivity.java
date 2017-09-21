package br.com.sardinha.iohan.eventos;

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
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class EventosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ProgressBar progress;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference followingsReference;
    private String userID;
    private DatabaseReference eventsReference;

    private Usuario currentUser;

    ArrayList<Evento> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        progress = (ProgressBar)findViewById(R.id.progressBar2);
        progress.setVisibility(View.VISIBLE);

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
        //FirebaseMessaging.getInstance().subscribeToTopic("teste");
        followingsReference = database.getReference("Followings").child(userID);

        eventsReference = database.getReference("Events").child(userID);

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

        getData();

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ListaEventosAdapter(list,this));
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
                                        finish();
                                    }
                                })
                                .setNegativeButton("Agora não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        eventsReference.child(e.getId()).removeValue();
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
                finish();
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

    public void novoEvento(View view) {
        startActivity(new Intent(this,NovoEventoActivity.class));
        finish();
    }

    private String formatador(int x){
        if (x < 10){
            return "0"+String.valueOf(x);
        }
        return String.valueOf(x);
    }
}
