package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use Case: Get all workouts as a reactive stream.
 *
 * A Use Case (also called Interactor) encapsulates ONE piece of business logic.
 * This class has a single public method - the invoke() operator - which lets
 * you call it like a function: getWorkoutsUseCase()
 *
 * Why Use Cases instead of calling the repository directly from the ViewModel?
 * - ViewModels can get bloated with logic
 * - Use Cases are independently unit-testable
 * - Use Cases can be reused across multiple ViewModels
 * - Use Cases can compose multiple repository calls into one operation
 *
 * @Inject: Hilt provides the WorkoutRepository (as the interface, bound in RepositoryModule).
 */
class GetWorkoutsUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    /**
     * Invoke operator: allows calling this class like a function.
     * Returns Flow<Result<List<Workout>>> - a stream where each emission is
     * either a successful list or an error.
     *
     * Flow.map wraps successful data in Result.Success.
     * Flow.catch intercepts any upstream exception and emits Result.Error.
     */
    operator fun invoke(): Flow<Result<List<Workout>>> =
        repository.getAllWorkouts()
            .map { workouts -> Result.Success(workouts) as Result<List<Workout>> }
            .catch { throwable ->
                emit(Result.Error(Exception(throwable)))
            }
}
