package com.intelligentquestionbank.memory;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.intelligentquestionbank.memory.fragments.LibraryListFragment;
import com.intelligentquestionbank.memory.fragments.ImportFragment;
import com.intelligentquestionbank.memory.fragments.StatisticsFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        
        // 默认显示题库列表页面
        if (savedInstanceState == null) {
            replaceFragment(new LibraryListFragment());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_library) {
            selectedFragment = new LibraryListFragment();
        } else if (itemId == R.id.nav_import) {
            selectedFragment = new ImportFragment();
        } else if (itemId == R.id.nav_statistics) {
            selectedFragment = new StatisticsFragment();
        }

        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
            return true;
        }

        return false;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void navigateToLibraryList() {
        bottomNavigationView.setSelectedItemId(R.id.nav_library);
    }
}