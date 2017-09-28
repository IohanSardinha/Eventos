package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ConvidadosActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Evento evento;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convidados);

        setTitle("Convidar");
        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra("evento");
        usuario = (Usuario) intent.getSerializableExtra("usuario");

        viewPager = (ViewPager)findViewById(R.id.container2);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs2);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager)
    {
        FragmentAdapter a = new FragmentAdapter(getSupportFragmentManager());
        ConvidadosFragment convidadosFragment = new ConvidadosFragment();
        convidadosFragment.setEvento(evento);
        convidadosFragment.setUsuario(usuario);
        SeguidoresFragment seguidoresFragment = new SeguidoresFragment();
        seguidoresFragment.setEvento(evento);
        seguidoresFragment.setUsuario(usuario);
        a.addFragment(seguidoresFragment,"Seguidores");
        a.addFragment(convidadosFragment,"Convidados");
        viewPager.setAdapter(a);
    }

    public void confirmarOnClick(View view) {
        finish();
    }
}
