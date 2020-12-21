package com.example

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class InsertController {
    fun insertStory(image: String, idClient: Int): Int {
        transaction {
            Story.insert {
                it[Story.image] = image
                it[Story.idClient] = idClient
            }
        }
        var storyId : Int? = null
        transaction{
            Story.select { Story.idStory eq wrapAsExpression(Story.slice(Story.idStory.max()).selectAll()) }.map{
                storyId = it[Story.idStory]
            }
        }
        return storyId!!
    }

    fun tokenDefine(token : String) : Int{
        var client : Int? = null
        transaction {
            Client.select { Client.token eq token  }.map{
                client = it[Client.idClient]
            }
        }
            return client!!
    }

    fun insertScreen(name: String, urlFile: String, bgColour: String, delay: Int,
                     dateStart: String, dateEnd: String, number : Int, idStory: Int): Int {
        transaction {
            Screen.insert {
                it[Screen.name] = name
                it[Screen.urlFile] = urlFile
                it[Screen.bgColour] = bgColour!!
                it[Screen.delay] = delay
                it[Screen.dateStart] = DateTime(dateStart)
                it[Screen.dateEnd] = DateTime(dateEnd)
                it[Screen.number] = number
                it[Screen.idStory] = idStory
            }
        }
        return number
    }

    fun insertClient(name: String, organization: String,
                     email: String, phone: String): String {
        transaction {
            Client.insert {
                it[Client.name] = name
                it[Client.organization] = organization
                it[Client.email] = email
                it[Client.phone] = phone
            }
        }
        return email
    }

    fun screeDefine(idStory: Int, number: Int) : Int{
        var screenId : Int? = null
        transaction {
            Screen.select { (Screen.number eq number) and (Screen.idStory eq idStory)  }.map{
                screenId = it[Screen.idScreen]
            }
        }
        return screenId!!
    }

    fun insertText(idText : Int, title: String, subtitle: String, colour: String,
                   bold: Boolean, italic: Boolean, alVert: String): Int {
        transaction {
            Text.insert {
                it[Text.idText] = idText
                it[Text.title] = title
                it[Text.subtitle] = subtitle
                it[Text.colour] = colour
                it[Text.bold] = bold
                it[Text.italic] = italic
                it[Text.alVert] = alVert
            }
        }
        return idText
    }

    fun insertButton(idButton : Int ,colourButton: String, colourText: String,
                     text: String, url: String): Int {
        transaction {
            Button.insert {
                it[Button.idButton] = idButton
                it[Button.colourButton] = colourButton
                it[Button.colourText] = colourText
                it[Button.text] = text
                it[Button.url] = url
            }
        }
        return idButton
    }

     fun updateText(idText : Int, title: String, subtitle: String,
                           colour: String, bold: Boolean, italic: Boolean, alVert: String) : Int{
        transaction {
            Text.update ({Text.idText eq idText})
            {
                it[Text.title] = title
                it[Text.subtitle] = subtitle
                it[Text.colour] = colour
                it[Text.bold] = bold
                it[Text.italic] = italic
                it[Text.alVert] = alVert
            }
        }
        return 0
    }
     fun deleteText(idText : Int) : Int{
        transaction {
            Text.deleteWhere { Text.idText eq idText }
        }
        return 0
    }

     fun updateButton(idButton: Int, colourButton: String, colourText: String, text: String, url: String) : Int{
        transaction {
            Button.update ({Button.idButton eq idButton})
            {
                it[Button.colourButton] = colourButton
                it[Button.colourText] = colourText
                it[Button.text] = text
                it[Button.url] = url
            }
        }
        return 0
    }
     fun deleteButton(idText : Int) : Int{
        transaction {
            Button.deleteWhere { Button.idButton eq idText }
        }
        return 0
    }
    fun getToken(token : String, email : String) : String {
        transaction {
            Client.update({ Client.email eq email }) {
                it[Client.token] = token
            }
        }
        return token
    }
}
