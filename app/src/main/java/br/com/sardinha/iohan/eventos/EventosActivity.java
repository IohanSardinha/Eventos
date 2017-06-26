package br.com.sardinha.iohan.eventos;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class EventosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ProgressBar progress;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String userID;
    private DatabaseReference userReference;

    ArrayList<Evento> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        progress = (ProgressBar)findViewById(R.id.progressBar2);
        progress.setVisibility(View.VISIBLE);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userID = auth.getCurrentUser().getUid();
        reference = database.getReference("Events").child(userID);

        userReference = database.getReference("Users");

        list = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(list,this));
    }

    private void showData(DataSnapshot dataSnapshot)
    {
        list = new ArrayList<>();
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {
            list.add(ds.getValue(Evento.class));
        }
        recyclerView.setAdapter(new Adapter(list,this));
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
                Query firebaseQuery = userReference.orderByChild("nome").startAt(query).endAt(query+"\uf8ff").limitToFirst(1);
                firebaseQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            Usuario u = ds.getValue(Usuario.class);
                            Intent intent = new Intent(EventosActivity.this,UsuarioActivity.class);
                            intent.putExtra("Usuario",u);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Erro");
                    }
                });
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
                return true;
            case R.id.usuario:
                startActivity(new Intent(this,UsuarioActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void novoEvento(View view) {
        startActivity(new Intent(this,NovoEventoActivity.class));
    }
}
