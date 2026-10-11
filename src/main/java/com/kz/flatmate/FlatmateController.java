package com.kz.flatmate;

import com.kz.flatmate.model.User;
import com.kz.flatmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.kz.flatmate.model.ChatMessage;
import com.kz.flatmate.repository.ChatMessageRepository;

@Controller
public class FlatmateController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
private ChatMessageRepository chatMessageRepository;

    @GetMapping("/")
    public String showIndex() { 
        return "index"; 
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        java.util.List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            model.addAttribute("user", users.get(0));
        } else {
            User defaultUser = new User();
            defaultUser.setCleanliness("normal");
            defaultUser.setSleepMode("normal");
            defaultUser.setNoiseLevel("normal");
            defaultUser.setGuests("sometimes");
            defaultUser.setSmoking("no");
            defaultUser.setPets("no");
            defaultUser.setSociability("balanced");
            defaultUser.setSearchStatus("active");
            model.addAttribute("user", defaultUser);
        }
        return "profile";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute("user") User user) {
        try {
            userRepository.save(user);
            System.out.println("=============================================");
            System.out.println("=== АНКЕТА СТУДЕНТА ЗАПИСАНА В POSTGRESQL ===");
            System.out.println("=============================================");
            System.out.println("Имя: " + user.getName());
            System.out.println("Возраст: " + user.getAge());
            System.out.println("Университет: " + user.getUniversity());
            System.out.println("Критерии (Сон): " + user.getSleepMode());
            System.out.println("=============================================");
            return "redirect:/matches";
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
    }

    @GetMapping("/matches")
    public String showMatches(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "matches";
    }

    
    @GetMapping("/profile/view/{id}")
    public String viewProfile(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/matches";
        }

        model.addAttribute("profileUser", user);
        return "profile-view";
    }

   

@GetMapping("/chat/{id}")
public String showChat(@PathVariable Long id, Model model) {
    User user = userRepository.findById(id).orElse(null);

    if (user == null) {
        return "redirect:/matches";
    }

    model.addAttribute("chatUser", user);

    java.util.List<ChatMessage> messages =
            chatMessageRepository
                    .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtAsc(
                            user.getId(), user.getId(),
                            user.getId(), user.getId()
                    );

    model.addAttribute("messages", messages);
    return "chat";
}

@PostMapping("/chat/{id}/send")
public String sendMessage(
        @PathVariable Long id,
        @RequestParam String content) {

    if (content == null || content.trim().isEmpty()) {
        return "redirect:/chat/" + id;
    }

    User receiver = userRepository.findById(id).orElse(null);
    User sender = userRepository.findAll().stream()
            .findFirst()
            .orElse(null);

    if (receiver == null || sender == null
            || sender.getId().equals(receiver.getId())) {
        return "redirect:/matches";
    }

    ChatMessage message = new ChatMessage();
    message.setSenderId(sender.getId());
    message.setReceiverId(receiver.getId());
    message.setContent(content.trim());
    message.setSentAt(java.time.LocalDateTime.now());

    chatMessageRepository.save(message);

    return "redirect:/chat/" + id;
}
}
