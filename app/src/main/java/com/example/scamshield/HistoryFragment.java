package com.example.scamshield;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scamshield.database.DatabaseHelper;
import com.example.scamshield.models.ScamModel;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private Button btnClearAll;
    private DatabaseHelper dbHelper;
    private ScamAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        btnClearAll = view.findViewById(R.id.btnClearAll);

        dbHelper = new DatabaseHelper(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadScamHistory();

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearAllDialog();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadScamHistory();
    }

    private void loadScamHistory() {
        List<ScamModel> scamList = dbHelper.getAllScams();

        if (scamList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.VISIBLE);

            adapter = new ScamAdapter(getActivity(), scamList, new ScamAdapter.OnScamClickListener() {
                @Override
                public void onScamClick(ScamModel scam) {
                    showScamOptionsDialog(scam);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void showScamOptionsDialog(final ScamModel scam) {
        String[] options = {"📞 Call", "📱 Report to TCRA", "🗑️ Delete", "📋 Copy Details"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Scam Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Call
                        callNumber(scam.getPhoneNumber());
                        break;
                    case 1: // Report to TCRA
                        reportToTCRA(scam);
                        break;
                    case 2: // Delete
                        deleteScam(scam);
                        break;
                    case 3: // Copy Details
                        copyDetails(scam);
                        break;
                }
            }
        });
        builder.show();
    }

    private void callNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void reportToTCRA(ScamModel scam) {
        // Report to TCRA (15040)
        String message = "Namba " + scam.getPhoneNumber() + " inatuma ulaghai. Aina: " + scam.getScamType();

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:15040"));
        smsIntent.putExtra("sms_body", message);

        if (smsIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Toast.makeText(getActivity(), "SMS app not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteScam(ScamModel scam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Scam");
        builder.setMessage("Are you sure you want to delete this scam report?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteScam(scam.getId());
                loadScamHistory();
                Toast.makeText(getActivity(), "Scam deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void copyDetails(ScamModel scam) {
        String details = "Phone: " + scam.getPhoneNumber() +
                "\nType: " + scam.getScamType() +
                "\nMessage: " + scam.getMessage() +
                "\nDate: " + scam.getDateTime() +
                "\nStatus: " + (scam.isReported() ? "Reported" : "Pending");

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Scam Details", details);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getActivity(), "Details copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void showClearAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Clear All History");
        builder.setMessage("Are you sure you want to delete ALL scam history?");
        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteAllScams();
                loadScamHistory();
                Toast.makeText(getActivity(), "All history cleared", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}