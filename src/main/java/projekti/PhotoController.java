package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

@Controller
public class PhotoController {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/photos/{username}")
    public String getPhotos(Model model, @PathVariable String username) {
        Account acc = accountRepository.findByUsername(username);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        List<Photo> photos = photoRepository.findAllByUser(acc, pageable);
        model.addAttribute("photos", photos);
        HashMap<Photo, List<Comment>> h = new HashMap<>();
        Pageable pageable2 = PageRequest.of(0, 10, Sort.by("date").descending());
        for (Photo ph : photos) {
            List<Comment> comments = commentRepository.findAllByPhoto(ph, pageable2);
            h.put(ph, comments);
        }
        model.addAttribute("comments", h);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Account current = accountRepository.findByUsername(currentUser);
        model.addAttribute("currentUser", current);
        model.addAttribute("owner", username);
        return "photos";
    }

    @GetMapping("/photos/{username}/{id}")
    public ResponseEntity<byte[]> viewPhoto(@PathVariable String username, @PathVariable Long id) {
        Photo ph = photoRepository.getOne(id);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ph.getContentType()));
        headers.add("Content-Disposition", "attachment; filename=" + ph.getName());
        System.out.println(ph.getName());
        return new ResponseEntity<>(ph.getContent(), headers, HttpStatus.CREATED);
    }

    @GetMapping("/profilephoto/{id}")
    public ResponseEntity<byte[]> viewProfilePhoto(@PathVariable Long id) {
        Photo ph = photoRepository.getOne(id);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ph.getContentType()));
        headers.add("Content-Disposition", "attachment; filename=" + ph.getName());
        System.out.println(ph.getName());
        return new ResponseEntity<>(ph.getContent(), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getDefaultProfilePhoto() throws IOException {

        var imgFile = new ClassPathResource("public/default.jpg");
        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    @GetMapping(value = "/facecrook",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getFacecrook() throws IOException {

        var imgFile = new ClassPathResource("public/facecrook.jpg");
        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    @PostMapping("/photos")
    public String save(@RequestParam("photo") MultipartFile file) throws IOException {
        if (file.getContentType().contains("image")) {
            Photo ph = new Photo();
            ph.setContent(file.getBytes());
            ph.setName(file.getOriginalFilename());
            ph.setContentType(file.getContentType());
            ph.setContentLength(file.getSize());
            ph.setContent(file.getBytes());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth.getName();
            Account current = accountRepository.findByUsername(currentUser);
            ph.setUser(current);
            ph.setLikers(new ArrayList<>());
            current.getPhotos().add(ph);
            photoRepository.save(ph);
            accountRepository.save(current);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        return "redirect:/photos/" + currentUser;
    }

    @DeleteMapping("/photos/delete/{id}")
    public String delete(@PathVariable Long id) {
        Photo photo = photoRepository.getOne(id);
        List<Comment> comments = photo.getComments();
        for (Comment c : comments) {
            commentRepository.delete(c);
        }
        Long photoId = photo.getId();
        photoRepository.delete(photo);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account acc = accountRepository.findByUsername(username);
        if (Objects.equals(acc.getProfilePhotoId(), photoId)) {
            acc.setProfilePhotoId(0L);
            accountRepository.save(acc);
        }
        return "redirect:/photos/" + username;
    }

    @PostMapping("/photos/like/{id}")
    public String like(@PathVariable Long id) {
        Photo ph = photoRepository.getOne(id);
        ph.setLikes(ph.getLikes() + 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = auth.getName();
        ph.getLikers().add(current);
        photoRepository.save(ph);
        String username = ph.getUser().getUsername();
        return "redirect:/photos/" + username;
    }

    @PostMapping("/photos/unlike/{id}")
    public String unlike(@PathVariable Long id) {
        Photo ph = photoRepository.getOne(id);
        ph.setLikes(ph.getLikes() - 1);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = auth.getName();
        ph.getLikers().remove(current);
        photoRepository.save(ph);
        String username = ph.getUser().getUsername();
        return "redirect:/photos/" + username;
    }

    @PostMapping("/photos/profile/{id}")
    public String setAsProfilePicture(@PathVariable Long id) {
        Photo ph = photoRepository.getOne(id);
        String username = ph.getUser().getUsername();
        Account acc = accountRepository.findByUsername(username);
        acc.setProfilePhotoId(ph.getId());
        accountRepository.save(acc);
        return "redirect:/photos/" + username;
    }

    @PostMapping("/photos/unprofile/{id}")
    public String removeProfilePicture(@PathVariable Long id) {
        Photo ph = photoRepository.getOne(id);
        String username = ph.getUser().getUsername();
        Account acc = accountRepository.findByUsername(username);
        acc.setProfilePhotoId(0L);
        accountRepository.save(acc);
        return "redirect:/photos/" + username;
    }

    @PostMapping("/photos/comment/{id}")
    public String comment(@PathVariable Long id, @RequestParam String content) {
        Photo photo = photoRepository.getOne(id);
        String username = photo.getUser().getUsername();
        if (!content.equals("")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth.getName();
            Account sender = accountRepository.findByUsername(currentUser);
            Comment comment = new Comment(content, 0, sender, null, photo, LocalDateTime.now(), new ArrayList<>());
            photo.getComments().add(comment);
            photoRepository.save(photo);
            commentRepository.save(comment);
        }
        return "redirect:/photos/" + username;
    }
}
