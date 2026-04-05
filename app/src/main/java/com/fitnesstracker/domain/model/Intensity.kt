package com.fitnesstracker.domain.model

/**
 * The intensity level of a workout session template.
 *
 * Maps to the user's fixed weekly plan:
 *   HEAVY   → Tuesday   (compound lifts, high load)
 *   LIGHT   → Thursday  (lower load, recovery-focused)
 *   MODERATE → Saturday (mid-week volume)
 */
enum class Intensity {
    HEAVY,
    LIGHT,
    MODERATE
}
