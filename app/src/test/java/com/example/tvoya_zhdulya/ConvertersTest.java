package com.example.tvoya_zhdulya;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import com.example.tvoya_zhdulya.data.Converters;
import java.time.LocalDate;
import org.junit.Test;

public class ConvertersTest {
    @Test
    public void testDateToString() {
        LocalDate date = LocalDate.of(2026, 5, 12);
        assertEquals("2026-05-12", Converters.dateToString(date));
    }

    @Test
    public void testFromString() {
        String dateStr = "2026-05-12";
        LocalDate expected = LocalDate.of(2026, 5, 12);
        assertEquals(expected, Converters.fromString(dateStr));
    }

    @Test
    public void testNullHandling() {
        assertNull(Converters.dateToString(null));
        assertNull(Converters.fromString(null));
    }
}