
package projekti;
  
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation extends AbstractPersistable<Long>{
    
    private String fromUserUsername;
    private String toUserUsername;
    
    @ManyToMany
    private List<Account> users;
    
    private int accepted; // 0 = no action yet, 1 = declined, 2 = accepted
    
    private LocalDateTime datetime;
}
