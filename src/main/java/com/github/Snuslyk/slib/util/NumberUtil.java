package com.github.Snuslyk.slib.util;

import com.github.Snuslyk.slib.factory.ButtonFactory;

public class NumberUtil {

    public static boolean fieldCheck(ButtonFactory.TextFieldWrapper field, FieldSetValue value, Flags... flags){
        Object error = isCorrectNumber(field.getTextFieldText(), flags);
        if (error instanceof String string){
            field.setError(string);
            return false;
        } else {
            value.run((double) error);
            return true;
        }
    }

    public interface FieldSetValue{
        void run(double num);
    }

    public static Object isCorrectNumber(String input, Flags... flags){
        if (input.isBlank()) {
            return "Пустое поле!";
        }

        input = input.replace(",",".");

        double num;
        try {
            num = Double.parseDouble(input);
        }
        catch (NumberFormatException e) {
            return "Не числовое значение!";
        }

        for (Flags flag: flags){
            if (flag.check(num)){
                return flag.error;
            }
        }

        return num;
    }

    public enum Flags {
        POSITIVE("Число отрицательное!", num -> num < 0),
        INT("Не целочисленое значение!", num -> num % 1 != 0);


        private final String error;
        private final CheckNumber check;
        Flags(String error, CheckNumber check){
            this.error = error;
            this.check = check;
        }

        public String getError() {
            return error;
        }
        public boolean check(double num){
            return check.check(num);
        }
    }
    public interface CheckNumber{
        boolean check(double num);
    }
}
