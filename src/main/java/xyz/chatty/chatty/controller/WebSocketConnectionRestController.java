package xyz.chatty.chatty.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.chatty.chatty.util.ActiveUserManager;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@RestController
public class WebSocketConnectionRestController {

    private final ActiveUserManager activeUserManager;

    public WebSocketConnectionRestController(ActiveUserManager activeUserManager) {
        this.activeUserManager = activeUserManager;
    }

    @PostMapping("/api/users/connect")
    public String userConnect(HttpServletRequest request,
                              @ModelAttribute("username") String username) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("Remote_Addr");
            if (StringUtils.isEmpty(remoteAddr)) {
                remoteAddr = request.getHeader("X-FORWARDED-FOR");
                if (remoteAddr == null || "".equals(remoteAddr)) {
                    remoteAddr = request.getRemoteAddr();
                }
            }
        }

        activeUserManager.add(username, remoteAddr);
        return  remoteAddr;
    }

    @PostMapping("/api/users/disconnect")
    public String userDisconnect(@ModelAttribute("username") String username) {
        activeUserManager.remove(username);
        return "disconnected";
    }

    @GetMapping("/api/users/active-except/{username}")
    public Set<String> getActiveUsersExceptCurrentUser(@PathVariable String username) {
        return activeUserManager.getActiveUsersExceptCurrentUser(username);
    }
}
