package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {

    private String name;
    
    private String username;
    
    private String password;
    
    private ArrayList<String> friends;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities;
   
    @ManyToMany
    private List<Invitation> invitations;
    
    @OneToMany(mappedBy = "user")
    private List<Message> messages;
    
    @OneToMany(mappedBy = "user")
    private List<Photo> photos;
    
    private Long profilePhotoId;

}
