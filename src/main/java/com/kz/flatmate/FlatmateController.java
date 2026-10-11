package com.kz.flatmate;

import com.kz.flatmate.model.User;
import com.kz.flatmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FlatmateController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        model.addAttribute("user", new User());
        return "profile";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute("user") User user) {
        userRepository.save(user);
        return "redirect:/matches";
    }

    @GetMapping("/matches")
    public String showMatches(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "matches";
    }

    @GetMapping("/chat")
    public String showChat() {
        return "chat";
    }
}
