package br.com.sardinha.iohan.eventos.Fragment;

import android.content.Context;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Adapter.ListaEventosAdapter;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.R;

public class MeusEventosFragment extends Fragment {

    private boolean meus_eventos = false;
    private RecyclerView recyclerView;


    public void setMeus_eventos(boolean meus_eventos) {
        this.meus_eventos = meus_eventos;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_meus_eventos,container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.lista_meus_eventos_criados);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(meus_eventos)
        {
            Query ref = FirebaseDatabase.getInstance().getReference("Events").orderByChild("userID").startAt(uID).endAt(uID);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Evento> list = new ArrayList<Evento>();
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        list.add(ds.getValue(Evento.class));
                    }
                    recyclerView.setAdapter(new ListaEventosAdapter(list,view.getContext()));
                    view.findViewById(R.id.progressBar5).setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EventsInvited").child(uID);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final long[] done = {0,999999999};
                    done[1] = dataSnapshot.getChildrenCount();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
                    final ArrayList<Evento> list = new ArrayList<Evento>();
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        ref.child(ds.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                list.add(dataSnapshot.getValue(Evento.class));
                                done[0] += 1;
                                draw(done[1],done[0],list,view);
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
        }

        return view;
    }

    private void draw(long total, long done, ArrayList<Evento> list, View view)
    {
        if(done >= total)
        {
            recyclerView.setAdapter(new ListaEventosAdapter(list,view.getContext()));
            view.findViewById(R.id.progressBar5).setVisibility(View.GONE);
        }
    }


}
