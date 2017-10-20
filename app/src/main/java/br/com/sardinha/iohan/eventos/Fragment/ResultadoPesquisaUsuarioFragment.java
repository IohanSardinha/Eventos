package br.com.sardinha.iohan.eventos.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Adapter.ListaUsuariosAdapter;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;


public class ResultadoPesquisaUsuarioFragment extends Fragment {
    private String query;
    private ArrayList<Usuario> list;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;

    private String USER_ID;

    private ListaUsuariosAdapter adapter;

    public void setQuery(String query)
    {
        this.query = query;
    }

    public void filter(String query)
    {
        query = query.toLowerCase();
        ArrayList<Usuario> newList = new ArrayList<>();
        for(Usuario user : list)
        {
            if(user.getNomeLow().contains(query))
            {
                newList.add(user);
            }
        }
        adapter.setFilter(newList);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_resultado_pesquisa_usuario,container,false);

        progressBar = (ProgressBar)view.findViewById(R.id.progress_lista_usuarios);
        progressBar.setVisibility(View.VISIBLE);

        USER_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        list = new ArrayList<>();

        recyclerView = (RecyclerView)view.findViewById(R.id.lista_usuarios_resultado_pesquisa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FirebaseDatabase.getInstance().getReference("Users").child(USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario usuario = dataSnapshot.getValue(Usuario.class);
                databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                Query firebaseQuery = databaseReference.orderByChild("nomeLow").startAt(query).endAt(query+"\uf8ff").limitToFirst(25);
                firebaseQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            Usuario user = ds.getValue(Usuario.class);
                            if(!user.getId().equals(USER_ID))
                            {
                                list.add(user);
                            }
                        }
                        adapter = new ListaUsuariosAdapter(list,view.getContext(),usuario);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
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

        return view;
    }
}
