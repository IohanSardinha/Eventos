package br.com.sardinha.iohan.eventos.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Activity.ConvidadosActivity;
import br.com.sardinha.iohan.eventos.Activity.UsuarioActivity;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.OneShotClickListener;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListaUsuariosConvidadosAdapter extends RecyclerView.Adapter<ListaUsuariosConvidadosAdapter.ViewHolder>{

    ArrayList<Usuario> list;
    Context context;
    Evento event;
    Usuario currentUser;

    public ListaUsuariosConvidadosAdapter(ArrayList<Usuario> list, Context context, Usuario currentUser, Evento event)
    {
        this.list = list;
        this.context = context;
        this.currentUser = currentUser;
        this.event = event;
    }

    @Override
    public ListaUsuariosConvidadosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_convidados,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(list.get(position).getId().isEmpty())
        {
            holder.userProfilePhoto.setVisibility(View.GONE);
            holder.imageHolder.setVisibility(View.GONE);
            holder.userName.setPadding(0,10,0,0);
            holder.userName.setTextSize(18);
        }
        else
        {
            if(!list.get(position).getImagem().isEmpty())
            {
                Glide.with(context).load(Uri.parse(list.get(position).getImagem())).into(holder.userProfilePhoto);
            }
        }
        holder.userName.setText(list.get(position).getNome());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        CircleImageView userProfilePhoto;
        View imageHolder;
        public ViewHolder(View itemView)
        {
            super(itemView);
            userProfilePhoto = (CircleImageView) itemView.findViewById(R.id.fotoPerfilItemConvidados);
            userName = (TextView)itemView.findViewById(R.id.nome_usuario_convidado_item);
            imageHolder = itemView.findViewById(R.id.imageHolder);
            itemView.setOnClickListener(new OneShotClickListener() {
                @Override
                public void performClick(View v) {
                    if(list.get(getAdapterPosition()).getId().isEmpty())
                    {
                        Intent intent = new Intent(context, ConvidadosActivity.class);
                        intent.putExtra("evento",event);
                        intent.putExtra("usuario",currentUser);
                        intent.putExtra("confirmados",true);
                        context.startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(context, UsuarioActivity.class);
                        intent.putExtra("Usuario",list.get(getAdapterPosition()));
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
