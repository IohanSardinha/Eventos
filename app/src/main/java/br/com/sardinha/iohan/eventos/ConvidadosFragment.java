package br.com.sardinha.iohan.eventos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConvidadosFragment extends Fragment {

    private String uID;
    private Evento evento;
    private ArrayList<Usuario> usersList;
    private RecyclerView recyclerView;

    public void setEvento(Evento evento)
    {
        this.evento = evento;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.convidados_fragment,container,false);
        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersList = new ArrayList<>();

        recyclerView = (RecyclerView)view.findViewById(R.id.list_usuarios_convidados);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        DatabaseReference usersInvitedReference = FirebaseDatabase.getInstance().getReference("UsersInvited").child(evento.getId());
        usersInvitedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    usersList.add(ds.getValue(Usuario.class));
                }
                recyclerView.setAdapter(new ListaUsuariosConvidarAdapter(usersList,evento));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
