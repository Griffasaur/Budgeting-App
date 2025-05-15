package com.example.myapplication.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Date;

/**
 * Represents a source of income in the budgeting app
 */
@Entity(tableName = "incomes")
public class IncomeEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private double amount;
    
    @TypeConverters(Converters.class)
    private Frequency frequency;
    
    private String source; // e.g., "Job", "Investments", "Side Hustle"
    private String notes;
    
    @TypeConverters(Converters.class)
    private Date dateAdded;
    
    @TypeConverters(Converters.class)
    private Date lastUpdated;

    // Default constructor required by Room
    public IncomeEntity() {}
    
    // Constructor
    public IncomeEntity(String name, double amount, Frequency frequency, String source, String notes) {
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.source = source;
        this.notes = notes;
        this.dateAdded = new Date();
        this.lastUpdated = new Date();
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public Frequency getFrequency() {
        return frequency;
    }
    
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Date getDateAdded() {
        return dateAdded;
    }
    
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    // Helper method to calculate monthly income equivalent
    public double getMonthlyAmount() {
        switch (frequency) {
            case DAILY:
                return amount * 30; // Approximate
            case WEEKLY:
                return amount * 4.33; // Approximate
            case BI_WEEKLY:
                return amount * 2.17; // Approximate
            case MONTHLY:
                return amount;
            case QUARTERLY:
                return amount / 3;
            case YEARLY:
                return amount / 12;
            default:
                return amount;
        }
    }
} 