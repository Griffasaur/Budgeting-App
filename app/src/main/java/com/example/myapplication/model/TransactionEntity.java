package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.myapplication.model.Converters;

@Entity(tableName = "transactions")
@TypeConverters({Converters.class})
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private int transactionId;
    private String transactionName;
    private String transactionType;
    private BigDecimal transactionAmount;
    private String transactionDescription;
    private String transactionFrequency;
    private LocalDate startDate;
    private LocalDate nextDueDate;

    public TransactionEntity(String transactionName, String transactionType, BigDecimal transactionAmount, String transactionDescription, String transactionFrequency, LocalDate startDate, LocalDate nextDueDate) {
        this.transactionName = transactionName;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionDescription = transactionDescription;
        this.transactionFrequency = transactionFrequency;
        this.startDate = startDate;
        this.nextDueDate = nextDueDate;
    }

    public TransactionEntity() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public String getTransactionFrequency() {
        return transactionFrequency;
    }

    public void setTransactionFrequency(String transactionFrequency) {
        this.transactionFrequency = transactionFrequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "TransactionEntity{" +
                "transactionId=" + transactionId +
                ", transactionName='" + transactionName + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", transactionDescription='" + transactionDescription + '\'' +
                ", transactionFrequency='" + transactionFrequency + '\'' +
                ", startDate=" + startDate +
                ", nextDueDate=" + nextDueDate +
                '}';
    }
}
