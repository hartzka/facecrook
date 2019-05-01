package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InvitationController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @GetMapping("/invitations/{username}")
    public String invitations(Model model, @PathVariable String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Account owner = accountRepository.findByUsername(username);
        List<Invitation> invitations = owner.getInvitations();
        List<Invitation> sentInvitations = new ArrayList<>();
        for (Invitation inv : invitations) {
            if (inv.getFromUserUsername().equals(currentUser)) {
                sentInvitations.add(inv);
            }
        }
        invitations.removeAll(sentInvitations);
        model.addAttribute("invitations", invitations);
        model.addAttribute("sentInvitations", sentInvitations);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("friends", owner.getFriends());
        model.addAttribute("owner", username);
        return "invitations";
    }

    @PostMapping("/invitations/search/{username}")
    public String search(Model model, @PathVariable String username, @RequestParam String content) {
        if (content != null && !content.trim().isEmpty()) {
            Account acc = accountRepository.findByUsername(content);
            if (acc == null) {
                model.addAttribute("name", "No matches found");
                model.addAttribute("username", "");
            } else {
                String name = acc.getName();
                String user = acc.getUsername();
                Account friendAccount = accountRepository.findByUsername(user);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String currentUser = auth.getName();
                Account current = accountRepository.findByUsername(currentUser);
                List<String> friends = current.getFriends();
                List<Invitation> currentInvitations = current.getInvitations();
                List<Invitation> friendInvitations = friendAccount.getInvitations();
                boolean found = false;
                for (String friendUserName : friends) {
                    if (friendUserName.equals(user)) {
                        model.addAttribute("name", name);
                        model.addAttribute("username", user);
                        model.addAttribute("alreadyFriend", true);
                        found = true;
                    }
                }
                for (Invitation inv : currentInvitations) {
                    if ((inv.getFromUserUsername().equals(user) && inv.getToUserUsername().equals(currentUser)) || (inv.getFromUserUsername().equals(currentUser) && inv.getToUserUsername().equals(user))) {
                        model.addAttribute("name", name);
                        model.addAttribute("username", user);
                        model.addAttribute("alreadyFriend", true);
                        found = true;
                    }
                }
                for (Invitation inv : friendInvitations) {
                    if (inv.getFromUserUsername().equals(currentUser) && inv.getToUserUsername().equals(user)) {
                        model.addAttribute("name", name);
                        model.addAttribute("username", user);
                        model.addAttribute("alreadyFriend", true);
                        found = true;
                    }
                }
                if (!found) {
                    model.addAttribute("name", name);
                    model.addAttribute("username", user);
                }
            }
        } else {
            model.addAttribute("name", "No matches found");
            model.addAttribute("username", "");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Account owner = accountRepository.findByUsername(username);
        List<Invitation> invitations = owner.getInvitations();
        List<Invitation> sentInvitations = new ArrayList<>();
        for (Invitation inv : invitations) {
            if (inv.getFromUserUsername().equals(currentUser)) {
                sentInvitations.add(inv);
            }
        }
        invitations.removeAll(sentInvitations);
        model.addAttribute("invitations", invitations);
        model.addAttribute("sentInvitations", sentInvitations);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("friends", owner.getFriends());
        model.addAttribute("owner", username);
        return "invitations";
    }

    @PostMapping("invitations/ask/{username}")
    public String ask(@PathVariable String username) {
        Account to = accountRepository.findByUsername(username);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Account from = accountRepository.findByUsername(currentUser);
        Invitation inv = new Invitation(currentUser, username, new ArrayList<>(Arrays.asList(from, to)), 0, LocalDateTime.now());
        from.getInvitations().add(inv);
        to.getInvitations().add(inv);
        invitationRepository.save(inv);
        accountRepository.save(from);
        accountRepository.save(to);

        return "redirect:/invitations/" + currentUser;
    }

    @PostMapping("invitations/delete/{username}")
    public String deleteAll(@PathVariable String username) {
        Account account = accountRepository.findByUsername(username);
        List<Invitation> invitations = new ArrayList<>();
        invitations.addAll(account.getInvitations());
        //account.getInvitations().clear();
        for (Invitation inv : invitations) {
            if (inv.getFromUserUsername().equals(username)) {
                account.getInvitations().remove(inv);
                inv.getUsers().remove(account);
                invitationRepository.save(inv);
            }
        }
        accountRepository.save(account);
        return "redirect:/invitations/" + username;
    }

    @PostMapping("invitations/accept/{invitationId}")
    public String accept(@PathVariable Long invitationId) {
        Invitation inv = invitationRepository.getOne(invitationId);
        String sender = inv.getFromUserUsername();
        String current = inv.getToUserUsername();
        Account senderAccount = accountRepository.findByUsername(sender);
        Account currentAccount = accountRepository.findByUsername(current);
        currentAccount.getFriends().add(sender);
        senderAccount.getFriends().add(current);
        inv.setAccepted(2);
        inv.getUsers().remove(currentAccount);
        if (currentAccount.getInvitations().contains(inv)) {
            currentAccount.getInvitations().remove(inv);
        }
        accountRepository.save(currentAccount);
        accountRepository.save(senderAccount);
        invitationRepository.save(inv);
        return "redirect:/invitations/" + current;
    }

    @PostMapping("invitations/decline/{invitationId}")
    public String decline(@PathVariable Long invitationId) {
        Invitation inv = invitationRepository.getOne(invitationId);
        inv.setAccepted(1);
        Account acc = accountRepository.findByUsername(inv.getToUserUsername());
        inv.getUsers().remove(acc);
        acc.getInvitations().remove(inv);
        accountRepository.save(acc);
        invitationRepository.save(inv);
        return "redirect:/invitations/" + inv.getToUserUsername();
    }
}
