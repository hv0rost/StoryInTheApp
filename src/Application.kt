package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import java.io.PrintWriter
import java.net.URI
import java.net.URL
import java.time.Duration
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dm = 34.toChar()
    //dbInnit("a1640Z89")
    Database.connect(hikari())
    val query = StoriesController()
    install(CORS)
    {
        //exposeHeader("key")
        method(HttpMethod.Options)
        header(HttpHeaders.XForwardedProto)
        anyHost()
        host("my-host")
        // host("my-host:80")
        // host("my-host", subDomains = listOf("www"))
        // host("my-host", schemes = listOf("http", "https"))
        allowCredentials = true
        allowNonSimpleContentTypes = true
        maxAge = Duration.ofDays(1)
    }
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
        get("/screen-test") {
            val graphQLRequest = call.receive<GraphQLRequest>()
            call.respond(query.screenController.execute(graphQLRequest.query!!))
        }
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
    props.setProperty("dataSource.user", "postgres")
    props.setProperty("dataSource.password", password)
    props.setProperty("dataSource.databaseName", "SebbiaStories")
    props["dataSource.logWriter"] = PrintWriter(System.out)

    val config = HikariConfig(props)
    config.schema = "story"
    val ds = HikariDataSource(config)
    Database.connect(ds)
}

private fun hikari(): HikariDataSource {
    val props = Properties()
    props["dataSource.logWriter"] = PrintWriter(System.out)
    val config = HikariConfig(props)
    config.driverClassName = System.getenv("JDBC_DRIVER")
    val dbUri = URI(System.getenv("DATABASE_URL"))
    val username = dbUri.userInfo.split(":").toTypedArray()[0]
    val password = dbUri.userInfo.split(":").toTypedArray()[1]
    val dbUrl = "jdbc:postgresql://" + dbUri.host + ':' + dbUri.port + dbUri.path

    config.jdbcUrl = /*System.getenv("DATABASE_URL_KTOR")*/ dbUrl
    config.username = System.getenv("USERNAME")
    config.password = System.getenv("PASSWORD")
    config.schema = "story"
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()

    return HikariDataSource(config)
}



