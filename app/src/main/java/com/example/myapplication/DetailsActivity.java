package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    TextView details;
    student student;

    ActivityResultLauncher<Intent> editLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        details = findViewById(R.id.details);

        editLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK &&
                            result.getData() != null) {

                        student updated = (student)
                                result.getData().getSerializableExtra("student");

                        if (updated != null) {
                            student = updated;
                            showStudent();
                        }
                    }
                }
        );

        if (getIntent().hasExtra("student")) {
            student = (student) getIntent().getSerializableExtra("student");
        } else {
            loadStudent();
        }

        if (student == null) {
            finish();
            return;
        }

        showStudent();

        findViewById(R.id.edit).setOnClickListener(v -> editStudent());
        findViewById(R.id.emailButton).setOnClickListener(v -> sendEmail());
        findViewById(R.id.shareButton).setOnClickListener(v -> shareStudent());
    }

    private void showStudent() {
        String text =
                getString(R.string.result_first, student.firstName) + "\n" +
                        getString(R.string.result_last, student.lastName) + "\n" +
                        getString(R.string.result_email, student.email) + "\n" +
                        getString(R.string.result_grade, student.grade) + "\n" +
                        getString(R.string.result_major, student.major) + "\n" +
                        getString(R.string.result_notifications,
                                getString(student.notifications ? R.string.yes : R.string.no)) + "\n" +
                        getString(R.string.result_terms,
                                getString(student.terms ? R.string.yes : R.string.no));

        details.setText(text);
    }

    private void editStudent() {
        Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
        intent.putExtra("student", student);
        editLauncher.launch(intent);
    }

    private void sendEmail() {
        String text = details.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{student.email});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.email_chooser)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_email_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareStudent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, details.getText().toString());

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_chooser)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_share_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStudent() {
        SharedPreferences sp = getSharedPreferences("Student", MODE_PRIVATE);

        if (!sp.contains("first")) {
            student = null;
            return;
        }

        student = new student(
                sp.getString("first", ""),
                sp.getString("last", ""),
                sp.getString("email", ""),
                sp.getString("grade", ""),
                sp.getString("major", ""),
                sp.getBoolean("notifications", false),
                sp.getBoolean("terms", false)
        );
    }
}