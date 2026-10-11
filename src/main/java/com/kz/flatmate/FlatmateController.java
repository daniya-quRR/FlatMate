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

       // сохранение анкеты из js через fetch
    @PostMapping("/api/profile/save")
    @ResponseBody
    public String saveFromJs(@RequestBody User user) {
        try {
            // Сохраняем все поля в PostgreSQL
            userRepository.save(user);
            
            // Выводим вообще всю анкету в консоль для проверки
            System.out.println("=============================================");
            System.out.println("=== НОВАЯ АНКЕТА ЗАПИСАНА В POSTGRESQL ===");
            System.out.println("=============================================");
            System.out.println("Имя: " + user.getName());
            System.out.println("Возраст: " + user.getAge());
            System.out.println("Университет: " + user.getUniversity());
            System.out.println("Курс: " + user.getCourse());
            System.out.println("О себе: " + user.getAbout());
            System.out.println("---------------------------------------------");
            System.out.println("Критерии сожителя:");
            System.out.println("🧹 Чистота: " + user.getCleanliness());
            System.out.println("🌙 Режим сна: " + user.getSleepMode());
            System.out.println("🔊 Уровень шума: " + user.getNoiseLevel());
            System.out.println("👥 Гости: " + user.getGuests());
            System.out.println("🚭 Курение: " + user.getSmoking());
            System.out.println("🐾 Животные: " + user.getPets());
            System.out.println("💬 Общительность: " + user.getSociability());
            System.out.println("---------------------------------------------");
            System.out.println("Статус поиска: " + user.getSearchStatus());
            System.out.println("=============================================");
            
            return "ok";
        } catch (Exception e) {
            return "error";
        }
    }
}

