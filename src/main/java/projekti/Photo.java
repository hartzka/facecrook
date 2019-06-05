
package projekti;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
 
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Photo extends AbstractPersistable<Long>{
    
    private String name;
    private String contentType;
    
    private int likes;
    
    @ManyToOne
    private Account user;
    
    @Lob //local
    @Basic(fetch = FetchType.LAZY) //local
    //@Type(type = "org.hibernate.type.BinaryType") //heroku
    private byte[] content;
    
    @OneToMany(mappedBy = "photo")
    private List<Comment> comments;
    
    private LocalDateTime date = LocalDateTime.now();
    private ArrayList<String> likers;
}