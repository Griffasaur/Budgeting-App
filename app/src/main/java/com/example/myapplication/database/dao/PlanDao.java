package com.example.myapplication.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.PlanEntity;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface PlanDao {

    // Create operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PlanEntity plan);

    // Update operations    
    @Update
    int update(PlanEntity plan);

    // Delete operations
    @Delete
    int delete(PlanEntity plan);
    
    @Query("DELETE FROM plans WHERE planId = :id")
    int deleteById(int id);

    // Read operations
    @Query("SELECT * FROM plans")
    LiveData<List<PlanEntity>> getAllPlans();

    @Query("SELECT * FROM plans WHERE planId = :id")
    LiveData<PlanEntity> getPlanById(int id);
    
    @Query("SELECT * FROM plans WHERE planId = :id")
    PlanEntity getPlanByIdSync(int id);

    @Query("SELECT * FROM plans WHERE planType = :type")
    LiveData<List<PlanEntity>> getPlansByType(String type);

    @Query("SELECT * FROM plans WHERE frequency = :frequency")
    LiveData<List<PlanEntity>> getPlansByFrequency(String frequency);

    @Query("SELECT * FROM plans WHERE startDate BETWEEN :startDate AND :endDate")
    LiveData<List<PlanEntity>> getPlansByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM plans WHERE planName = :name")
    LiveData<PlanEntity> getPlanByName(String name);

    @Query("SELECT * FROM plans WHERE planDescription LIKE '%' || :description || '%'")
    LiveData<List<PlanEntity>> getPlansByDescription(String description);

    @Query("SELECT * FROM plans WHERE planType = 'BUDGET'")
    LiveData<List<PlanEntity>> getAllBudgetPlans();

    @Query("SELECT * FROM plans WHERE planType = 'SAVINGS'")
    LiveData<List<PlanEntity>> getAllSavingsPlans();

    @Query("SELECT * FROM plans WHERE planType = 'INVESTMENT'")
    LiveData<List<PlanEntity>> getAllInvestmentPlans();

    @Query("SELECT * FROM plans WHERE planType = 'DEBT'")
    LiveData<List<PlanEntity>> getAllDebtPlans();

    @Query("SELECT COUNT(*) FROM plans")
    int getPlanCount(); 
}
