package br.com.sardinha.iohan.eventos.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.R;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class confirmarPresentesAdapter extends RecyclerView.Adapter<confirmarPresentesAdapter.ViewHolder> {

    private ArrayList<Usuario> list;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private DatabaseReference usersParticipatingReference;
    private DatabaseReference eventsParticipating;
    private DatabaseReference eventsParticipated;
    private Evento event;
    private boolean CLICKED = false;
    private Context context;

    public confirmarPresentesAdapter(ArrayList<Usuario> list, Evento event, Context context)
    {
        this.list = list;
        this.event = event;
        this.context = context;
    }

    @Override
    public confirmarPresentesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirmacao_usuario,parent,false));
    }

    @Override
    public void onBindViewHolder(final confirmarPresentesAdapter.ViewHolder holder, final int position) {
        holder.nomeUsuario.setText(list.get(position).getNome());

        if(!list.get(position).getImagem().isEmpty())
        {
            Glide.with(context).load(Uri.parse(list.get(position).getImagem())).into(holder.userPhoto);
        }

        holder.buttonSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CLICKED) {
                    usersReference.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Usuario user = dataSnapshot.getValue(Usuario.class);
                            user.setEventosComparecidos(user.getEventosComparecidos() + 1);
                            user.setEventosConfirmados(user.getEventosConfirmados() + 1);
                            if(!CLICKED) {
                                usersReference.child(user.getId()).setValue(user);
                                usersParticipatingReference.child(list.get(position).getId()).removeValue();
                                eventsParticipating.child(user.getId()).child(event.getId()).removeValue();
                                eventsParticipated.child(user.getId()).child(event.getId()).setValue(event);
                                list.clear();
                            }
                            CLICKED = true;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        holder.buttonNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CLICKED) {
                    usersReference.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Usuario user = dataSnapshot.getValue(Usuario.class);
                            user.setEventosConfirmados(user.getEventosConfirmados() + 1);
                            if(!CLICKED) {
                                usersReference.child(user.getId()).setValue(user);
                                usersParticipatingReference.child(list.get(position).getId()).removeValue();
                                eventsParticipating.child(user.getId()).child(event.getId()).removeValue();
                                list.clear();
                            }
                            CLICKED = true;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeUsuario;
        Button buttonSim;
        Button buttonNao;
        CircleImageView userPhoto;
        public ViewHolder(View itemView) {
            super(itemView);
            database = FirebaseDatabase.getInstance();
            usersReference = database.getReference("Users");
            usersParticipatingReference = database.getReference("UsersParticipating").child(event.getId());
            eventsParticipated = database.getReference("EventsParticipated");
            eventsParticipating = database.getReference("EventsParticipating");
            nomeUsuario = (TextView)itemView.findViewById(R.id.nome_usuario_item_confirmacao);
            buttonSim = (Button)itemView.findViewById(R.id.foi_item_confirmacao);
            buttonNao = (Button)itemView.findViewById(R.id.nao_foi_item_confirmacao);
            userPhoto = (CircleImageView) itemView.findViewById(R.id.foto_usuario_confirmacao);
        }
    }
}
