
package projekti;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;
 
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Photo extends AbstractPersistable<Long>{
    
    private String name;
    private String contentType;
    private Long contentLength;
    
    private int likes;
    
    @ManyToOne
    private Account user;
    
    @Type(type="org.hibernate.type.PrimitiveByteArrayBlobType") 
    private byte[] content;
    
    @OneToMany(mappedBy = "photo")
    private List<Comment> comments;
    
    private LocalDateTime date = LocalDateTime.now();
    private ArrayList<String> likers;
}