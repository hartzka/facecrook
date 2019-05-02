package projekti;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DefaultController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("*")
    public String defaul_t(Model model) {
        return "redirect:/homepage";
    }
    
    @GetMapping("/error")
    public String error() {
        return "homepage";
    }

    @GetMapping("/homepage")
    public String home() {
        return "homepage";
    }

    @GetMapping("/index/{username}")
    public String index(Model model, @PathVariable String username) {
        model.addAttribute("owner", username);
        Account acc = accountRepository.findByUsername(username);
        model.addAttribute("name", acc.getName());
        Pageable pageable = PageRequest.of(0, 25, Sort.by("date").descending());
        List<Message> messages = messageRepository.findAllByUser(acc, pageable);
        model.addAttribute("messages", messages);
        HashMap<Message, List<Comment>> h = new HashMap<>();
        Pageable pageable2 = PageRequest.of(0, 10, Sort.by("date").descending());
        for(Message m : messages) {
            List<Comment> comments = commentRepository.findAllByMessage(m, pageable2);
            h.put(m, comments);
        }
        model.addAttribute("comments", h);
        model.addAttribute("account", acc);
        return "index";
    }
    
    @GetMapping("/index")
    public String redirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return "redirect:/index/" + username;
    }
    
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/homepage";
    }

    
}
