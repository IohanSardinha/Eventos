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


public class SeguidoresFragment extends Fragment {

    private String uID;
    private ArrayList<Usuario> usersList;
    private RecyclerView recyclerView;
    private Evento evento;

    public void setEvento(Evento evento)
    {
        this.evento = evento;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.seguidores_fragment,container,false);
        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersList = new ArrayList<>();

        recyclerView = (RecyclerView)view.findViewById(R.id.lista_usuarios_convidar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final DatabaseReference convidados = FirebaseDatabase.getInstance().getReference().child(evento.getId());
        final DatabaseReference followings = FirebaseDatabase.getInstance().getReference("Followings").child(uID);

        convidados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot convidados) {
                followings.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot followings) {
                        usersList.clear();
                        for(DataSnapshot user : followings.getChildren())
                        {
                            if(!convidados.hasChild(user.getKey()))
                            {
                                usersList.add(user.getValue(Usuario.class));
                            }
                        }
                        recyclerView.setAdapter(new ListaUsuariosConvidarAdapter(usersList,evento));
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
