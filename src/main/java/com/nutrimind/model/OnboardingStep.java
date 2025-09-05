package com.nutrimind.model;

import com.nutrimind.model.enums.OnboardingStepType;

public record OnboardingStep(int stepNumber, OnboardingStepType type, String question) {}