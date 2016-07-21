package com.webservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    @RequestMapping("/booking")
    public String booking(@RequestParam(value="name", defaultValue="") String name) {
     return "hi";
    }
}
