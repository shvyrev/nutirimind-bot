package com.nutrimind.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;
import java.util.Map;
import jakarta.persistence.Convert;
import com.nutrimind.model.converter.JsonConverter;
import com.nutrimind.model.enums.OnboardingStepType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "onboarding_states")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OnboardingState extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false)
    public OnboardingStepType currentStep = OnboardingStepType.WELCOME;

    @Column(name = "answers", columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> answers;

    @Column(name = "completed", nullable = false)
    public Boolean completed = false;

    // Reactive finder methods
    public static Uni<OnboardingState> findByUserId(Long userId) {
        return find("user.id", userId).firstResult();
    }

    // Reactive save method
    public Uni<OnboardingState> store() {
        return persist().chain(this::flush).onItem().transform(voidValue -> this);
    }
}