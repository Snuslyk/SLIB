package com.github.Snuslyk.slib.util;

import com.github.Snuslyk.slib.controls.fields.DatePickerField;
import com.github.Snuslyk.slib.factory.TextFieldWrapper;
import javafx.scene.control.DatePicker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeUtil {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);

    public static long now(){
        return Instant.now().getEpochSecond();
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
