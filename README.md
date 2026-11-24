# BrainBuddy

A quiz app for primary-school learners (about 8-10 years old), built for CP3406 Assessment 3 (Education App).
Learners pick a topic (Maths, Science, Animals, Geography), answer short multiple-choice quizzes, and see their progress over time.

## Screens
| Screen | Purpose |
|---|---|
| Home (landing) | Greeting, one-tap "Start quiz", topic shortcuts, progress summary |
| Quiz (activity) | Question, four options, instant feedback, progress bar |
| Result | Score and encouraging message, play again or go home |
| Progress (statistics) | Totals, accuracy, best score, days played, per-topic bars, recent quizzes |
| Settings | Topic, challenge mode (online questions), difficulty, questions per quiz, larger text, vibration, privacy and "Delete my progress" |

## Technical features
- **Kotlin + Jetpack Compose + Material 3**; every screen is a stateful `Screen` wrapping a stateless `Content` composable so it can be UI-tested.
- **Navigation Compose**: bottom bar for top-level screens, `quiz/{category}` and `result/{category}/{correct}/{total}` routes with arguments; the quiz is popped off the back stack when it finishes.
- **Architecture**: ViewModel + Repository pattern + Hilt dependency injection; pure-Kotlin domain layer (`QuizProgress`, `StatsCalculator`, `Encouragement`).
- **Networking**: in optional *Challenge mode* Retrofit + Moshi fetch questions from the [Open Trivia Database](https://opentdb.com). If the device is offline, the request fails or the API rate-limits, `QuestionRepositoryImpl` falls back to the built-in question bank and the quiz screen shows a notice. Challenge mode is off by default because Open Trivia questions are not written for children (testing showed a Millennium Prize question under "Maths - Easy").
- **Persistence**: Room stores every finished quiz (statistics); DataStore (Preferences) stores settings.
- **Testing**: 48 JUnit/MockK unit tests (model, repository, mapping, ViewModels) and 17 Compose UI tests (`androidTest`).

## Ethical design (from Assessment 2)
- **Privacy / data minimisation**: no account, name, email or location. Scores stay on the device; `allowBackup` is off; API requests carry no personal data.
- **User autonomy**: "Delete my progress" erases all stored results after confirmation.
- **No dark patterns**: no ads, leaderboards, streak penalties or countdown timers. Feedback is always encouraging.
- **Accessibility**: larger-text option, answer feedback uses icon + text + colour (never colour alone), 56 dp minimum option height, content descriptions and state descriptions for screen readers.
- **Safe, age-appropriate content**: the default question bank is written for the target age; online questions are opt-in and clearly labelled as possibly harder.

## Running
Open in Android Studio (JDK 17+), or:
```
./gradlew testDebugUnitTest          # unit tests
./gradlew connectedDebugAndroidTest  # UI tests (emulator/device required)
./gradlew installDebug
```
