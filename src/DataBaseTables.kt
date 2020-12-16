package com.example


import com.example.Client.nullable
import com.example.Text.autoIncrement
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

val dm = 34.toChar()

object Client : Table() {
    val idClient = integer("idClient").autoIncrement()
    val name = varchar("name", 30)
    val organization = varchar("organization", 70)
    val email = varchar("email", 150)
    val phone = varchar("phone", 30)
    val token = varchar("token", 255)

    override val primaryKey = PrimaryKey(idClient, name = "client_pkey")

    fun clientToMap(row: ResultRow): ClientData =
        ClientData(
            idClient = row[idClient],
            name = row[name],
            organization = row[organization],
            email = row[email],
            phone = row[phone]
        )

    fun getEmail(row: ResultRow): ClientData =
            ClientData(
                    email = row[email]
            )
}

object Screen : Table("$dm" + "story" + "$dm" +".screen") {
    val idScreen = (integer("idScreen").autoIncrement() references Text.idText)
    val name = varchar("name", 50)
    val urlFile = varchar("urlFile", 255)
    val bgColour = varchar("bgColour", 30)
    val delay = integer("delay")
    val dateStart = datetime("dateStart")
    val dateEnd = datetime("dateEnd")
    val number = integer("number")
    val idStory = (integer("idStory") references Story.idStory)

    override val primaryKey = PrimaryKey(idScreen, name = "screen_pkey")

    fun screenToMap(row: ResultRow): ScreenData =
        ScreenData(
            idScreen = row[idScreen],
            name = row[name],
            urlFile = row[urlFile],
            bgColour = row[bgColour],
            delay = row[delay],
            dateStart = row[dateStart].toString("dd-MM-yyyy"),
            dateEnd = row[dateEnd].toString("dd-MM-yyyy"),
            number = row[number],
            idStory = row[idStory]
        )
}

object Story : Table() {
    val idStory = integer("idStory").autoIncrement()
    val image = varchar("image", 255)

    val idClient = (integer("idClient") references Client.idClient)


    override val primaryKey = PrimaryKey(idStory, name = "story_pkey")

    fun storyToMap(row: ResultRow): StoryData =
        StoryData(
            idStory = row[idStory],
            image = row[image],
            idClient = row[idClient],
        )
}

object Text : Table() {
    val idText = integer("idText")
    val title = varchar("title", 150)
    val subtitle = varchar("subtitle", 150)
    val colour = varchar("colour", 25)
    val bold = bool("bold")
    val italic = bool("italic")
    val alVert = varchar("alVert", 15)

    override val primaryKey = PrimaryKey(idText, name = "text_pkey")

    fun textToMap(row: ResultRow): TextData =
        TextData(
            idText = row[idText],
            title = row[title],
            subtitle = row[subtitle],
            colour = row[colour],
            bold = row[bold],
            italic = row[italic],
            alVert = row[alVert]
        )
}

object Button : Table() {
    val idButton = integer("idButton").autoIncrement()
    val colourButton = varchar("colourButton", 15)
    val colourText = varchar("colourText", 15)
    val text = varchar("text", 50)
    val url = varchar("url", 255)

    override val primaryKey = PrimaryKey(idButton, name = "button_pkey")

    fun buttonToMap(row: ResultRow): ButtonData =
        ButtonData(
            idButton = row[idButton],
            colourButton = row[colourButton],
            colourText = row[colourText],
            text = row[text],
            url = row[url]
        )
}