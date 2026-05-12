package com.example.tvoya_zhdulya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModelProvider;
import com.example.tvoya_zhdulya.ui.MainViewModel;
import com.example.tvoya_zhdulya.ui.PersonsActivity;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private GridLayout calendarGrid;
    private TextView tvHeader, tvCounter, tvQuote, tvMoodToday, tvMonthYear, tvEventInfo;
    private Button btnChangePerson, btnBackToList, btnPrevMonth, btnNextMonth, btnSaveThoughts, btnAddEvent;
    private LinearLayout moodButtonsContainer, layoutNotes;
    private EditText etThoughts;
    private ImageView ivCat;

    private LocalDate currentDisplayMonth = LocalDate.now();
    private LocalDate selectedDate = LocalDate.now();
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", new Locale("ru"));
    private DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final int[] moodColors = { 0xFFFF0000, 0xFFFF69B4, 0xFFFFFF00, 0xFF000000 };
    private final String[] moodNames = { "Сильно скучаю", "Люблю", "Нейтрально", "Не думала" };

    private Map<String, Integer> moodMap = new HashMap<>();
    private Map<String, String> eventMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        initViews();
        setupObservers();
        setupMoodButtonsDisplay();

        btnChangePerson.setOnClickListener(v -> {
            startActivity(new Intent(this, PersonsActivity.class));
            finish();
        });

        btnBackToList.setOnClickListener(v -> {
            startActivity(new Intent(this, PersonsActivity.class));
            finish();
        });

        btnPrevMonth.setOnClickListener(v -> {
            currentDisplayMonth = currentDisplayMonth.minusMonths(1);
            renderCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentDisplayMonth = currentDisplayMonth.plusMonths(1);
            renderCalendar();
        });

        btnSaveThoughts.setOnClickListener(v -> {
            viewModel.saveNoteForDate(LocalDate.now(), etThoughts.getText().toString());
        });

        btnAddEvent.setOnClickListener(v -> showAddEventDialog());
        tvQuote.setOnClickListener(v -> viewModel.refreshQuote());
        ivCat.setOnClickListener(v -> viewModel.fetchCat());

        long personId = getIntent().getLongExtra("person_id", -1);
        if (personId != -1) viewModel.loadPersonById(personId);
    }

    private void initViews() {
        calendarGrid = findViewById(R.id.calendarGrid);
        tvHeader = findViewById(R.id.tvHeader);
        tvCounter = findViewById(R.id.tvCounter);
        tvQuote = findViewById(R.id.tvQuote);
        tvMoodToday = findViewById(R.id.tvMoodToday);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnChangePerson = findViewById(R.id.btnChangePerson);
        btnBackToList = findViewById(R.id.btnBackToList);
        moodButtonsContainer = findViewById(R.id.moodButtonsContainer);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        layoutNotes = findViewById(R.id.layoutNotes);
        etThoughts = findViewById(R.id.etThoughts);
        btnSaveThoughts = findViewById(R.id.btnSaveThoughts);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        tvEventInfo = findViewById(R.id.tvEventInfo);
        ivCat = findViewById(R.id.ivCat);
    }

    private void setupMoodButtonsDisplay() {
        if (moodButtonsContainer == null) return;
        moodButtonsContainer.removeAllViews();
        for (int i = 0; i < moodNames.length; i++) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setPadding(8, 8, 8, 8);
            item.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            View color = new View(this);
            color.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(moodColors[i]);
            gd.setCornerRadius(8);
            color.setBackground(gd);
            TextView text = new TextView(this);
            text.setText(moodNames[i]);
            text.setTextSize(10);
            text.setGravity(View.TEXT_ALIGNMENT_CENTER);
            item.addView(color);
            item.addView(text);
            moodButtonsContainer.addView(item);
        }
    }

    private void setupObservers() {
        viewModel.getCurrentPerson().observe(this, person -> {
            if (person != null) {
                tvHeader.setText("Жду: " + person.name);
                int days = viewModel.getDaysRemainingForPerson(person);
                tvCounter.setText(days >= 0 ? "Осталось дней: " + days : "Освободился!");
                renderCalendar();
            }
        });
        viewModel.getDailyQuote().observe(this, quote -> tvQuote.setText(quote));
        viewModel.getSelectedDate().observe(this, date -> {
            selectedDate = date;
            highlightSelectedDate();
            displayMoodForDate(date);
        });
        viewModel.getMoodMap().observe(this, map -> {
            moodMap = map;
            renderCalendar();
        });
        viewModel.getEventMap().observe(this, map -> {
            eventMap = map;
            renderCalendar();
        });
        viewModel.getCurrentEntry().observe(this, entry -> {
            boolean isToday = selectedDate.equals(LocalDate.now());
            if (entry != null) {
                etThoughts.setText(entry.note != null ? entry.note : "");
                layoutNotes.setVisibility((isToday || (entry.note != null && !entry.note.isEmpty())) ? View.VISIBLE : View.GONE);
                tvEventInfo.setVisibility((entry.isVisitationDay || entry.isPackageDay) ? View.VISIBLE : View.GONE);
                if (entry.isVisitationDay) tvEventInfo.setText("В этот день: Встреча");
                else if (entry.isPackageDay) tvEventInfo.setText("В этот день: Передачка");
            } else {
                etThoughts.setText("");
                layoutNotes.setVisibility(isToday ? View.VISIBLE : View.GONE);
                tvEventInfo.setVisibility(View.GONE);
            }
            etThoughts.setEnabled(isToday);
            btnSaveThoughts.setVisibility(isToday ? View.VISIBLE : View.GONE);
        });
    }

    private void displayMoodForDate(LocalDate date) {
        String dateStr = date.toString();
        if (moodMap.containsKey(dateStr)) {
            int type = moodMap.get(dateStr);
            tvMoodToday.setText("Настроение: " + moodNames[type]);
            tvMoodToday.setTextColor(moodColors[type]);
        } else {
            tvMoodToday.setText("Настроение: не выбрано");
            tvMoodToday.setTextColor(Color.GRAY);
        }
    }

    private void renderCalendar() {
        if (calendarGrid == null) return;
        calendarGrid.removeAllViews();
        calendarGrid.setRowCount(7);
        calendarGrid.setColumnCount(7);

        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        for (String d : days) {
            TextView h = new TextView(this);
            h.setText(d);
            h.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            h.setTextColor(Color.RED);
            h.setTypeface(null, android.graphics.Typeface.BOLD);
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0; p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            h.setLayoutParams(p);
            calendarGrid.addView(h);
        }

        YearMonth ym = YearMonth.from(currentDisplayMonth);
        LocalDate first = currentDisplayMonth.withDayOfMonth(1);
        int leading = first.getDayOfWeek().getValue() - 1;
        int currentDay = 1;

        for (int i = 0; i < 42; i++) {
            LocalDate date;
            int num;
            boolean inMonth;
            if (i < leading) {
                date = currentDisplayMonth.minusMonths(1).withDayOfMonth(ym.minusMonths(1).lengthOfMonth() - leading + i + 1);
                num = date.getDayOfMonth(); inMonth = false;
            } else if (currentDay <= ym.lengthOfMonth()) {
                date = currentDisplayMonth.withDayOfMonth(currentDay);
                num = currentDay++; inMonth = true;
            } else {
                date = currentDisplayMonth.plusMonths(1).withDayOfMonth(i - leading - ym.lengthOfMonth() + 1);
                num = date.getDayOfMonth(); inMonth = false;
            }

            Button b = new Button(this);
            b.setText(String.valueOf(num));
            String dateStr = date.toString();
            Integer mType = moodMap.get(dateStr);
            String eType = eventMap.get(dateStr);

            if (!inMonth) { b.setTextColor(Color.GRAY); b.setEnabled(false); }
            else {
                if (mType != null) { b.setBackgroundColor(moodColors[mType]); b.setTextColor(Color.WHITE); }
                else b.setBackgroundColor(Color.TRANSPARENT);
                if (eType != null) b.setText(b.getText() + (eType.equals("VISIT") ? " [В]" : " [П]"));
            }
            if (date.equals(selectedDate) && inMonth) b.setBackgroundColor(mType != null ? moodColors[mType] : 0xFFE91E63);

            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0; p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            b.setLayoutParams(p);
            b.setOnClickListener(v -> {
                if (inMonth) {
                    viewModel.selectDate(date);
                    if (date.equals(LocalDate.now())) showMoodDialog(date);
                }
            });
            calendarGrid.addView(b);
        }
        tvMonthYear.setText(monthFormatter.format(currentDisplayMonth));
    }

    private void highlightSelectedDate() { renderCalendar(); }

    private void showMoodDialog(LocalDate date) {
        new AlertDialog.Builder(this).setTitle("Настроение " + date.format(dayFormatter))
                .setItems(moodNames, (d, w) -> viewModel.saveMoodForDate(w, date))
                .setNegativeButton("Отмена", null).show();
    }

    private void showAddEventDialog() {
        String[] opts = {"Встреча", "Передачка"};
        new AlertDialog.Builder(this).setTitle("Что планируется?")
                .setItems(opts, (d, w) -> {
                    android.app.DatePickerDialog dp = new android.app.DatePickerDialog(this);
                    dp.getDatePicker().setMinDate(System.currentTimeMillis() + 86400000L);
                    dp.setOnDateSetListener((v, yr, mo, day) -> {
                        LocalDate ed = LocalDate.of(yr, mo + 1, day);
                        viewModel.saveEventForDate(ed, w == 0, w == 1);
                    });
                    dp.show();
                }).show();
    }
}