package br.com.sardinha.iohan.eventos.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.sardinha.iohan.eventos.Activity.DetalhesEventoActivity;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.R;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.Activity.UsuarioActivity;


public class ListaEventosAdapter extends RecyclerView.Adapter<ListaEventosAdapter.ViewHolder> {
    ArrayList<Evento> list;
    Context context;
    DatabaseReference eventsReference,userReference, userParticipatingReference, eventsParticipatingReference;
    String uID;
    double dateNow;
    Usuario currentUser;
    public ListaEventosAdapter(ArrayList<Evento> list, Context context, Usuario currentUser) {
        this.list = list;
        this.context = context;
        this.currentUser = currentUser;
    }

    public ListaEventosAdapter(ArrayList<Evento> list, Context context) {
        this.list = list;
        this.context = context;
        currentUser = null;
    }

    @Override
    public ListaEventosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento,parent,false));
    }

    @Override
    public void onBindViewHolder(final ListaEventosAdapter.ViewHolder holder, final int position) {
        final Evento evento = list.get(position);
        if (context instanceof UsuarioActivity)
        {
            if(evento.getUserID().equals(currentUser.getId()))
            {
                userReference.child(uID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.userName.setText(dataSnapshot.getValue(Usuario.class).getNome()+" criou um evento");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else
            {
                userReference.child(uID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.getValue(Usuario.class).getNome();
                        userReference.child(evento.getUserID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(evento.getDataHora()<= dateNow)
                                {
                                    holder.userName.setText(userName+" compareceu a um evento de "+dataSnapshot.getValue(Usuario.class).getNome());
                                    holder.participate.setVisibility(View.GONE);
                                    holder.itemView.setClickable(false);
                                }
                                else
                                {
                                    holder.userName.setText(userName+" confirmou presença em um evento de "+dataSnapshot.getValue(Usuario.class).getNome());
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
            }
        }
        else
        {
            userReference.child(evento.getUserID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.userName.setText(dataSnapshot.getValue(Usuario.class).getNome()+" criou um evento");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userParticipatingReference.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uID))
                {
                    holder.participate.setText("Participando");
                    holder.participate.setBackgroundResource(R.color.buttonPressed);
                    holder.participate.setTextColor(Color.WHITE);
                    holder.participate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            userParticipatingReference.child(list.get(position).getId()).child(userID).removeValue();
                            eventsParticipatingReference.child(userID).child(list.get(position).getId()).removeValue();
                        }
                    });
                }
                else
                {
                    if(list.get(position).getUserID().equals(uID))
                    {
                        holder.participate.setVisibility(View.GONE);
                    }
                    else
                    {
                        if(evento.getLimite()-dataSnapshot.getChildrenCount() > 0 || evento.getLimite() == -1)
                        {
                            holder.participate.setBackgroundResource(R.color.button);
                            holder.participate.setText("Participar");
                            holder.participate.setTextColor(Color.BLACK);
                            holder.participate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    userReference.child(userID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            userParticipatingReference.child(list.get(position).getId()).child(userID).setValue(dataSnapshot.getValue(Usuario.class));
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    eventsParticipatingReference.child(userID).child(list.get(position).getId()).setValue(list.get(position));
                                }
                            });
                        }
                        else if(!dataSnapshot.hasChild(uID))
                        {
                            holder.participate.setBackgroundResource(R.color.button);
                            holder.participate.setTextColor(Color.BLACK);
                            holder.participate.setText("Lotado");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.info.setText(String.valueOf(list.get(position).getTitulo()));
        holder.description.setText(String.valueOf(list.get(position).getEndereco()));
        if(list.get(position).getImagem() != null)
        {
            Glide.with(context).load(Uri.parse(list.get(position).getImagem())).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView info;
        TextView description;
        TextView userName;
        ImageView image;
        Button participate;
        public ViewHolder(View itemView) {
            super(itemView);
            uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userReference = FirebaseDatabase.getInstance().getReference("Users");
            userParticipatingReference = FirebaseDatabase.getInstance().getReference("UsersParticipating");
            eventsParticipatingReference = FirebaseDatabase.getInstance().getReference("EventsParticipating");
            eventsReference = FirebaseDatabase.getInstance().getReference("Events");
            image = (ImageView)itemView.findViewById(R.id.imagem_item_lista);
            info = (TextView)itemView.findViewById(R.id.info_text);
            description = (TextView)itemView.findViewById(R.id.info_text2);
            userName = (TextView)itemView.findViewById(R.id.nome_usuario_item_evento);
            participate = (Button)itemView.findViewById(R.id.participar_item_evento);
            Calendar calendar = Calendar.getInstance();
            dateNow = Double.parseDouble(
                    formatador(calendar.get(Calendar.YEAR))
                            +formatador(calendar.get(Calendar.MONTH)+1)
                            +formatador(calendar.get(Calendar.DAY_OF_MONTH))
                            +formatador(calendar.get(Calendar.HOUR_OF_DAY))
                            +formatador(calendar.get(Calendar.MINUTE)));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,DetalhesEventoActivity.class);
            intent.putExtra("evento",list.get(getAdapterPosition()));
            context.startActivity(intent);
            ((Activity)context).finish();
        }
    }
    private String formatador(int x){
        if (x < 10){
            return "0"+String.valueOf(x);
        }
        return String.valueOf(x);
    }
}
