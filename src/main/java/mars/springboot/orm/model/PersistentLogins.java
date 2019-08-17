package mars.springboot.orm.model;

import java.util.Date;

import lombok.Data;

@Data
public class PersistentLogins {
	
    private String username;

    private String series;
    
    private String token;
    
    private Date last_used;

}
