package br.com.sardinha.iohan.eventos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultadoPesquisaEventoFragment extends Fragment {
    String query;

    private ArrayList<Evento> list;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;

    public void setQuery(String query)
    {
        this.query = query;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_resultado_pesquisa_evento,container,false);

        progressBar = (ProgressBar)view.findViewById(R.id.progress_lista_eventos);
        progressBar.setVisibility(View.VISIBLE);

        list = new ArrayList<>();

        System.out.println(query);

        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Query firebaseQuery = databaseReference.child(ds.getKey()).orderByChild("titulo").startAt(query).endAt(query+"\uf8ff").limitToFirst(25);
                    firebaseQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            list.clear();
                            for(DataSnapshot dss: dataSnapshot.getChildren())
                            {
                                list.add(dss.getValue(Evento.class));
                            }
                            recyclerView.setAdapter(new ListaEventosAdapter(list,view.getContext()));
                            progressBar.setVisibility(View.GONE);
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

        recyclerView = (RecyclerView)view.findViewById(R.id.lista_eventos_resultado_pesquisa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new ListaEventosAdapter(list,view.getContext()));

        return view;
    }
}
