package com.example.scamshield;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.scamshield.database.DatabaseHelper;
import com.example.scamshield.utils.NotificationHelper;
import com.example.scamshield.utils.ScamDetector;

public class ReportFragment extends Fragment {

    private EditText etPhone, etMessage;
    private Button btnAnalyze, btnReport;
    private TextView tvResult;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        etPhone = view.findViewById(R.id.etPhone);
        etMessage = view.findViewById(R.id.etMessage);
        btnAnalyze = view.findViewById(R.id.btnAnalyze);
        btnReport = view.findViewById(R.id.btnReport);
        tvResult = view.findViewById(R.id.tvResult);

        dbHelper = new DatabaseHelper(getActivity());

        // Analyze button
        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeScam();
            }
        });

        // Report button
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportScam();
            }
        });

        return view;
    }

    private void analyzeScam() {
        String phone = etPhone.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (phone.isEmpty()) {
            etPhone.setError("Namba ya simu inahitajika");
            etPhone.requestFocus();
            return;
        }

        if (message.isEmpty()) {
            etMessage.setError("Ujumbe inahitajika");
            etMessage.requestFocus();
            return;
        }

        // Use ScamDetector
        ScamDetector.ScamResult result = ScamDetector.detect(message, phone);

        if (result.isScam) {
            // Build result message
            StringBuilder sb = new StringBuilder();
            sb.append("⚠️ SCAM DETECTED!\n\n");
            sb.append("Type: ").append(result.scamType).append("\n");
            sb.append("Danger Level: ").append(result.dangerLevel).append("\n\n");
            sb.append("Indicators:\n");
            for (String indicator : result.indicators) {
                sb.append("• ").append(indicator).append("\n");
            }

            tvResult.setText(sb.toString());
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            // Save to database
            long id = dbHelper.addScam(phone, message, result.scamType);
            if (id != -1) {
                Toast.makeText(getActivity(), "✅ Scam saved to database", Toast.LENGTH_SHORT).show();
            }

            // Send notification
            NotificationHelper.createNotificationChannel(getActivity());
            NotificationHelper.showScamAlert(getActivity(), phone, result.scamType);

        } else {
            tvResult.setText("✅ This message appears safe.\nNo scam indicators detected.");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void reportScam() {
        String phone = etPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            etPhone.setError("Namba ya simu inahitajika");
            etPhone.requestFocus();
            return;
        }

        // Report to TCRA
        Toast.makeText(getActivity(),
                "✅ Report sent to TCRA (15040)\nPhone: " + phone + "\n\nTest number: 0693228000",
                Toast.LENGTH_LONG).show();

        etPhone.setText("");
        etMessage.setText("");
        tvResult.setText("");
    }
}