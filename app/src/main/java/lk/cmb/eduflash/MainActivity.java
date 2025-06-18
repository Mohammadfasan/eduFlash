package lk.cmb.eduflash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import lk.cmb.eduflash.fragments.AcademicFragment;
import lk.cmb.eduflash.fragments.DevInfoFragment;
import lk.cmb.eduflash.fragments.EventsFragment;
import lk.cmb.eduflash.fragments.ProfileFragment;
import lk.cmb.eduflash.fragments.SportsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Fragment instances
    private final Fragment sportsFragment = new SportsFragment();
    private final Fragment academicFragment = new AcademicFragment();
    private final Fragment eventsFragment = new EventsFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment devInfoFragment = new DevInfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_sports) {
                loadFragment(sportsFragment);
                return true;
            } else if (id == R.id.nav_academic) {
                loadFragment(academicFragment);
                return true;
            } else if (id == R.id.nav_events) {
                loadFragment(eventsFragment);
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(profileFragment);
                return true;
            } else if (id == R.id.nav_dev_info) {
                loadFragment(devInfoFragment);
                return true;
            }
            return false;
        });

        // Check if activity opened from RegisterActivity
        if (savedInstanceState == null) {
            boolean fromRegister = getIntent().getBooleanExtra("fromRegister", false);

            if (fromRegister) {
                bottomNavigationView.setSelectedItemId(R.id.nav_sports);
            } else {
                // Default fragment if needed
                bottomNavigationView.setSelectedItemId(R.id.nav_sports);
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
