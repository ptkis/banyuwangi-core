package com.katalisindonesia.banyuwangi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloController {
    @GetMapping("/world")
    fun world() = "hello world " + System.currentTimeMillis()
}
