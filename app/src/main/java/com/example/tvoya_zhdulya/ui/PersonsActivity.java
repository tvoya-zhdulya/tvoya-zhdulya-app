package com.example.tvoya_zhdulya.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.tvoya_zhdulya.MainActivity;
import com.example.tvoya_zhdulya.R;
import com.example.tvoya_zhdulya.data.Person;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonsActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private ListView listView;
    private TextView tvEmpty;
    private List<Person> personList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        listView = findViewById(R.id.listPersons);
        tvEmpty = findViewById(R.id.tvEmpty);

        findViewById(R.id.btnAddPerson).setOnClickListener(v -> showAddPersonDialog());

        viewModel.getPersons().observe(this, persons -> {
            if (persons != null) {
                personList = persons;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (personList.isEmpty()) {
            listView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            updateAdapter();
        }
    }

    private void updateAdapter() {
        ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_2, android.R.id.text1, personList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Person p = personList.get(position);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                text1.setText(p.name);
                int daysLeft = viewModel.getDaysRemainingForPerson(p);
                text2.setText(daysLeft >= 0 ? "Осталось: " + daysLeft + " дн." : "Освободился!");
                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Person selected = personList.get(position);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("person_id", selected.id);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Person p = personList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Удаление")
                    .setMessage("Удалить " + p.name + "?")
                    .setPositiveButton("Да", (d, w) -> viewModel.deletePerson(p))
                    .setNegativeButton("Нет", null)
                    .show();
            return true;
        });
    }

    private void showAddPersonDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_person, null);
        EditText etName = v.findViewById(R.id.etName);
        DatePicker dp = v.findViewById(R.id.datePicker);

        new AlertDialog.Builder(this)
                .setTitle("Добавить близкого")
                .setView(v)
                .setPositiveButton("Добавить", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        String date = LocalDate.of(dp.getYear(), dp.getMonth() + 1, dp.getDayOfMonth()).toString();
                        viewModel.addPerson(name, date, 0);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}