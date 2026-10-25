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

    // Страница входа/регистрации (девочки сделают её форму)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // API для авторизации по email и password
    @PostMapping("/api/auth/login")
    @ResponseBody
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("currentUser", user); // Запоминаем текущего юзера в сессию
            return "ok";
        }
        return "error";
    }

    // API для регистрации нового студента
    @PostMapping("/api/auth/register")
    @ResponseBody
    public String register(@RequestParam String email, @RequestParam String password, @RequestParam String name) {
        if (userRepository.findByEmail(email) != null) {
            return "exists";
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setName(name);
        
        // Базовые дефолты анкеты
        newUser.setCleanliness("normal");
        newUser.setSleepMode("normal");
        newUser.setNoiseLevel("normal");
        newUser.setGuests("sometimes");
        newUser.setSmoking("no");
        newUser.setPets("no");
        newUser.setSociability("balanced");
        newUser.setSearchStatus("active");
        
        userRepository.save(newUser);
        return "ok";
    }

    // Выгружает анкету СТРОГО текущего залогиненного пользователя
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login"; // Если не авторизован — на вход
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
            // Защита: принудительно ставим ID и доступы текущей сессии
            user.setId(currentUser.getId());
            user.setEmail(currentUser.getEmail());
            user.setPassword(currentUser.getPassword());

            userRepository.save(user);

            System.out.println("=============================================");
            System.out.println("=== АНКЕТА СТУДЕНТА ЗАПИСАНА В POSTGRESQL ===");
            System.out.println("=============================================");
            System.out.println("Имя: " + user.getName());
            System.out.println("=============================================");

            return "redirect:/matches";
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
    }

    @GetMapping("/matches")
    public String showMatches(Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }
        model.addAttribute("users", userRepository.findAll());
        return "matches";
    }

    // Профиль соседа с анализом совместимости на основе СЕССИИ
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

    // Чат вытаскивает диалог конкретно МЕЖДУ ТОБОЙ (из сессии) и собеседником
    @GetMapping("/chat/{id}")
    public String showChat(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        User chatWithUser = userRepository.findById(id).orElse(null);
        if (chatWithUser == null) {
            return "redirect:/matches";
        }

        model.addAttribute("chatUser", chatWithUser);

        // Ищем сообщения только между текущим юзером и собеседником в обе стороны
        java.util.List<ChatMessage> messages = chatMessageRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtAsc(
                        currentUser.getId(), chatWithUser.getId(),
                        chatWithUser.getId(), currentUser.getId()
                );

        model.addAttribute("messages", messages);
        return "chat";
    }

    // Отправка сообщения от имени текущего авторизованного пользователя
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
        message.setSenderId(currentUser.getId()); // Отправитель — строго из сессии
        message.setReceiverId(receiver.getId());
        message.setContent(content.trim());
        message.setSentAt(java.time.LocalDateTime.now());

        chatMessageRepository.save(message);

        return "redirect:/chat/" + id;
    }

    // Выход из аккаунта
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Полностью очищаем сессию
        return "redirect:/";
    }
}
