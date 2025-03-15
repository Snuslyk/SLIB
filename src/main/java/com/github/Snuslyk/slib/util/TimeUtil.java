package com.github.Snuslyk.slib.util;

import com.github.Snuslyk.slib.controls.fields.DatePickerField;
import com.github.Snuslyk.slib.factory.TextFieldWrapper;
import javafx.scene.control.DatePicker;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class TimeUtil {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);

    public static long now(){
        return Instant.now().getEpochSecond();
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : dateFormatter.format(date);
    }

    public static LocalDate parseDate(String dateString) {
        return (dateString == null || dateString.isEmpty()) ? null : LocalDate.parse(dateString, dateFormatter);
    }

    public static String formatDateRange(Set<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) return "";
        TreeSet<LocalDate> sortedDates = new TreeSet<>(dates);
        LocalDate minDate = sortedDates.first();
        LocalDate maxDate = sortedDates.last();
        return minDate.equals(maxDate) ? formatDate(minDate) : formatDate(minDate) + " - " + formatDate(maxDate);
    }

    public static Set<LocalDate> parseDateRange(String dateRange) {
        Set<LocalDate> parsedDates = new LinkedHashSet<>();

        if (dateRange == null || dateRange.trim().isEmpty()) {
            return parsedDates;
        }

        String[] parts = dateRange.split(" - ");
        if (parts.length == 1) {
            parsedDates.add(parseDate(parts[0]));
        } else if (parts.length == 2) {
            LocalDate startDate = parseDate(parts[0]);
            LocalDate endDate = parseDate(parts[1]);

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                parsedDates.add(date);
            }
        }

        return parsedDates;
    }

    public static long parseDataPicker(TextFieldWrapper field){

        DatePicker datePicker = ((DatePickerField) field).getDatePicker();
        if (datePicker.getValue() == null){
            return 0;
        }

        return ((DatePickerField) field).getDatePicker().getValue().toEpochSecond(LocalTime.now(), ZoneOffset.UTC);
    }

    public static String formatToColumnDisplay(Object object){
        return formatTime((int) Instant.ofEpochSecond(((long) object)).getEpochSecond());
    }

    public static String formatTime(int seconds){
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        return dateTime.format(dateFormatter);
    }
}
