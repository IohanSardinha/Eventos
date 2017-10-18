package br.com.sardinha.iohan.eventos.Activity;


import android.widget.SearchView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import br.com.sardinha.iohan.eventos.Fragment.ResultadoPesquisaEventoFragment;
import br.com.sardinha.iohan.eventos.Fragment.ResultadoPesquisaUsuarioFragment;
import br.com.sardinha.iohan.eventos.R;

public class ResultadoPesquisaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private FragmentAdapter adapter;
    private ViewPager viewPager;
    private String query;
    private SearchView searchView;

    ResultadoPesquisaUsuarioFragment userFragment;
    ResultadoPesquisaEventoFragment eventFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_pesquisa);

        query = getIntent().getStringExtra("Query");

        adapter = new FragmentAdapter(getSupportFragmentManager());
        searchView = (SearchView)findViewById(R.id.search_view_resultado);
        searchView.setOnQueryTextListener(this);

        viewPager = (ViewPager)findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void setupViewPager(ViewPager viewPager)
    {
        FragmentAdapter a = new FragmentAdapter(getSupportFragmentManager());
        userFragment = new ResultadoPesquisaUsuarioFragment();
        userFragment.setQuery(query);
        eventFragment = new ResultadoPesquisaEventoFragment();
        eventFragment.setQuery(query);
        a.addFragment(userFragment,"Usuários");
        a.addFragment(eventFragment,"Eventos");
        viewPager.setAdapter(a);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        userFragment.filter(newText);
        eventFragment.filter(newText);
        System.out.println(newText);
        return true;
    }
}
