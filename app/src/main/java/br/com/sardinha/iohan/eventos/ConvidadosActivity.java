package br.com.sardinha.iohan.eventos;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ConvidadosActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convidados);

        setTitle("Convidar");

        viewPager = (ViewPager)findViewById(R.id.container2);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs2);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager)
    {
        FragmentAdapter a = new FragmentAdapter(getSupportFragmentManager());
        ConvidadosFragment convidadosFragment = new ConvidadosFragment();
        SeguidoresFragment seguidoresFragment = new SeguidoresFragment();
        a.addFragment(seguidoresFragment,"Seguidores");
        a.addFragment(convidadosFragment,"Convidados");
        viewPager.setAdapter(a);
    }
}
