package stoner.tcache.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import stoner.tcache.bean.User;
import stoner.tcache.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("getUser")
    public User getUser(String name) {
        return userService.getUser(name);
    }

    @ResponseBody
    @RequestMapping("saveUser")
    public User saveUser(User user) {
        return userService.saveUser(user);
    }

    @ResponseBody
    @RequestMapping("cleanUser")
    public boolean cleanUser(String name) {
        return userService.cleanUser(name);
    }

    @ResponseBody
    @RequestMapping("getUserById")
    public User getUser(int id) {
        return userService.getUser(id);
    }
}
