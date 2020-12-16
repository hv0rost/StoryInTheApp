package com.example

import com.apurebase.kgraphql.KGraphQL
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class StoriesController {

    private val transaction = InsertController()
    val tokenController = KGraphQL.schema {
        query("getTokenFromEmail") {
            resolver { email: String ->
                transaction {
                    Client.slice(Client.email).select { Client.email eq email }.map { Client.getEmail(it) }
                }
            }
        }
        mutation("postToken"){
            resolver { token : String , email : String ->
                transaction {
                    Client.update({ Client.email eq email }) {
                        it[Client.token] = token
                    }
                }
            }
        }
    }

    val storyController = KGraphQL.schema {
        query("getStory") {
            resolver { token: String ->
                transaction {
                    Story.leftJoin(Client).select { Client.token eq token }.map { Story.storyToMap(it) }
                }
            }
        }
        mutation("postStory") {
            resolver { image: String, token: String ->
                transaction.insertStory(image, transaction.tokenDefine(token))
            }
        }
        mutation("deleteStory"){
            resolver{ idStory : Int ->
                transaction {
                    Story.deleteWhere { Story.idStory eq idStory }
                }
            }
        }
        mutation("updateStory") {
            resolver { idStory: Int, image: String ->
                transaction {
                    Story.update({ Story.idStory eq idStory }) {
                        it[Story.image] = image
                    }
                }
            }
        }
    }

    val screenController = KGraphQL.schema {
        query("getScreen") {
            resolver { idStory : Int ->
                transaction {
                    Screen.select{Screen.idStory eq idStory}.map { Screen.screenToMap(it) }
                }
            }
        }
        mutation("postScreen") {
            resolver { name: String,
                       urlFile: String,
                       bgColour: String,
                       delay: Int,
                       dateStart: String,
                       dateEnd: String,
                       number: Int,
                       idStory: Int ->
                transaction.insertScreen(name, urlFile, bgColour, delay, dateStart, dateEnd, number, idStory)
            }
        }
        mutation("updateScreen") {
            resolver { idStory : Int, number : Int, name : String, urlFile : String,
                       bgColour : String, delay : Int, dateStart : String, dateEnd : String  ->
                transaction {
                    Story.update({ (Screen.idStory eq idStory) and (Screen.number eq number) }) {
                          it[Screen.name] = name
                          it[Screen.urlFile] = urlFile
                          it[Screen.bgColour] = bgColour
                          it[Screen.delay] = delay
                          it[Screen.dateStart] = DateTime(dateStart)
                          it[Screen.dateEnd] = DateTime(dateEnd)
                          it[Screen.number] = number
                    }
                }
            }
        }
        mutation("deleteScreen"){
            resolver{ idStory : Int, number : Int ->
                transaction {
                    Screen.deleteWhere { (Screen.idStory eq idStory) and (Screen.number eq number)  }
                    //todo make dynamic number update when client is deleting row
                }
            }
        }
    }

    val clientController = KGraphQL.schema {
        query("getClient") {
            resolver { token : String ->
                transaction {
                    Client.select {Client.token eq token } .map { Client.clientToMap(it) }
                }
            }
        }
        mutation("postClient") {
            resolver { name: String, organization: String, email: String, phone: String ->
                transaction.insertClient(name, organization, email, phone)
            }
        }
        mutation("updateClient")
        {
            resolver{token : String, name: String, organization: String, email: String, phone: String ->
                transaction {
                    Client.update ({Client.token eq token }) {
                        it[Client.name] = name
                        it[Client.organization] = organization
                        it[Client.email] = email
                        it[Client.phone] = phone
                    }
                }
            }
        }
    }

    val buttonController = KGraphQL.schema {
        query("getButton") {
            resolver { idScreen : Int ->
                transaction {
                    Button.select{ Button.idButton eq idScreen }.map { Button.buttonToMap(it) }
                }
            }
        }
        mutation("postButton") {
            resolver { idStory: Int,
                       number : Int,
                       colourButton: String,
                       colourText: String,
                       text: String,
                       url: String ->
                transaction.insertButton(transaction.screeDefine(idStory,number),colourButton, colourText, text, url)
            }
        }
        mutation("deleteButton"){
            resolver{ idStory : Int, number : Int ->
                transaction.deleteButton(transaction.screeDefine(idStory, number))
            }
        }
        mutation("updateButton"){
            resolver{ idStory : Int, number : Int, colourButton: String,
                      colourText: String, text: String, url: String  ->
                transaction.updateButton(transaction.screeDefine(idStory, number),colourButton, colourText, text, url)
            }
        }
    }

    val textController = KGraphQL.schema {
        query("getText") {
            resolver { idScreen : Int  ->
                transaction {
                    Text.select { Text.idText eq idScreen }.map { Text.textToMap(it) }
                }
            }
        }
        mutation("postText") {
            resolver { idStory: Int, number : Int, title: String, subtitle: String,
                       colour: String, bold: Boolean, italic: Boolean, alVert: String ->
                transaction.insertText(transaction.screeDefine(idStory,number),title, subtitle, colour, bold, italic, alVert)
            }
        }
        mutation("deleteText"){
            resolver{ idStory : Int, number : Int ->
                transaction.deleteText(transaction.screeDefine(idStory, number))
            }
        }
        mutation("updateText"){
            resolver{ idStory : Int, number : Int, title: String, subtitle: String,
                      colour: String, bold: Boolean, italic: Boolean, alVert: String  ->
                transaction.updateText(transaction.screeDefine(idStory, number),title, subtitle, colour, bold, italic,alVert)
            }
        }
    }

}
