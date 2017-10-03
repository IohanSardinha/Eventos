package br.com.sardinha.iohan.eventos.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import br.com.sardinha.iohan.eventos.Fragment.ResultadoPesquisaEventoFragment;
import br.com.sardinha.iohan.eventos.Fragment.ResultadoPesquisaUsuarioFragment;
import br.com.sardinha.iohan.eventos.R;

public class ResultadoPesquisaActivity extends AppCompatActivity {

    private FragmentAdapter adapter;
    private ViewPager viewPager;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_pesquisa);

        query = getIntent().getStringExtra("Query");

        adapter = new FragmentAdapter(getSupportFragmentManager());

        viewPager = (ViewPager)findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void setupViewPager(ViewPager viewPager)
    {
        FragmentAdapter a = new FragmentAdapter(getSupportFragmentManager());
        ResultadoPesquisaUsuarioFragment userFragment = new ResultadoPesquisaUsuarioFragment();
        userFragment.setQuery(query);
        ResultadoPesquisaEventoFragment eventFragment = new ResultadoPesquisaEventoFragment();
        eventFragment.setQuery(query);
        a.addFragment(userFragment,"Usuários");
        a.addFragment(eventFragment,"Eventos");
        viewPager.setAdapter(a);
    }


    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this,EventosActivity.class));
    }*/
}
