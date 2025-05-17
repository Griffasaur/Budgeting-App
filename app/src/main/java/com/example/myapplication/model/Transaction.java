package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Unified Transaction entity that represents both income and expenses in the budgeting app
 */
@Entity(tableName = "transactions")
@TypeConverters({Converters.class})
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private BigDecimal amount;
    private TransactionType type;  // INCOME or EXPENSE
    private Frequency frequency;
    private String category;  // Job, Utility, Food, etc.
    private String description;
    private LocalDate startDate;
    private LocalDate nextDueDate;
    private LocalDate createdDate;
    private LocalDate lastUpdatedDate;

    // Default constructor required by Room
    public Transaction() {
        this.createdDate = LocalDate.now();
        this.lastUpdatedDate = LocalDate.now();
    }
    
    // Constructor
    public Transaction(String name, BigDecimal amount, TransactionType type, 
                     Frequency frequency, String category, String description,
                     LocalDate startDate) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.frequency = frequency;
        this.category = category;
        this.description = description;
        this.startDate = startDate;
        this.nextDueDate = calculateNextDueDate(startDate, frequency);
        this.createdDate = LocalDate.now();
        this.lastUpdatedDate = LocalDate.now();
    }
    
    // Calculate the next due date based on frequency
    private LocalDate calculateNextDueDate(LocalDate start, Frequency freq) {
        if (start == null) return null;
        
        LocalDate today = LocalDate.now();
        LocalDate next = start;
        
        while (next.isBefore(today) || next.isEqual(today)) {
            switch (freq) {
                case DAILY:
                    next = next.plusDays(1);
                    break;
                case WEEKLY:
                    next = next.plusWeeks(1);
                    break;
                case BI_WEEKLY:
                    next = next.plusWeeks(2);
                    break;
                case MONTHLY:
                    next = next.plusMonths(1);
                    break;
                case QUARTERLY:
                    next = next.plusMonths(3);
                    break;
                case YEARLY:
                    next = next.plusYears(1);
                    break;
                default:
                    return start;  // For CUSTOM or other cases
            }
        }
        
        return next;
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public Frequency getFrequency() {
        return frequency;
    }
    
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
        // Update next due date when frequency changes
        if (this.startDate != null) {
            this.nextDueDate = calculateNextDueDate(this.startDate, frequency);
        }
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        // Update next due date when start date changes
        if (startDate != null && this.frequency != null) {
            this.nextDueDate = calculateNextDueDate(startDate, this.frequency);
        }
    }
    
    public LocalDate getNextDueDate() {
        return nextDueDate;
    }
    
    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }
    
    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
    
    // Helper method to calculate monthly equivalent
    public BigDecimal getMonthlyEquivalent() {
        if (amount == null) return BigDecimal.ZERO;
        
        switch (frequency) {
            case DAILY:
                return amount.multiply(new BigDecimal("30"));
            case WEEKLY:
                return amount.multiply(new BigDecimal("4.33"));
            case BI_WEEKLY:
                return amount.multiply(new BigDecimal("2.17"));
            case MONTHLY:
                return amount;
            case QUARTERLY:
                return amount.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_HALF_UP);
            case YEARLY:
                return amount.divide(new BigDecimal("12"), 2, BigDecimal.ROUND_HALF_UP);
            default:
                return amount;
        }
    }
    
    @NonNull
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", frequency=" + frequency +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", nextDueDate=" + nextDueDate +
                '}';
    }
} 