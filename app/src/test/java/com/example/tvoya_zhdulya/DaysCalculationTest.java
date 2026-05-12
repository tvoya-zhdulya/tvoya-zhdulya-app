package com.example.tvoya_zhdulya;

import static org.junit.Assert.assertEquals;
import com.example.tvoya_zhdulya.data.Person;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.Test;

public class DaysCalculationTest {
    @Test
    public void testDaysRemainingCalculation() {
        LocalDate today = LocalDate.now();
        LocalDate releaseDate = today.plusDays(15);

        Person person = new Person("Name", releaseDate.toString(), 0);

        long expected = 15;
        long actual = ChronoUnit.DAYS.between(today, LocalDate.parse(person.releaseDate));

        assertEquals(expected, actual);
    }
}