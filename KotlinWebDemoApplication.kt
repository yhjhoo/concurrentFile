package me.prince.kotlinwebdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@SpringBootApplication
class KotlinWebDemoApplication

fun main(args: Array<String>) {
    runApplication<KotlinWebDemoApplication>(*args)
}

@RestController
class WelcomeController {

    @GetMapping("/person")
    fun getPerson(): String {
        Thread.sleep(1000);

        return Files.readString(Path.of(URI.create("file:///Users/yhjhoo/github/kotlin-web-demo/text.txt")));

    }
}
