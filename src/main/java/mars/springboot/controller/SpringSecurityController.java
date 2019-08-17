package mars.springboot.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.AllArgsConstructor;
import mars.springboot.orm.UserDaoService;
import mars.springboot.orm.model.User;

//@RestController
@Controller
@AllArgsConstructor
public class SpringSecurityController {

//	@Autowired
//    private UserService userService;
    
    private final UserDaoService userDaoService;

//    @RequestMapping(value="/home", method = RequestMethod.GET)
//    public String home(Model model) {
//        return "home";
//    }
    
//    @RequestMapping(value="/login", method = RequestMethod.GET)
//    public String login(Model model) {
//        return "login";
//    }

    @GetMapping({"/", "/home"})
    public String root(){
        return "home";
    }

    @GetMapping({"/login"})
    public String login(){
        return "login";
    }

    @GetMapping({"/iplogin"})
    public String iplogin(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(User userDO){
        // 此处省略校验逻辑
        userDaoService.insert(userDO);
        return "redirect:register?success";
    }

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal Principal principal, Model model){
        model.addAttribute("username", principal.getName());
        return "user/user";
    }

    @GetMapping("/super")
    public String superuser(@AuthenticationPrincipal Principal principal, Model model){
        model.addAttribute("username", principal.getName());
        return "user/superuser";
    }

}
