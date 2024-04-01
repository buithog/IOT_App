package com.example.iotapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageControler {
    @GetMapping("/home")
    public String gethomepage(){
        return "index";
    }

    @GetMapping("/historysensor")
    public String gethistorysensor(){return "his_sensor";}

    @GetMapping("/historyaction")
    public String gethistoryaction(){return "his_action";}
    @GetMapping("/profile")
    public String getProfilePage(){
        return "profile";
    }
}
