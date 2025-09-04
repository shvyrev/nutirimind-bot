package com.nutrimind.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AnswerValidator {

    public Uni<Boolean> validateAgeHeightWeight(String answer) {
        try {
            String[] parts = answer.split(" ");
            if (parts.length != 3) {
                return Uni.createFrom().item(false);
            }
            
            int age = Integer.parseInt(parts[0]);
            double height = Double.parseDouble(parts[1]);
            double weight = Double.parseDouble(parts[2]);
            
            if (age < 12 || age > 120) {
                return Uni.createFrom().item(false);
            }
            
            if (height < 100 || height > 250) {
                return Uni.createFrom().item(false);
            }
            
            if (weight < 30 || weight > 300) {
                return Uni.createFrom().item(false);
            }
            
            return Uni.createFrom().item(true);
        } catch (NumberFormatException e) {
            return Uni.createFrom().item(false);
        }
    }

    public Uni<Boolean> validateHealthGoal(String answer) {
        String lowerAnswer = answer.toLowerCase();
        return Uni.createFrom().item(
            lowerAnswer.contains("похуд") ||
            lowerAnswer.contains("набор") ||
            lowerAnswer.contains("мыш") ||
            lowerAnswer.contains("сбаланс") ||
            lowerAnswer.contains("поддерж")
        );
    }

    public Uni<Boolean> validateActivityLevel(String answer) {
        // Простая проверка - ответ должен содержать число
        try {
            String cleanAnswer = answer.replaceAll("[^0-9]", "");
            if (!cleanAnswer.isEmpty()) {
                int times = Integer.parseInt(cleanAnswer);
                return Uni.createFrom().item(times >= 0 && times <= 7);
            }
            return Uni.createFrom().item(false);
        } catch (NumberFormatException e) {
            return Uni.createFrom().item(false);
        }
    }

    public Uni<Boolean> validateCommunicationStyle(String answer) {
        String lowerAnswer = answer.toLowerCase();
        return Uni.createFrom().item(
            lowerAnswer.contains("строг") ||
            lowerAnswer.contains("наставник") ||
            lowerAnswer.contains("легк") ||
            lowerAnswer.contains("приятель")
        );
    }
}