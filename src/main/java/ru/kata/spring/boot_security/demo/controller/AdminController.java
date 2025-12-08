package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/admin_home";
    }

    @GetMapping("/add")
    public String openAddForm(Model model, User user) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/new";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") @Valid User user,
                             @RequestParam("selectedRoles") List<String> selectedRoles,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/new";
        }

        // получение ролей из бд
        List<Role> roles = roleService.getRolesByNames(selectedRoles);
        // установка ролей для пользователя
        user.setRoles(roles);

        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") @Valid User user,
                             @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/edit";
        }
        // проверка на наличие выбранных ролей
        if (selectedRoles != null && !selectedRoles.isEmpty()) {
            List<Role> roles = roleService.getRolesByNames(selectedRoles);
            user.setRoles(roles);
        } else {
            // если роли не выбраны, устанавливаем пустой список
            user.setRoles(new ArrayList<>());
        }
        userService.updateUserById(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String openEditForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("user", user);
            // передача всех ролей для выбора
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/edit";
        }
        return "not-found";
    }

    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user_home";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
}

