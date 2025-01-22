package com.store.storeapp.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/errorPage")
    public String errorPage(){
        return "error/404";
    }
}
