package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import javax.inject.Inject

/**
 * Ensures the 3 default templates exist on first launch.
 * A no-op if templates already exist.
 */
class SeedDefaultTemplatesUseCase @Inject constructor(
    private val repository: WorkoutTemplateRepository
) {
    suspend operator fun invoke() {
        repository.seedDefaultTemplatesIfEmpty()
    }
}
