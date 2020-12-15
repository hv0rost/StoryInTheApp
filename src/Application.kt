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

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port) {
        val dm = 34.toChar()
        dbInnit("a1640Z89")
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
    }.start(wait = true)
}

fun dbInnit(password: String){
    val props = Properties()
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.user", "postgres")
    props.setProperty("dataSource.password", password)
    props.setProperty("dataSource.databaseName", "SebbiaStories")
    props["dataSource.logWriter"] = PrintWriter(System.out)

    val config = HikariConfig(props)
    config.schema = "SebbiaStories"
    val ds = HikariDataSource(config)
    Database.connect(ds)
}


