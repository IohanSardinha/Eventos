package br.com.sardinha.iohan.eventos.Adapter;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Activity.UsuarioActivity;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.NotificationSender;
import br.com.sardinha.iohan.eventos.Class.OneShotClickListener;
import br.com.sardinha.iohan.eventos.R;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;


public class ListaUsuariosConvidarAdapter extends RecyclerView.Adapter<ListaUsuariosConvidarAdapter.ViewHolder>{

    private ArrayList<Usuario> list;
    private DatabaseReference usersInvited;
    private DatabaseReference eventsInvited;
    private Evento evento;
    private Usuario currentUser;
    private Context context;
    private boolean confirmados = false;


    public ListaUsuariosConvidarAdapter( Context context,ArrayList<Usuario> list,Evento evento, Usuario currentUser)
    {
        this.list = list;
        this.evento = evento;
        this.context = context;
        this.currentUser = currentUser;
    }

    public ListaUsuariosConvidarAdapter( Context context,ArrayList<Usuario> list,Evento evento, Usuario currentUser, boolean confirmados)
    {
        this.list = list;
        this.evento = evento;
        this.context = context;
        this.currentUser = currentUser;
        this.confirmados = confirmados;
    }

    @Override
    public ListaUsuariosConvidarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_convidar,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.nome.setText(list.get(position).getNome());
        if(!list.get(position).getImagem().isEmpty())
        {
            Glide.with(context).load(Uri.parse(list.get(position).getImagem())).into(holder.userPhoto);
        }
        if(confirmados)
        {
            holder.convidarButton.setVisibility(View.GONE);
        }
        else
        {
            usersInvited.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(Usuario.class) != null)
                    {
                        holder.convidarButton.setBackgroundResource(R.color.colorPrimary);
                        holder.convidarButton.setText("Convidado");
                        holder.convidarButton.setTextColor(Color.WHITE);
                        holder.convidarButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                usersInvited.child(list.get(position).getId()).removeValue();
                                eventsInvited.child(list.get(position).getId()).child(evento.getId()).removeValue();
                            }
                        });
                    }
                    else
                    {
                        holder.convidarButton.setBackgroundResource(R.color.button);
                        holder.convidarButton.setText("Convidar");
                        holder.convidarButton.setTextColor(Color.BLACK);
                        holder.convidarButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                usersInvited.child(list.get(position).getId()).setValue(list.get(position));
                                eventsInvited.child(list.get(position).getId()).child(evento.getId()).setValue(evento.getId());
                                new NotificationSender().SendInvitationNotification(context,evento,list.get(position),currentUser);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button convidarButton;
        TextView nome;
        CircleImageView userPhoto;
        public ViewHolder(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.nome_usuario_convidar_item);
            userPhoto = (CircleImageView) itemView.findViewById(R.id.foto_perfil_convidar);
            convidarButton = (Button)itemView.findViewById(R.id.convidar_usuario_item);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            usersInvited = database.getReference("UsersInvited").child(evento.getId());
            eventsInvited = database.getReference("EventsInvited");
            itemView.setOnClickListener(new OneShotClickListener() {
                @Override
                public void performClick(View v) {
                    Intent intent = new Intent(context, UsuarioActivity.class);
                    intent.putExtra("Usuario",list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    public void setFilter(ArrayList<Usuario> newList)
    {
        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
