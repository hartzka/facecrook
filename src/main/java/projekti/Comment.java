
package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Comment extends AbstractPersistable<Long> {
    
    
    private String content;
    
    private int likes;
    
    @ManyToOne
    private Account sender;
    
    @ManyToOne
    private Message message;
    
    @ManyToOne
    private Photo photo;
    
    private LocalDateTime date = LocalDateTime.now();
    
    private ArrayList<String> likers;
}