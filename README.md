# fitness-tracker
Gym fitness Tracker

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ WorkoutList │  │ CreateWorkout│  │ WorkoutDetail│   │
│  │   Screen    │  │   Screen    │  │    Screen    │   │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘    │
│         │                │                │            │
│         ▼                ▼                ▼            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │  ViewModel  │  │  ViewModel  │  │  ViewModel  │    │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘    │
└─────────┼────────────────┼────────────────┼────────────┘
          │                │                │
          ▼                ▼                ▼
┌─────────────────────────────────────────────────────────┐
│                   Domain Layer                           │
│  ┌─────────────────────────────────────────────────┐    │
│  │                   Use Cases                      │    │
│  │  CreateWorkout  GetWorkouts  AddExercise  ...   │    │
│  └──────────────────────┬──────────────────────────┘    │
│                         │                                 │
│  ┌──────────────────────┴──────────────────────────┐    │
│  │              Repository Interfaces               │    │
│  │     WorkoutRepository    ExerciseRepository     │    │
│  └──────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                            │
│  ┌──────────────────────────────────────────────────┐   │
│  │            Repository Implementations            │   │
│  │         WorkoutRepositoryImpl                     │   │
│  └──────────────────────┬───────────────────────────┘   │
│                         │                                │
│  ┌──────────────────────┴───────────────────────────┐   │
│  │                     DAOs                          │   │
│  │    WorkoutDao    ExerciseDao    ExerciseSetDao   │   │
│  └──────────────────────┬───────────────────────────┘   │
│                         │                                │
│  ┌──────────────────────┴───────────────────────────┐   │
│  │                  Room Database                    │   │
│  └───────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────┐
│                  Dependency Injection                   │
│  ┌─────────────────────────────────────────────────┐    │
│  │  Hilt: DatabaseModule, RepositoryModule         │    │
│  │  FitnessTrackerApp (@HiltAndroidApp)            │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

---

## SOLID Principles Applied

| Principle | Where Applied |
|-----------|---------------|
| **S** - Single Responsibility | Use Cases handle one business operation each |
| **O** - Open/Closed | New use cases added without modifying existing code |
| **L** - Liskov Substitution | Repository implementations are interchangeable |
| **I** - Interface Segregation | Separate interfaces for Workout/Exercise repositories |
| **D** - Dependency Inversion | UI depends on abstractions (interfaces), not implementations |

---