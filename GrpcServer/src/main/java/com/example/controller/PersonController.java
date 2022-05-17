package com.example.controller;

import com.example.entity.Person;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class PersonController {


    @MessageMapping("people.findById")
    Mono<Person> getOne(Person person){ //1

        return null;
    }

    @MessageMapping("people.findAll")
    Flux<Person> all(Person person){ //2
        return null;
    }

}

