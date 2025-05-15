package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.myapplication.model.Converters;

/**
 * Entity representing a budget/savings/investment/debt plan
 */
@Entity(tableName = "plans")
@TypeConverters({Converters.class})
public class PlanEntity {
    
    @PrimaryKey(autoGenerate = true)
    private int planId;
    
    private String planName;
    private String planDescription;
    private String planType;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Constructor with all fields except ID (which is auto-generated)
    public PlanEntity(String planName, String planDescription, String planType, String frequency, LocalDate startDate, LocalDate endDate) {
        this.planName = planName;
        this.planDescription = planDescription;
        this.planType = planType;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Empty constructor required by Room
    public PlanEntity() {
    }   

    // Getter and setter methods
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }       

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlanEntity{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", planDescription='" + planDescription + '\'' +
                ", planType='" + planType + '\'' +
                ", frequency='" + frequency + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
