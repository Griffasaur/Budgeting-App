package com.example.myapplication.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.IncomeEntity;

import java.util.List;

@Dao
public interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(IncomeEntity income);

    @Update
    void update(IncomeEntity income);

    @Delete
    void delete(IncomeEntity income);

    @Query("DELETE FROM incomes WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM incomes WHERE id = :id")
    LiveData<IncomeEntity> getIncomeById(long id);

    @Query("SELECT * FROM incomes ORDER BY name ASC")
    LiveData<List<IncomeEntity>> getAllIncomesByName();

    @Query("SELECT * FROM incomes ORDER BY amount DESC")
    LiveData<List<IncomeEntity>> getAllIncomesByAmount();

    @Query("SELECT * FROM incomes ORDER BY frequency ASC")
    LiveData<List<IncomeEntity>> getAllIncomesByFrequency();

    @Query("SELECT * FROM incomes ORDER BY dateAdded DESC")
    LiveData<List<IncomeEntity>> getAllIncomesByDateAdded();

    @Query("SELECT SUM(amount) FROM incomes WHERE frequency = 'MONTHLY'")
    LiveData<Double> getTotalMonthlyIncome();

    @Query("SELECT SUM(CASE " +
           "WHEN frequency = 'DAILY' THEN amount * 30 " +
           "WHEN frequency = 'WEEKLY' THEN amount * 4.33 " +
           "WHEN frequency = 'BI_WEEKLY' THEN amount * 2.17 " +
           "WHEN frequency = 'MONTHLY' THEN amount " +
           "WHEN frequency = 'QUARTERLY' THEN amount / 3 " +
           "WHEN frequency = 'YEARLY' THEN amount / 12 " +
           "ELSE amount END) " +
           "FROM incomes")
    LiveData<Double> getEstimatedMonthlyIncome();
} 