package com.jcu.brainbuddy.data.repository

import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.domain.Question
import kotlin.random.Random

/** Built-in, age-appropriate questions used when the internet quiz service is unavailable. */
object FallbackQuestions {

    private fun q(text: String, correct: String, vararg wrong: String, random: Random = Random.Default): Question {
        val options = (wrong.toList() + correct).shuffled(random)
        return Question(text, options, options.indexOf(correct))
    }

    private val math = listOf(
        q("What is 7 + 5?", "12", "11", "13", "10"),
        q("What is 9 x 3?", "27", "24", "36", "21"),
        q("What is 20 - 8?", "12", "14", "11", "10"),
        q("How many sides does a triangle have?", "3", "4", "5", "6"),
        q("What is 36 divided by 6?", "6", "7", "5", "8"),
        q("What is half of 50?", "25", "20", "30", "15"),
        q("Which number is even?", "18", "15", "21", "13"),
        q("How many minutes are in one hour?", "60", "30", "100", "24"),
        q("What is 100 - 1?", "99", "101", "89", "90"),
        q("What is 8 x 8?", "64", "56", "72", "48")
    )

    private val science = listOf(
        q("Which planet do we live on?", "Earth", "Mars", "Venus", "Jupiter"),
        q("What do plants need to make their food?", "Sunlight", "Plastic", "Sand", "Music"),
        q("What is water called when it freezes?", "Ice", "Steam", "Cloud", "Rain"),
        q("Which star lights up our day?", "The Sun", "The Moon", "Mars", "Pluto"),
        q("How many legs does an insect have?", "6", "4", "8", "10"),
        q("What gas do we breathe in to stay alive?", "Oxygen", "Helium", "Smoke", "Steam"),
        q("Which part of the body pumps blood?", "Heart", "Stomach", "Lungs", "Brain"),
        q("What force pulls things down to the ground?", "Gravity", "Magnetism", "Wind", "Light"),
        q("Which planet is known as the Red Planet?", "Mars", "Saturn", "Neptune", "Earth"),
        q("What do bees make?", "Honey", "Milk", "Silk", "Bread")
    )

    private val animals = listOf(
        q("Which animal says 'moo'?", "Cow", "Dog", "Cat", "Duck"),
        q("What is a baby dog called?", "Puppy", "Kitten", "Calf", "Cub"),
        q("Which animal is the tallest in the world?", "Giraffe", "Elephant", "Horse", "Camel"),
        q("Which bird cannot fly?", "Penguin", "Eagle", "Parrot", "Sparrow"),
        q("What do caterpillars turn into?", "Butterflies", "Beetles", "Birds", "Bees"),
        q("Which animal is known as the King of the Jungle?", "Lion", "Tiger", "Bear", "Wolf"),
        q("How many legs does a spider have?", "8", "6", "4", "10"),
        q("Which animal has a long trunk?", "Elephant", "Rhino", "Hippo", "Zebra"),
        q("Where do fish live?", "Water", "Trees", "Caves", "Sand dunes"),
        q("Which animal hops and carries its baby in a pouch?", "Kangaroo", "Koala", "Wombat", "Emu")
    )

    private val geography = listOf(
        q("Which is the largest ocean on Earth?", "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean"),
        q("What is the capital city of Australia?", "Canberra", "Sydney", "Melbourne", "Perth"),
        q("On which continent is Egypt?", "Africa", "Asia", "Europe", "South America"),
        q("Which is the longest river in Australia?", "Murray River", "Amazon River", "Thames", "Nile"),
        q("How many continents are there?", "7", "5", "6", "8"),
        q("Which country is shaped like a boot?", "Italy", "Spain", "France", "Greece"),
        q("What is the capital of Japan?", "Tokyo", "Seoul", "Beijing", "Bangkok"),
        q("Which desert is the largest hot desert in the world?", "Sahara", "Gobi", "Kalahari", "Simpson"),
        q("Which direction does the sun rise from?", "East", "West", "North", "South"),
        q("Which is the tallest mountain on Earth?", "Mount Everest", "Uluru", "Mount Fuji", "Kilimanjaro")
    )

    private val byCategory: Map<QuizCategory, List<Question>> = mapOf(
        QuizCategory.MATH to math,
        QuizCategory.SCIENCE to science,
        QuizCategory.ANIMALS to animals,
        QuizCategory.GEOGRAPHY to geography
    )

    fun forCategory(category: QuizCategory, count: Int, random: Random = Random.Default): List<Question> =
        byCategory.getValue(category).shuffled(random).take(count)
}
