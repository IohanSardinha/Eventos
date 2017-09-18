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

public class confirmarPresentesAdapter extends RecyclerView.Adapter<confirmarPresentesAdapter.ViewHolder> {

    private ArrayList<Usuario> list;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private DatabaseReference usersParticipatingReference;
    private String eventID;

    public confirmarPresentesAdapter(ArrayList<Usuario> list, String eventID)
    {
        this.list = list;
        this.eventID = eventID;
    }

    @Override
    public confirmarPresentesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirmacao_usuario,parent,false));
    }

    @Override
    public void onBindViewHolder(final confirmarPresentesAdapter.ViewHolder holder, final int position) {
        holder.nomeUsuario.setText(list.get(position).getNome());
        holder.buttonSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersReference.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario user = dataSnapshot.getValue(Usuario.class);
                        user.setEventosComparecidos(user.getEventosComparecidos()+1);
                        user.setEventosConfirmados(user.getEventosConfirmados()+1);
                        usersReference.child(user.getId()).setValue(user);
                        usersParticipatingReference.child(list.get(position).getId()).removeValue();
                        notifyDataSetChanged();
                        list.remove(position);
                        holder.buttonSim.setEnabled(false);
                        holder.buttonNao.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.buttonNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersReference.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario user = dataSnapshot.getValue(Usuario.class);
                        user.setEventosComparecidos(user.getEventosComparecidos()+1);
                        usersReference.child(user.getId()).setValue(user);
                        usersParticipatingReference.child(list.get(position).getId()).removeValue();
                        list.remove(position);
                        notifyDataSetChanged();
                        holder.buttonSim.setEnabled(false);
                        holder.buttonNao.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
        public ViewHolder(View itemView) {
            super(itemView);
            database = FirebaseDatabase.getInstance();
            usersReference = database.getReference("Users");
            usersParticipatingReference = database.getReference("UsersParticipating").child(eventID);
            System.out.println(usersParticipatingReference);
            nomeUsuario = (TextView)itemView.findViewById(R.id.nome_usuario_item_confirmacao);
            buttonSim = (Button)itemView.findViewById(R.id.foi_item_confirmacao);
            buttonNao = (Button)itemView.findViewById(R.id.nao_foi_item_confirmacao);
        }
    }
}
