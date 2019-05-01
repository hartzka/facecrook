package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("messages/add/{username}")
    public String add(@PathVariable String username, @RequestParam String content) {
        if (!content.equals("")) {
            Account sender = accountRepository.findByUsername(username);
            Message message = new Message(content, 0, sender, new ArrayList<>(), LocalDateTime.now(), new ArrayList<>());
            messageRepository.save(message);
        }
        return "redirect:/index/" + username;

    }

    @DeleteMapping("/messages/delete/{id}")
    public String delete(@PathVariable Long id) {
        Message message = messageRepository.getOne(id);
        List<Comment> comments = message.getComments();
        for (Comment comment : comments) {
            commentRepository.delete(comment);
        }
        messageRepository.deleteById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return "redirect:/index/" + username;
    }

    @PostMapping("/messages/like/{id}")
    public String like(@PathVariable Long id) {
        Message message = messageRepository.getOne(id);
        message.setLikes(message.getLikes() + 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Account sender = accountRepository.findByUsername(currentUser);
        message.getLikers().add(sender.getUsername());
        messageRepository.save(message);
        String username = message.getUser().getUsername();
        return "redirect:/index/" + username;
    }

    @PostMapping("/messages/unlike/{id}")
    public String unlike(@PathVariable Long id) {
        Message message = messageRepository.getOne(id);
        message.setLikes(message.getLikes() - 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Account sender = accountRepository.findByUsername(currentUser);
        message.getLikers().remove(currentUser);
        messageRepository.save(message);
        String username = message.getUser().getUsername();
        return "redirect:/index/" + username;
    }

    @PostMapping("/messages/comment/{id}")
    public String comment(@PathVariable Long id, @RequestParam String content) {
        Message message = messageRepository.getOne(id);
        String username = message.getUser().getUsername();
        if (!content.equals("")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth.getName();
            Account sender = accountRepository.findByUsername(currentUser);
            Comment comment = new Comment(content, 0, sender, message, null, LocalDateTime.now(), new ArrayList<>());
            message.getComments().add(comment);
            messageRepository.save(message);
            commentRepository.save(comment);
        }
        return "redirect:/index/" + username;
    }
}
