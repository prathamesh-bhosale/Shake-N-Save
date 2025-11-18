package com.example.safetyalert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private TabLayout indicator;
    private TextView skip;
    private TextView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        pager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);
        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);

        pager.setAdapter(new OnboardingAdapter(this));

        new TabLayoutMediator(indicator, pager,
                (tab, position) -> { // A a new tab and its position is provided
            // You can custom the tab view here
        }).attach();

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if(position == 4){
                    skip.setVisibility(View.GONE);
                    next.setText("Done");
                } else {
                    skip.setVisibility(View.VISIBLE);
                    next.setText("Next");
                }
            }
        });

        skip.setOnClickListener(v -> finishOnboarding());

        next.setOnClickListener(v -> {
            if(pager.getCurrentItem() == 4){
                finishOnboarding();
            } else {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });
    }

    private void finishOnboarding() {
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        preferences.edit().putBoolean("onboarding_complete",true).apply();

        Intent main = new Intent(OnBoardingActivity.this, Home.class);
        startActivity(main);
        finish();
    }

    private static class OnboardingAdapter extends FragmentStateAdapter {

        public OnboardingAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new S1Fragment();
                case 1:
                    return new S2Fragment();
                case 2:
                    return new S3Fragment();
                case 3:
                    return new S4Fragment();
                case 4:
                    return new S5Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}