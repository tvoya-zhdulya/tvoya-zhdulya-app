package com.example.tvoya_zhdulya.ui;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.tvoya_zhdulya.api.ApiClient;
import com.example.tvoya_zhdulya.api.CatApiService;
import com.example.tvoya_zhdulya.api.CatResponse;
import com.example.tvoya_zhdulya.data.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    private final MutableLiveData<List<Person>> persons = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Person> currentPerson = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> selectedDate = new MutableLiveData<>(LocalDate.now());
    private final MutableLiveData<JournalEntry> currentEntry = new MutableLiveData<>();
    private final MutableLiveData<String> dailyQuote = new MutableLiveData<>();
    private final MutableLiveData<String> catImageUrl = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> moodMap = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Map<String, String>> eventMap = new MutableLiveData<>(new HashMap<>());

    private final String[] localQuotes = {
            "Любовь сильнее расстояний",
            "Каждый день приближает тебя к встрече",
            "Твоя любовь ждет тебя",
            "Ты сильнее, чем думаешь",
            "Скоро все изменится к лучшему",
            "Держись, родная",
            "Время летит быстрее, чем кажется"
    };

    public MainViewModel(Application application) {
        super(application);
        database = AppDatabase.getDatabase(application);
        executorService = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
        loadPersons();
        refreshQuote();
        fetchCat();
    }

    public void fetchCat() {
        CatApiService apiService = ApiClient.getClient().create(CatApiService.class);
        apiService.getRandomCat().enqueue(new Callback<CatResponse>() {
            @Override
            public void onResponse(Call<CatResponse> call, Response<CatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    catImageUrl.postValue("https://cataas.com/cat/" + response.body().id);
                }
            }
            @Override
            public void onFailure(Call<CatResponse> call, Throwable t) {}
        });
    }

    public void refreshQuote() {
        int randomIndex = (int) (Math.random() * localQuotes.length);
        dailyQuote.setValue(localQuotes[randomIndex]);
    }

    public void loadPersons() {
        executorService.execute(() -> {
            List<Person> list = database.personDao().getAllPersonsRaw();
            mainHandler.post(() -> persons.setValue(list != null ? list : new ArrayList<>()));
        });
    }

    public void loadPersonById(long id) {
        executorService.execute(() -> {
            Person p = database.personDao().getPersonByIdRaw(id);
            mainHandler.post(() -> {
                currentPerson.setValue(p);
                loadDataForDate(selectedDate.getValue());
                loadAllMoodsForPerson(p.id);
                loadEventsForPerson(p.id);
            });
        });
    }

    private void loadAllMoodsForPerson(long personId) {
        executorService.execute(() -> {
            List<MoodEntry> moods = database.moodDao().getAllMoodsForPersonRaw(personId);
            Map<String, Integer> moodMapData = new HashMap<>();
            for (MoodEntry mood : moods) {
                moodMapData.put(mood.date, mood.moodType);
            }
            mainHandler.post(() -> moodMap.setValue(moodMapData));
        });
    }

    private void loadEventsForPerson(long personId) {
        executorService.execute(() -> {
            List<JournalEntry> entries = database.journalDao().getAllEntriesForPersonRaw(personId);
            Map<String, String> events = new HashMap<>();
            for (JournalEntry entry : entries) {
                if (entry.isVisitationDay) events.put(entry.date, "VISIT");
                else if (entry.isPackageDay) events.put(entry.date, "PACK");
            }
            mainHandler.post(() -> eventMap.setValue(events));
        });
    }

    public void saveMoodForDate(int type, LocalDate date) {
        Person p = currentPerson.getValue();
        if (p == null || date == null) return;
        executorService.execute(() -> {
            database.moodDao().insertOrUpdate(new MoodEntry(p.id, date.toString(), type));
            loadAllMoodsForPerson(p.id);
        });
    }

    public void addPerson(String name, String date, int color) {
        executorService.execute(() -> {
            database.personDao().insert(new Person(name, date, color));
            loadPersons();
        });
    }

    public void deletePerson(Person person) {
        executorService.execute(() -> {
            database.personDao().delete(person.id);
            loadPersons();
        });
    }

    public void selectDate(LocalDate date) {
        selectedDate.setValue(date);
        loadDataForDate(date);
    }

    private void loadDataForDate(LocalDate date) {
        Person p = currentPerson.getValue();
        if (p == null || date == null) return;
        executorService.execute(() -> {
            final JournalEntry entry = database.journalDao().getEntryByDateRaw(p.id, date.toString());
            mainHandler.post(() -> currentEntry.setValue(entry));
        });
    }

    public void saveNoteForDate(LocalDate date, String note) {
        Person p = currentPerson.getValue();
        if (p == null || date == null) return;
        executorService.execute(() -> {
            JournalEntry entry = database.journalDao().getEntryByDateRaw(p.id, date.toString());
            if (entry == null) {
                entry = new JournalEntry(p.id, date.toString(), note, false, false);
            } else {
                entry.note = note;
            }
            database.journalDao().insertOrUpdate(entry);
            loadDataForDate(date);
        });
    }

    public void saveEventForDate(LocalDate date, boolean isVisit, boolean isPackage) {
        Person p = currentPerson.getValue();
        if (p == null || date == null) return;
        executorService.execute(() -> {
            JournalEntry entry = database.journalDao().getEntryByDateRaw(p.id, date.toString());
            if (entry == null) {
                entry = new JournalEntry(p.id, date.toString(), "", isVisit, isPackage);
            } else {
                entry.isVisitationDay = isVisit;
                entry.isPackageDay = isPackage;
            }
            database.journalDao().insertOrUpdate(entry);
            loadEventsForPerson(p.id);
            loadDataForDate(selectedDate.getValue());
        });
    }

    public int getDaysRemainingForPerson(Person person) {
        if (person == null || person.releaseDate == null) return 0;
        try {
            LocalDate release = LocalDate.parse(person.releaseDate, formatter);
            return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), release);
        } catch (Exception e) {
            return 0;
        }
    }

    public LiveData<List<Person>> getPersons() { return persons; }
    public LiveData<Person> getCurrentPerson() { return currentPerson; }
    public LiveData<JournalEntry> getCurrentEntry() { return currentEntry; }
    public LiveData<String> getDailyQuote() { return dailyQuote; }
    public LiveData<String> getCatImageUrl() { return catImageUrl; }
    public LiveData<LocalDate> getSelectedDate() { return selectedDate; }
    public LiveData<Map<String, Integer>> getMoodMap() { return moodMap; }
    public LiveData<Map<String, String>> getEventMap() { return eventMap; }
}