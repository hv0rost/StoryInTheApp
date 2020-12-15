package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import java.io.PrintWriter
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false)  {
        val dm = 34.toChar()
        dbInnit("b463a485eee00c04196ac8c5d76eb7b93941848999e68e37f152d92a6e582c47")
        val query = StoriesController()
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(Authentication) {
            jwt {
                verifier(JwtConfig.verifier)
                realm = "hv0rost"
                validate {
                    TokenKey(it.payload.getClaim("email").asString())
                }
            }
        }

        routing {
            get("/client") {
                val graphQLRequest = call.receive<GraphQLRequest>()
                call.respond(query.clientController.execute(graphQLRequest.query!!))
            }
            get("/generate-token") {
                val graphQLRequest = call.receive<GraphQLRequest>()
                val emailKey = TokenKey(
                    (query.tokenController.execute(graphQLRequest.query!!)
                        .substringAfterLast(':').substring(1)).substringBefore('"')
                )
                val token = JwtConfig.generateToken(emailKey.email)
                call.respond(
                    query.tokenController.execute(
                        "mutation{postToken(token : ${dm + token + dm}, email : ${dm + emailKey.email + dm}) }"
                    )
                )
            }
            authenticate {
                get("/story") {
                    val graphQLRequest = call.receive<GraphQLRequest>()
                    call.respond(query.storyController.execute(graphQLRequest.query!!))
                }
                get("/screen") {
                    val graphQLRequest = call.receive<GraphQLRequest>()
                    call.respond(query.screenController.execute(graphQLRequest.query!!))
                }
                get("/text") {
                    val graphQLRequest = call.receive<GraphQLRequest>()
                    call.respond(query.textController.execute(graphQLRequest.query!!))
                }
                get("/button") {
                    val graphQLRequest = call.receive<GraphQLRequest>()
                    call.respond(query.buttonController.execute(graphQLRequest.query!!))
                }
            }
        }
    }


fun dbInnit(password: String){
    val props = Properties()
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.user", "lucmeoetruzkgd")
    props.setProperty("dataSource.password", password)
    props.setProperty("dataSource.databaseName", "dett2jacvmrrg1")
    props["dataSource.logWriter"] = PrintWriter(System.out)

    val config = HikariConfig(props)
    config.schema = "SebbiaStories"
    val ds = HikariDataSource(config)
    Database.connect(ds)
}



