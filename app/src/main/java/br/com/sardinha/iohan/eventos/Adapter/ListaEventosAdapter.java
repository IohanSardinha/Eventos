package br.com.sardinha.iohan.eventos.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.com.sardinha.iohan.eventos.Activity.DetalhesEventoActivity;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.OneShotClickListener;
import br.com.sardinha.iohan.eventos.R;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.Activity.UsuarioActivity;


public class ListaEventosAdapter extends RecyclerView.Adapter<ListaEventosAdapter.ViewHolder> {
    private ArrayList<Evento> list;
    private Context context;
    private DatabaseReference userReference, userParticipatingReference, eventsParticipatingReference;
    private String uID;
    private double dateNow;
    private Usuario currentUser;
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
                userReference.child(currentUser.getId()).addValueEventListener(new ValueEventListener() {
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
                userReference.child(currentUser.getId()).addValueEventListener(new ValueEventListener() {
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


        if(evento.getUserID().equals(uID))
        {
            holder.participate.setVisibility(View.GONE);
        }
        else
        {
            userParticipatingReference.child(evento.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot eventSnapshot) {
                    userParticipatingReference.child(evento.getId()).child(uID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                holder.participatingImage.setImageResource(R.drawable.ic_event_available_black_24dp);
                                holder.participatingImage.setColorFilter(Color.BLACK);
                                holder.participatingText.setText(R.string.confirmado);
                                holder.participate.setOnClickListener(new OneShotClickListener() {
                                    @Override
                                    public void performClick(View v) {
                                        userParticipatingReference.child(evento.getId()).child(uID).removeValue();
                                        eventsParticipatingReference.child(uID).child(evento.getId()).removeValue();
                                    }
                                });
                            }
                            else if(evento.getLimite()-eventSnapshot.getChildrenCount() > 0 || evento.getLimite() == -1)
                            {
                                holder.participatingImage.setImageResource(R.drawable.calendar_plus);
                                holder.participatingImage.setColorFilter(ContextCompat.getColor(context,R.color.button));
                                holder.participatingText.setText(R.string.ir);
                                holder.participate.setOnClickListener(new OneShotClickListener() {
                                    @Override
                                    public void performClick(View v) {
                                        userReference.child(uID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Usuario user = dataSnapshot.getValue(Usuario.class);
                                                userParticipatingReference.child(evento.getId()).child(uID).setValue(user);
                                                eventsParticipatingReference.child(uID).child(evento.getId()).setValue(evento);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }
                            else
                            {
                                holder.participatingImage.setImageResource(R.drawable.ic_event_busy_black_24dp);
                                holder.participatingImage.setColorFilter(Color.BLACK);
                                holder.participatingText.setText(R.string.lotado);
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

        holder.title.setText(String.valueOf(list.get(position).getTitulo()));
        holder.address.setText(String.valueOf(list.get(position).getEndereco()));
        holder.time.setText(list.get(position).getHoraInicio());
        String data = list.get(position).getDataInicio();
        List<String> tempData = Arrays.asList(data.split("/"));
        holder.day.setText(tempData.get(0));
        holder.month.setText(getStringMonth(tempData.get(1)));

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
        TextView title;
        TextView address;
        TextView userName;
        TextView month;
        TextView time;
        TextView day;
        ImageView image;
        View participate;
        ImageView participatingImage;
        TextView participatingText;
        public ViewHolder(View itemView) {
            super(itemView);

            uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userReference = FirebaseDatabase.getInstance().getReference("Users");
            userParticipatingReference = FirebaseDatabase.getInstance().getReference("UsersParticipating");
            eventsParticipatingReference = FirebaseDatabase.getInstance().getReference("EventsParticipating");

            image = (ImageView)itemView.findViewById(R.id.imagem_item_lista);
            title = (TextView)itemView.findViewById(R.id.titulo_item_evento);
            address = (TextView)itemView.findViewById(R.id.endereco_item_evento);
            month = (TextView)itemView.findViewById(R.id.mes_item_evento);
            day = (TextView)itemView.findViewById(R.id.dia_item_evento);
            time = (TextView)itemView.findViewById(R.id.hora_item_evento);
            userName = (TextView)itemView.findViewById(R.id.nome_usuario_item_evento);
            participate = itemView.findViewById(R.id.participarButton);
            participatingImage = (ImageView)itemView.findViewById(R.id.imagemParticipandoItem);
            participatingText = (TextView)itemView.findViewById(R.id.textoParticipandoItem);

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
        }
    }
    private String formatador(int x){
        if (x < 10){
            return "0"+String.valueOf(x);
        }
        return String.valueOf(x);
    }
    private String getStringMonth(String month)
    {
        String[] meses = context.getResources().getStringArray(R.array.meses);
        switch (month)
        {
            case "01":
                return meses[0];
            case "02":
                return meses[1];
            case "03":
                return meses[2];
            case "04":
                return meses[3];
            case "05":
                return meses[4];
            case "06":
                return meses[5];
            case "07":
                return meses[6];
            case "08":
                return meses[7];
            case "09":
                return meses[8];
            case "10":
                return meses[9];
            case "11":
                return meses[10];
            default:
                return meses[11];
        }
    }

    public void setFilter(ArrayList<Evento> newList)
    {
        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
