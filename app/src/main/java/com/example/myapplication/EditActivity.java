package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    EditText firstName, lastName, email;
    RadioGroup gradeGroup;
    Spinner major;
    CheckBox notifications, terms;
    TextView gradeError, majorError;

    student oldStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        gradeGroup = findViewById(R.id.gradeGroup);
        major = findViewById(R.id.major);
        notifications = findViewById(R.id.notifications);
        terms = findViewById(R.id.terms);
        gradeError = findViewById(R.id.gradeError);
        majorError = findViewById(R.id.majorError);

        if (getIntent().hasExtra("student")) {
            oldStudent = (student) getIntent().getSerializableExtra("student");
        }

        if (oldStudent == null) {
            finish();
            return;
        }

        fillFields();

        findViewById(R.id.save).setOnClickListener(v -> save());
        findViewById(R.id.cancel).setOnClickListener(v -> finish());
    }

    private void fillFields() {
        firstName.setText(oldStudent.firstName);
        lastName.setText(oldStudent.lastName);
        email.setText(oldStudent.email);

        if (oldStudent.grade.equals(getString(R.string.grade10)))
            gradeGroup.check(R.id.grade10);
        else if (oldStudent.grade.equals(getString(R.string.grade11)))
            gradeGroup.check(R.id.grade11);
        else if (oldStudent.grade.equals(getString(R.string.grade12)))
            gradeGroup.check(R.id.grade12);

        for (int i = 1; i < major.getCount(); i++) {
            if (major.getItemAtPosition(i).toString().equals(oldStudent.major)) {
                major.setSelection(i);
                break;
            }
        }

        notifications.setChecked(oldStudent.notifications);
        terms.setChecked(oldStudent.terms);
    }

    private void save() {

        firstName.setError(null);
        lastName.setError(null);
        email.setError(null);
        terms.setError(null);

        gradeError.setVisibility(View.GONE);
        majorError.setVisibility(View.GONE);

        boolean valid = true;

        String first = firstName.getText().toString().trim();
        String last = lastName.getText().toString().trim();
        String mail = email.getText().toString().trim();

        if (first.isEmpty()) {
            firstName.setError(getString(R.string.error_first));
            valid = false;
        }

        if (last.isEmpty()) {
            lastName.setError(getString(R.string.error_last));
            valid = false;
        }

        if (mail.isEmpty()) {
            email.setError(getString(R.string.error_email));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError(getString(R.string.error_email_invalid));
            valid = false;
        }

        int id = gradeGroup.getCheckedRadioButtonId();
        String grade = "";

        if (id == -1) {
            gradeError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            RadioButton rb = findViewById(id);
            grade = rb.getText().toString();
        }

        String selectedMajor = "";

        if (major.getSelectedItemPosition() == 0) {
            majorError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            selectedMajor = major.getSelectedItem().toString();
        }

        if (!terms.isChecked()) {
            terms.setError(getString(R.string.error_terms));
            valid = false;
        }

        if (!valid) {
            return;
        }

        student updated = new student(
                first,
                last,
                mail,
                grade,
                selectedMajor,
                notifications.isChecked(),
                terms.isChecked()
        );

        saveStudent(updated);

        Intent result = new Intent();
        result.putExtra("student", updated);

        setResult(RESULT_OK, result);
        finish();
    }

    private void saveStudent(student s) {
        SharedPreferences sp = getSharedPreferences("Student", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();

        e.putString("first", s.firstName);
        e.putString("last", s.lastName);
        e.putString("email", s.email);
        e.putString("grade", s.grade);
        e.putString("major", s.major);
        e.putBoolean("notifications", s.notifications);
        e.putBoolean("terms", s.terms);

        e.apply();
    }
}