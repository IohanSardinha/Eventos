package br.com.sardinha.iohan.eventos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListaUsuariosConvidarAdapter extends RecyclerView.Adapter<ListaUsuariosConvidarAdapter.ViewHolder>{

    private ArrayList<Usuario> list;
    private DatabaseReference invitationReference;
    private Evento evento;

    public ListaUsuariosConvidarAdapter(ArrayList<Usuario> list,Evento evento)
    {
        this.list = list;
        this.evento = evento;
    }

    @Override
    public ListaUsuariosConvidarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_convidar,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.nome.setText(list.get(position).getNome());
        invitationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(list.get(position).getId()))
                {
                    holder.convidarButton.setBackgroundResource(R.color.colorPrimary);
                    holder.convidarButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            invitationReference.child(list.get(position).getId()).removeValue();
                        }
                    });
                }
                else
                {
                    holder.convidarButton.setBackgroundResource(R.color.button);
                    holder.convidarButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            invitationReference.child(list.get(position).getId()).setValue(list.get(position));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button convidarButton;
        TextView nome;
        public ViewHolder(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.nome_usuario_convidar_item);
            convidarButton = (Button)itemView.findViewById(R.id.convidar_usuario_item);
            invitationReference = FirebaseDatabase.getInstance().getReference("UsersInvited").child(evento.getId());
        }
    }
}
