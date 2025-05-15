package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.PlanEntity;
import com.example.myapplication.repository.PlanRepository;

import java.time.LocalDate;
import java.util.List;


public class PlanViewModel extends AndroidViewModel {
    private final PlanRepository repository;
    private final LiveData<List<PlanEntity>> allPlans;

    public PlanViewModel(Application application) {
        super(application);
        repository = new PlanRepository(application);
        allPlans = repository.getAllPlans();
    }

    //-------------------- CREATE OPERATIONS --------------------//

    /**
     * Insert a plan asynchronously without callback
     */
    public void insert(PlanEntity plan) {
        repository.insert(plan);
    }
    
    /**
     * Insert a plan asynchronously with callback
     */
    public void insert(PlanEntity plan, PlanRepository.OnPlanInsertedListener listener) {
        repository.insert(plan, listener);
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
        return repository.getPlanByIdLive(id);
    }
    
    /**
     * Get a specific plan by ID with callback
     */
    public void getPlanById(int id, PlanRepository.OnPlanLoadedListener listener) {
        repository.getPlanById(id, listener);
    }
    
    /**
     * Get plans filtered by type
     */
    public LiveData<List<PlanEntity>> getPlansByType(String type) {
        return repository.getPlansByType(type);
    }
    
    /**
     * Get plans filtered by frequency
     */
    public LiveData<List<PlanEntity>> getPlansByFrequency(String frequency) {
        return repository.getPlansByFrequency(frequency);
    }
    
    /**
     * Get plans within a date range
     */
    public LiveData<List<PlanEntity>> getPlansByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.getPlansByDateRange(startDate, endDate);
    }
    
    /**
     * Get plan by name
     */
    public LiveData<PlanEntity> getPlanByName(String name) {
        return repository.getPlanByName(name);
    }
    
    /**
     * Get plans by description
     */
    public LiveData<List<PlanEntity>> getPlansByDescription(String description) {
        return repository.getPlansByDescription(description);
    }
    
    /**
     * Get all budget plans
     */
    public LiveData<List<PlanEntity>> getAllBudgetPlans() {
        return repository.getAllBudgetPlans();
    }
    
    /**
     * Get all savings plans
     */
    public LiveData<List<PlanEntity>> getAllSavingsPlans() {
        return repository.getAllSavingsPlans();
    }
    
    /**
     * Get all investment plans
     */
    public LiveData<List<PlanEntity>> getAllInvestmentPlans() {
        return repository.getAllInvestmentPlans();
    }
    
    /**
     * Get all debt plans
     */
    public LiveData<List<PlanEntity>> getAllDebtPlans() {
        return repository.getAllDebtPlans();
    }
    
    /**
     * Get plan count without callback
     */
    public int getPlanCount() {
        return repository.getPlanCount();
    }
    
    /**
     * Get plan count with callback
     */
    public void getPlanCount(PlanRepository.OnPlanCountListener listener) {
        repository.getPlanCount(listener);
    }

    //-------------------- UPDATE OPERATIONS --------------------//
    
    /**
     * Update a plan asynchronously without callback
     */
    public void update(PlanEntity plan) {
        repository.update(plan);
    }
    
    /**
     * Update a plan asynchronously with callback
     */
    public void update(PlanEntity plan, PlanRepository.OnPlanUpdatedListener listener) {
        repository.update(plan, listener);
    }

    //-------------------- DELETE OPERATIONS --------------------//
    
    /**
     * Delete a plan asynchronously without callback
     */
    public void delete(PlanEntity plan) {
        repository.delete(plan);
    }
    
    /**
     * Delete a plan asynchronously with callback
     */
    public void delete(PlanEntity plan, PlanRepository.OnPlanDeletedListener listener) {
        repository.delete(plan, listener);
    }
    
    /**
     * Delete a plan by ID without callback
     */
    public void deleteById(int id) {
        repository.deleteById(id);
    }
    
    /**
     * Delete a plan by ID with callback
     */
    public void deleteById(int id, PlanRepository.OnPlanDeletedByIdListener listener) {
        repository.deleteById(id, listener);
    }
}
