package com.example.scamshield;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.scamshield.database.DatabaseHelper;

public class HomeFragment extends Fragment {

    private TextView tvTotalScams, tvTodayScams, tvHighRisk, tvMediumRisk, tvLowRisk, tvRecentAlerts;
    private Button btnViewHistory;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTotalScams = view.findViewById(R.id.tvTotalScams);
        tvTodayScams = view.findViewById(R.id.tvTodayScams);
        tvHighRisk = view.findViewById(R.id.tvHighRisk);
        tvMediumRisk = view.findViewById(R.id.tvMediumRisk);
        tvLowRisk = view.findViewById(R.id.tvLowRisk);
        tvRecentAlerts = view.findViewById(R.id.tvRecentAlerts);
        btnViewHistory = view.findViewById(R.id.btnViewHistory);

        dbHelper = new DatabaseHelper(getActivity());

        loadStats();

        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.historyFragment);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        int total = dbHelper.getTotalScams();
        int today = dbHelper.getTodayScams();

        tvTotalScams.setText(String.valueOf(total));
        tvTodayScams.setText(String.valueOf(today));

        // Count by risk type (simplified)
        int high = 0, medium = 0, low = 0;
        // You can add logic to count by scam type

        tvHighRisk.setText(String.valueOf(high));
        tvMediumRisk.setText(String.valueOf(medium));
        tvLowRisk.setText(String.valueOf(low));

        if (total == 0) {
            tvRecentAlerts.setText("No scams detected yet\nYou are safe!");
        } else {
            tvRecentAlerts.setText("⚠ " + total + " scams detected\nTap View History for details");
        }
    }
}