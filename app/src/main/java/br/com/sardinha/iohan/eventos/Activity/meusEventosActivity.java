package br.com.sardinha.iohan.eventos.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.Fragment.MeusEventosFragment;
import br.com.sardinha.iohan.eventos.R;

public class meusEventosActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        viewPager = (ViewPager)findViewById(R.id.container_meus_eventos);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs3);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        FragmentAdapter a = new FragmentAdapter(getSupportFragmentManager());
        MeusEventosFragment meus_eventos = new MeusEventosFragment();
        meus_eventos.setMeus_eventos(true);
        MeusEventosFragment eventos_convidado = new MeusEventosFragment();
        eventos_convidado.setMeus_eventos(false);
        a.addFragment(meus_eventos,"Criados por mim");
        a.addFragment(eventos_convidado,"Convidado");
        viewPager.setAdapter(a);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
