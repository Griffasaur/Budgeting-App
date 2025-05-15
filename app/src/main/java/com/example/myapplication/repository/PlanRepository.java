package com.example.myapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.dao.PlanDao;
import com.example.myapplication.model.PlanEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PlanRepository {
    private final PlanDao planDao;
    private final LiveData<List<PlanEntity>> allPlans;
    private final ExecutorService executorService;

    public PlanRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        planDao = database.planDao();
        allPlans = planDao.getAllPlans();
        executorService = Executors.newFixedThreadPool(4);
    }

    //-------------------- CREATE OPERATIONS --------------------//

    /**
     * Insert a plan asynchronously without callback
     */
    public void insert(PlanEntity plan) {
        executorService.execute(() -> planDao.insert(plan));
    }
    
    /**
     * Insert a plan asynchronously with callback
     */
    public void insert(PlanEntity plan, OnPlanInsertedListener listener) {
        executorService.execute(() -> { 
            long id = planDao.insert(plan);
            plan.setPlanId((int)id);
            listener.onPlanInserted(plan);
        });
    }

    //-------------------- READ OPERATIONS --------------------//
    
    /**
     * Get all plans as LiveData
     */
    public LiveData<List<PlanEntity>> getAllPlans() {
        return allPlans;
    }
    
    /**
     * Get a specific plan by ID as LiveData
     */
    public LiveData<PlanEntity> getPlanByIdLive(int id) {
        return planDao.getPlanById(id);
    }
    
    /**
     * Get a specific plan by ID with callback
     */
    public void getPlanById(int id, OnPlanLoadedListener listener) {
        executorService.execute(() -> {
            PlanEntity plan = planDao.getPlanByIdSync(id); 
            listener.onPlanLoaded(plan);
        });
    }
    
    /**
     * Get plans filtered by type
     */
    public LiveData<List<PlanEntity>> getPlansByType(String type) {
        return planDao.getPlansByType(type);
    }
    
    /**
     * Get plans filtered by frequency
     */
    public LiveData<List<PlanEntity>> getPlansByFrequency(String frequency) {
        return planDao.getPlansByFrequency(frequency);
    }
    
    /**
     * Get plans within a date range
     */
    public LiveData<List<PlanEntity>> getPlansByDateRange(LocalDate startDate, LocalDate endDate) {
        return planDao.getPlansByDateRange(startDate, endDate);
    }
    
    /**
     * Get plan by name
     */
    public LiveData<PlanEntity> getPlanByName(String name) {
        return planDao.getPlanByName(name);
    }
    
    /**
     * Get plans by description
     */
    public LiveData<List<PlanEntity>> getPlansByDescription(String description) {
        return planDao.getPlansByDescription(description);
    }
    
    /**
     * Get all budget plans
     */
    public LiveData<List<PlanEntity>> getAllBudgetPlans() {
        return planDao.getAllBudgetPlans();
    }
    
    /**
     * Get all savings plans
     */
    public LiveData<List<PlanEntity>> getAllSavingsPlans() {
        return planDao.getAllSavingsPlans();
    }
    
    /**
     * Get all investment plans
     */
    public LiveData<List<PlanEntity>> getAllInvestmentPlans() {
        return planDao.getAllInvestmentPlans();
    }
    
    /**
     * Get all debt plans
     */
    public LiveData<List<PlanEntity>> getAllDebtPlans() {
        return planDao.getAllDebtPlans();
    }
    
    /**
     * Get plan count without callback
     */
    public int getPlanCount() {
        return planDao.getPlanCount();
    }
    
    /**
     * Get plan count with callback
     */
    public void getPlanCount(OnPlanCountListener listener) {
        executorService.execute(() -> {
            int count = planDao.getPlanCount();
            listener.onPlanCount(count);
        });
    }

    //-------------------- UPDATE OPERATIONS --------------------//
    
    /**
     * Update a plan asynchronously without callback
     */
    public void update(PlanEntity plan) {
        executorService.execute(() -> planDao.update(plan));
    }
    
    /**
     * Update a plan asynchronously with callback
     */
    public void update(PlanEntity plan, OnPlanUpdatedListener listener) {
        executorService.execute(() -> {
            int rowsUpdated = planDao.update(plan);
            listener.onPlanUpdated(rowsUpdated);
        });
    }

    //-------------------- DELETE OPERATIONS --------------------//
    
    /**
     * Delete a plan asynchronously without callback
     */
    public void delete(PlanEntity plan) {
        executorService.execute(() -> planDao.delete(plan));
    }
    
    /**
     * Delete a plan asynchronously with callback
     */
    public void delete(PlanEntity plan, OnPlanDeletedListener listener) {
        executorService.execute(() -> {
            int rowsDeleted = planDao.delete(plan);
            listener.onPlanDeleted(rowsDeleted);
        });
    }
    
    /**
     * Delete a plan by ID without callback
     */
    public void deleteById(int id) {
        executorService.execute(() -> planDao.deleteById(id));
    }
    
    /**
     * Delete a plan by ID with callback
     */
    public void deleteById(int id, OnPlanDeletedByIdListener listener) {
        executorService.execute(() -> {
            int rowsDeleted = planDao.deleteById(id);
            listener.onPlanDeletedById(rowsDeleted);
        });
    }

    //-------------------- CALLBACK INTERFACES --------------------//
    
    /**
     * Callback for when a plan is loaded
     */
    public interface OnPlanLoadedListener {
        void onPlanLoaded(PlanEntity plan);
    }
    
    /**
     * Callback for when a plan is inserted
     */
    public interface OnPlanInsertedListener {
        void onPlanInserted(PlanEntity plan);
    }
    
    /**
     * Callback for when a plan is updated
     */
    public interface OnPlanUpdatedListener {
        void onPlanUpdated(int rowsUpdated);
    }
    
    /**
     * Callback for when a plan is deleted
     */
    public interface OnPlanDeletedListener {
        void onPlanDeleted(int rowsDeleted);
    }
    
    /**
     * Callback for when a plan is deleted by ID
     */
    public interface OnPlanDeletedByIdListener {
        void onPlanDeletedById(int rowsDeleted);
    }
    
    /**
     * Callback for when plan count is retrieved
     */
    public interface OnPlanCountListener {
        void onPlanCount(int count);
    }
}
