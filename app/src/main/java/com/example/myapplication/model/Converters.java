package com.example.myapplication.model;

import androidx.room.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Converters {
    @TypeConverter
    public static BigDecimal fromString(String value) {
        return value == null ? null : new BigDecimal(value);
    }
    @TypeConverter
    public static String toString(BigDecimal value) {
        return value == null ? null : value.toString();
    }
    @TypeConverter
    public static LocalDate fromTimestamp(String value) {
        return value == null ? null : LocalDate.parse(value);
    }
    @TypeConverter
    public static String dateToTimestamp(LocalDate date) {
        return date == null ? null : date.toString();
    }
}

