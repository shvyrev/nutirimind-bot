package com.nutrimind.service;

import com.nutrimind.model.UserProfile;
import com.nutrimind.model.enums.ActivityLevel;
import com.nutrimind.model.enums.HealthGoal;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NutritionCalculator {

    public record NutritionPlan(double dailyCalories, double proteinTarget, double fatTarget, double carbsTarget) {}

    public Uni<NutritionPlan> calculatePlan(UserProfile profile) {
        return Uni.createFrom().item(() -> {
            double bmr = calculateBMR(profile);
            double tdee = calculateTDEE(bmr, profile.activityLevel);
            double targetCalories = adjustCaloriesForGoal(tdee, profile.healthGoal);
            
            double protein = calculateProtein(targetCalories, profile.healthGoal);
            double fat = calculateFat(targetCalories);
            double carbs = calculateCarbs(targetCalories, protein, fat);
            
            return new NutritionPlan(targetCalories, protein, fat, carbs);
        });
    }

    private double calculateBMR(UserProfile profile) {
        // Формула Миффлина-Сан Жеора
        if (profile.gender != null && profile.gender.name().equals("MALE")) {
            return 10 * profile.weight + 6.25 * profile.height - 5 * profile.age + 5;
        } else {
            return 10 * profile.weight + 6.25 * profile.height - 5 * profile.age - 161;
        }
    }

    private double calculateTDEE(double bmr, ActivityLevel activityLevel) {
        if (activityLevel == null) {
            return bmr * 1.2; // sedentary by default
        }
        
        return switch (activityLevel) {
            case SEDENTARY -> bmr * 1.2;
            case LIGHTLY_ACTIVE -> bmr * 1.375;
            case MODERATELY_ACTIVE -> bmr * 1.55;
            case VERY_ACTIVE -> bmr * 1.725;
            case EXTREMELY_ACTIVE -> bmr * 1.9;
        };
    }

    private double adjustCaloriesForGoal(double tdee, HealthGoal healthGoal) {
        if (healthGoal == null) {
            return tdee; // maintenance by default
        }
        
        return switch (healthGoal) {
            case WEIGHT_LOSS -> tdee - 500; // deficit for weight loss
            case MUSCLE_GAIN -> tdee + 300; // surplus for muscle gain
            case MAINTENANCE -> tdee;
            case HEALTH_IMPROVEMENT -> tdee - 250; // slight deficit for health
        };
    }

    private double calculateProtein(double calories, HealthGoal healthGoal) {
        double proteinMultiplier = 1.6; // default for maintenance
        if (healthGoal == HealthGoal.MUSCLE_GAIN) {
            proteinMultiplier = 2.2;
        } else if (healthGoal == HealthGoal.WEIGHT_LOSS) {
            proteinMultiplier = 2.0;
        }
        return (calories * 0.25) / 4; // 25% of calories from protein
    }

    private double calculateFat(double calories) {
        return (calories * 0.25) / 9; // 25% of calories from fat
    }

    private double calculateCarbs(double calories, double protein, double fat) {
        double proteinCalories = protein * 4;
        double fatCalories = fat * 9;
        double carbCalories = calories - proteinCalories - fatCalories;
        return carbCalories / 4;
    }
}