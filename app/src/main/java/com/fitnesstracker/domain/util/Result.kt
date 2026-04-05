package com.fitnesstracker.domain.util

/**
 * A generic Result wrapper for use case return values.
 *
 * Why not use Kotlin's built-in kotlin.Result?
 * Kotlin's Result cannot be used as a return type in some contexts (e.g. suspend functions
 * returning Result<T> have restrictions with coroutines). This custom sealed class gives
 * us full control and clarity.
 *
 * Sealed class: the compiler knows ALL possible subtypes at compile time.
 * This means when you write a 'when' expression on Result<T>, the IDE enforces
 * that you handle both Success and Error - no silent unhandled cases.
 *
 * Usage in ViewModel:
 *   when (val result = getWorkoutsUseCase()) {
 *       is Result.Success -> showWorkouts(result.data)
 *       is Result.Error   -> showError(result.exception.message)
 *   }
 */
sealed class Result<out T> {

    /**
     * Represents a successful operation.
     * 'out T' means Result is covariant: Result<Workout> is a subtype of Result<Any>.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation.
     * The exception carries the cause - could be an IOException, SQLException, etc.
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    // ---------------------------------------------------------------------------
    // Convenience helpers
    // ---------------------------------------------------------------------------

    /** True if this is a Success */
    val isSuccess: Boolean get() = this is Success

    /** True if this is an Error */
    val isError: Boolean get() = this is Error

    /**
     * Returns the data if Success, otherwise null.
     * Useful in cases where you don't need to handle the error path.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Returns the data if Success, otherwise the provided default.
     */
    fun getOrDefault(default: @UnsafeVariance T): T = (this as? Success)?.data ?: default
}
