package com.fitnesstracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fitnesstracker.data.local.dao.WorkoutDao
import com.fitnesstracker.data.local.entity.WorkoutEntity
import com.fitnesstracker.data.local.entity.ExerciseEntity
import com.fitnesstracker.data.local.entity.ExerciseSetEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        ExerciseSetEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
