package com.kz.flatmate;

import com.kz.flatmate.model.User;
import com.kz.flatmate.repository.UserRepository;
import com.kz.flatmate.model.ChatMessage;
import com.kz.flatmate.repository.ChatMessageRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FlatmateController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CompatibilityService compatibilityService;

    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    // Страница входа/регистрации
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // API для авторизации по email и password (Обычный текст без хэширования)
    @PostMapping("/api/auth/login")
    @ResponseBody
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByEmail(email);
        
        // Сравниваем пароли как обычные строки текста
        if (user != null && user.getPassword().equals(password)) {
            user.setLastLoginAt(java.time.LocalDateTime.now());
            userRepository.save(user);
            
            session.setAttribute("currentUser", user); 
            return "ok";
        }
        return "error";
    }

    // API для регистрации нового студента (Обычный текст без хэширования)
    @PostMapping("/api/auth/register")
    @ResponseBody
    public String register(@RequestParam String email, @RequestParam String password, @RequestParam String name) {
        if (userRepository.findByEmail(email) != null) {
            return "exists";
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password); // Сохраняем пароль обычным текстом, как ввёл пользователь
        newUser.setName(name);
        
        newUser.setCleanliness("normal");
        newUser.setSleepMode("normal");
        newUser.setNoiseLevel("normal");
        newUser.setGuests("sometimes");
        newUser.setSmoking("no");
        newUser.setPets("no");
        newUser.setSociability("balanced");
        newUser.setSearchStatus("active");
        newUser.setGender("female");          
        newUser.setPreferredGender("any"); 
        
        newUser.setCreatedAt(java.time.LocalDateTime.now());
        
        userRepository.save(newUser);
        return "ok";
    }

    // Выгружает анкету СТРОГО текущего залогиненного пользователя
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login"; 
        }

        User userFromDb = userRepository.findById(currentUser.getId()).orElse(currentUser);
        model.addAttribute("user", userFromDb);
        return "profile";
    }

    // Сохраняет/обновляет анкету текущего пользователя
    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute("user") User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        try {
            user.setId(currentUser.getId());
            user.setEmail(currentUser.getEmail());
            user.setPassword(currentUser.getPassword()); // Передаем обычный пароль без изменений
            
            if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
                user.setPhoto(currentUser.getPhoto());
            }
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(currentUser.getCreatedAt());
            }
            if (user.getGender() == null) {
                user.setGender(currentUser.getGender());
            }
            if (user.getPreferredGender() == null) {
                user.setPreferredGender(currentUser.getPreferredGender());
            }

            userRepository.save(user);
            return "redirect:/matches";
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
    }
    
    // Лента матчей с обновлением данных сессии
    @GetMapping("/matches")
    public String showMatches(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        User freshUser = userRepository.findById(currentUser.getId()).orElse(currentUser);
        session.setAttribute("currentUser", freshUser);
        model.addAttribute("currentUser", freshUser);
        
        String myPref = freshUser.getPreferredGender() != null ? freshUser.getPreferredGender() : "any";
        String myGender = freshUser.getGender() != null ? freshUser.getGender() : "female";
        
        java.util.List<User> filteredUsers = userRepository.findAll().stream()
            .filter(u -> !u.getId().equals(freshUser.getId())) 
            .filter(u -> {
                String userGender = u.getGender() != null ? u.getGender() : "female";
                String userPref = u.getPreferredGender() != null ? u.getPreferredGender() : "any";

                if (!"any".equals(myPref)) {
                    if (!myPref.equals(userGender)) return false;
                }
                if (!"any".equals(userPref)) {
                    if (!userPref.equals(myGender)) return false;
                }
                return true;
            })
            .toList();
            
        model.addAttribute("users", filteredUsers);

        // Панель диалогов слева (Inbox)
        java.util.List<ChatMessage> myMessages = chatMessageRepository.findAll().stream()
            .filter(m -> m.getSenderId().equals(freshUser.getId()) || m.getReceiverId().equals(freshUser.getId()))
            .toList();
        java.util.Set<Long> dialogUserIds = new java.util.HashSet<>();
        for (ChatMessage m : myMessages) {
            dialogUserIds.add(m.getSenderId().equals(freshUser.getId()) ? m.getReceiverId() : m.getSenderId());
        }
        model.addAttribute("dialogUsers", userRepository.findAllById(dialogUserIds));

        return "matches";
    }

    // Профиль соседа с анализом совместимости
    @GetMapping("/profile/view/{id}")
    public String viewProfile(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        User roommate = userRepository.findById(id).orElse(null);
        if (roommate == null) {
            return "redirect:/matches";
        }

        model.addAttribute("profileUser", roommate);

        if (!currentUser.getId().equals(roommate.getId())) {
            CompatibilityService.Result result = compatibilityService.compare(currentUser, roommate);
            model.addAttribute("compatibility", result);
            model.addAttribute("currentUser", currentUser);
        }

        return "profile-view";
    }

    // Открытие чата (ПОЛНЫЙ ИСПРАВЛЕННЫЙ МЕТОД)
    @GetMapping("/chat/{id}")
    public String showChat(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        User freshUser = userRepository.findById(currentUser.getId()).orElse(currentUser);
        session.setAttribute("currentUser", freshUser);

        User chatWithUser = userRepository.findById(id).orElse(null);
        if (chatWithUser == null) {
            return "redirect:/matches";
        }

        model.addAttribute("chatUser", chatWithUser);

        java.util.List<ChatMessage> messages = chatMessageRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtAsc(
                        freshUser.getId(), chatWithUser.getId(),
                        chatWithUser.getId(), freshUser.getId()
                );

        model.addAttribute("messages", messages);
        return "chat";
    }

    // Отправка сообщения в чат
    @PostMapping("/chat/{id}/send")
    public String sendMessage(@PathVariable Long id, @RequestParam String content, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {
            return "redirect:/chat/" + id;
        }

        User receiver = userRepository.findById(id).orElse(null);
        if (receiver == null || currentUser.getId().equals(receiver.getId())) {
            return "redirect:/matches";
        }

        ChatMessage message = new ChatMessage();
        message.setSenderId(currentUser.getId());
        message.setReceiverId(receiver.getId());
        message.setContent(content.trim());
        message.setSentAt(java.time.LocalDateTime.now());

        chatMessageRepository.save(message);

        return "redirect:/chat/" + id;
    }

    // Выход из аккаунта
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }
}
