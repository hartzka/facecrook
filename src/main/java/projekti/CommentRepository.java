
package projekti;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    public List<Comment> findAllByMessage(Message m, Pageable pageable2);

    public List<Comment> findAllByPhoto(Photo ph, Pageable pageable);
    
}
