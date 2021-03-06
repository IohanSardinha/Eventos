package br.com.sardinha.iohan.eventos.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Adapter.ListaUsuariosConvidarAdapter;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;

public class ConfirmadosFragment extends Fragment {

    private ListaUsuariosConvidarAdapter adapter;
    private ArrayList<Usuario> usersList;
    private RecyclerView recyclerView;
    private Evento evento;
    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setEvento(Evento evento)
    {
        this.evento = evento;
    }

    public void filter(String query)
    {
        query = query.toLowerCase();
        ArrayList<Usuario> newList = new ArrayList<>();
        for(Usuario user : usersList)
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.confirmados_fragment,container,false);

        usersList = new ArrayList<>();

        recyclerView = (RecyclerView)view.findViewById(R.id.lista_usuarios_confirmados);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final DatabaseReference confirmados = FirebaseDatabase.getInstance().getReference("UsersParticipating").child(evento.getId());
        confirmados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    usersList.add(ds.getValue(Usuario.class));
                }
                adapter = new ListaUsuariosConvidarAdapter(view.getContext(),usersList,evento,usuario,true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
