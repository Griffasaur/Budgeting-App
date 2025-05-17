package com.example.myapplication.model;

import androidx.room.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Type converters for Room database
 * Handles conversion of custom types to/from database-storable formats
 */
public class Converters {
    // BigDecimal converters
    @TypeConverter
    public static BigDecimal fromString(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public static String toString(BigDecimal value) {
        return value == null ? null : value.toString();
    }
    
    // LocalDate converters
    @TypeConverter
    public static LocalDate fromTimestamp(String value) {
        return value == null ? null : LocalDate.parse(value);
    }
    
    @TypeConverter
    public static String dateToTimestamp(LocalDate date) {
        return date == null ? null : date.toString();
    }
    
    // TransactionType converters
    @TypeConverter
    public static TransactionType toTransactionType(String value) {
        return value == null ? null : TransactionType.valueOf(value);
    }
    
    @TypeConverter
    public static String fromTransactionType(TransactionType type) {
        return type == null ? null : type.name();
    }
    
    // Frequency converters
    @TypeConverter
    public static Frequency toFrequency(String value) {
        return value == null ? null : Frequency.valueOf(value);
    }
    
    @TypeConverter
    public static String fromFrequency(Frequency frequency) {
        return frequency == null ? null : frequency.name();
    }
}

