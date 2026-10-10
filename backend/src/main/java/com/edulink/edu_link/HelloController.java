package com.edulink.edu_link;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @GetMapping("/api/hello")
    public String sayHello() {
        return "Hello World";
    }
}
