package br.com.sardinha.iohan.eventos.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.Fragment.ConfirmadosFragment;
import br.com.sardinha.iohan.eventos.Fragment.ConvidadosFragment;
import br.com.sardinha.iohan.eventos.Fragment.SeguidoresFragment;
import br.com.sardinha.iohan.eventos.R;

public class ConvidadosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ViewPager viewPager;
    private Evento evento;
    private Usuario usuario;
    private boolean confirmados;

    ConvidadosFragment convidadosFragment;
    SeguidoresFragment seguidoresFragment;
    ConfirmadosFragment confirmadosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convidados);
        if(confirmados)
        {
            setTitle("Convidar");
        }
        else
        {
            setTitle("Convidados");
        }
        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra("evento");
        usuario = (Usuario) intent.getSerializableExtra("usuario");

        confirmados = intent.getBooleanExtra("confirmados",false);
        viewPager = (ViewPager)findViewById(R.id.container2);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs2);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager)
    {
        FragmentAdapter a = new FragmentAdapter(getSupportFragmentManager());
        convidadosFragment = new ConvidadosFragment();
        convidadosFragment.setEvento(evento);
        convidadosFragment.setUsuario(usuario);

        if(confirmados)
        {
            confirmadosFragment = new ConfirmadosFragment();
            confirmadosFragment.setEvento(evento);
            confirmadosFragment.setUsuario(usuario);
            a.addFragment(confirmadosFragment,"Confirmados");
            convidadosFragment.setConvidados(true);
            findViewById(R.id.floatingActionButton2).setVisibility(View.GONE);
        }
        else
        {
            seguidoresFragment = new SeguidoresFragment();
            seguidoresFragment.setEvento(evento);
            seguidoresFragment.setUsuario(usuario);
            a.addFragment(seguidoresFragment,"Seguidores");
        }

        a.addFragment(convidadosFragment,"Convidados");

        viewPager.setAdapter(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_convidados,menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_eventos).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        return true;
    }

    public void confirmarOnClick(View view) {
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        seguidoresFragment.filter(newText);
        convidadosFragment.filter(newText);
        confirmadosFragment.filter(newText);
        return true;
    }
}