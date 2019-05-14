package projekti;

import java.util.ArrayList;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/accounts")
    public String list(Model model) {
        return "accounts";
    }

    @PostMapping("/accounts")
    public String add(@Valid @ModelAttribute Account a, BindingResult bindingResult, Model model) {
        if (accountRepository.findByUsername(a.getUsername()) != null) {
            model.addAttribute("reserved", true);
            return "accounts";
        }
        if (bindingResult.hasErrors()) {
            return "accounts";
        }
        Account account = new Account();
        account.setName(a.getName());
        account.setUsername(a.getUsername());
        account.setPassword(passwordEncoder.encode(a.getPassword()));
        account.setAuthorities(new ArrayList<>());
        account.setFriends(new ArrayList<>());
        account.setInvitations(new ArrayList<>());
        account.setMessages(new ArrayList<>());
        account.setPhotos(new ArrayList<>());
        account.setProfilePhotoId(0L);
        accountRepository.save(account);
        model.addAttribute("created", true);
        return "accounts";
    }
}
