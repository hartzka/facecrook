
package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @DeleteMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        Comment comment = commentRepository.getOne(id);
        Message message = comment.getMessage();
        commentRepository.deleteById(id);
        String username = message.getUser().getUsername();
        return "redirect:/index/" + username;
    }

    @PostMapping("/comments/like/{id}")
    public String likeComment(@PathVariable Long id) {
        Comment comment = commentRepository.getOne(id);
        comment.setLikes(comment.getLikes() + 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        comment.getLikers().add(currentUser);
        commentRepository.save(comment);
        String username = comment.getMessage().getUser().getUsername();
        return "redirect:/index/" + username;
    }
    
    @PostMapping("/comments/unlike/{id}")
    public String unlikeComment(@PathVariable Long id) {
        Comment comment = commentRepository.getOne(id);
        comment.setLikes(comment.getLikes() - 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        comment.getLikers().remove(currentUser);
        commentRepository.save(comment);
        String username = comment.getMessage().getUser().getUsername();
        return "redirect:/index/" + username;
    }
    
    @DeleteMapping("/photocomments/delete/{id}")
    public String deletePhotoComment(@PathVariable Long id) {
        Comment comment = commentRepository.getOne(id);
        Photo photo = comment.getPhoto();
        commentRepository.deleteById(id);
        String username = photo.getUser().getUsername();
        return "redirect:/photos/" + username;
    }

    @PostMapping("/photocomments/like/{id}")
    public String likePhotoComment(@PathVariable Long id) {
        Comment comment = commentRepository.getOne(id);
        comment.setLikes(comment.getLikes() + 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        comment.getLikers().add(currentUser);
        commentRepository.save(comment);
        String username = comment.getPhoto().getUser().getUsername();
        return "redirect:/photos/" + username;
    }
    
    @PostMapping("/photocomments/unlike/{id}")
    public String unlikePhotoComment(@PathVariable Long id) {
        Comment comment = commentRepository.getOne(id);
        comment.setLikes(comment.getLikes() - 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        comment.getLikers().remove(currentUser);
        commentRepository.save(comment);
        String username = comment.getPhoto().getUser().getUsername();
        return "redirect:/photos/" + username;
    }
}
