package nz.ac.canterbury.seng303.lab2.models

class FlashCard(
    val id: Int,
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
    ): Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }
}

