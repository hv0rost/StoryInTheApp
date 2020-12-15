package com.example

data class GraphQLRequest(val query: String? = null)

data class ClientData(
    val idClient: Int? = null,
    val name: String? = null,
    val organization: String? = null,
    val email: String? = null,
    val phone: String? = null
)

data class ScreenData(
    val idScreen: Int? = null,
    val name: String? = null,
    val urlFile: String? = null,
    val bgColour: String? = null,
    val delay: Int? = null,
    val dateStart: String? = null,
    val dateEnd: String? = null,
    val number: Int? = null,
    val idStory: Int? = null
)

data class StoryData(
    val idStory: Int? = null,
    val image: String? = null,
    val idClient: Int? = null
)

data class TextData(
    val idText: Int? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val colour: String? = null,
    val bold: Boolean? = null,
    val italic: Boolean? = null,
    val alVert: String? = null
)

data class ButtonData(
    val idButton: Int? = null,
    val colourButton: String? = null,
    val colourText: String? = null,
    val text: String? = null,
    val url: String? = null
)