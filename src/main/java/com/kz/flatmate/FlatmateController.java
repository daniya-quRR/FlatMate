package com.kz.flatmate;

import com.kz.flatmate.model.User;
import com.kz.flatmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class FlatmateController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String showIndex() { 
        return "index"; 
    }

    @GetMapping("/profile")
    public String showProfile() { 
        return "profile"; 
    }

    @GetMapping("/matches")
    public String showMatches() { 
        return "matches"; 
    }

    @GetMapping("/chat")
    public String showChat() { 
        return "chat"; 
    }

    //сохранение анкеты из js через fetch
    @PostMapping("/api/profile/save")
    @ResponseBody
    public String saveFromJs(@RequestBody User user) {
        try {
            userRepository.save(user);
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }
}
