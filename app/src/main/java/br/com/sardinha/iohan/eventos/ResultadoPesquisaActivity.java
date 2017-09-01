package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class ResultadoPesquisaActivity extends AppCompatActivity {

    private FragmentAdapter adapter;
    ViewPager viewPager;
    String query;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this,EventosActivity.class));
    }
}
