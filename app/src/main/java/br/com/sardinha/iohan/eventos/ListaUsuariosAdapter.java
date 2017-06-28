package br.com.sardinha.iohan.eventos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.ViewHolder> {

    private ArrayList<Usuario> list;
    Context context;

    public ListaUsuariosAdapter(ArrayList<Usuario> users, Context context) {
        list = users;
        this.context = context;
    }

    @Override
    public ListaUsuariosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userName.setText(list.get(position).getNome());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        Button followButton;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView)itemView.findViewById(R.id.nome_usuario_item);
            followButton = (Button)itemView.findViewById(R.id.seguir_usuario_item);
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
