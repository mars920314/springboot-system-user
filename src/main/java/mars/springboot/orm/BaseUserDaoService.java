package mars.springboot.orm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mars.springboot.orm.mapper.LocalhostMapper;
import mars.springboot.orm.model.User;

@Service
@Primary
public class BaseUserDaoService implements UserDaoService {

    private final static Map<Integer, String> ENCODER_TYPE = new HashMap<>();

    private final static Map<String, PasswordEncoder> ENCODER_MAP = new HashMap<>();
    
	private final LocalhostMapper localhostMapper;


    public BaseUserDaoService(LocalhostMapper localhostMapper) {
        this.localhostMapper = localhostMapper;
    }

    static {
        ENCODER_TYPE.put(0, "noop");
        ENCODER_TYPE.put(1, "bcrypt");
        ENCODER_TYPE.put(2, "pbkdf2");
        ENCODER_TYPE.put(3, "scrypt");
        ENCODER_TYPE.put(4, "sha256");
        ENCODER_MAP.put("noop", NoOpPasswordEncoder.getInstance());
        ENCODER_MAP.put("bcrypt", new BCryptPasswordEncoder());
        ENCODER_MAP.put("pbkdf2", new Pbkdf2PasswordEncoder());
        ENCODER_MAP.put("scrypt", new SCryptPasswordEncoder());
        ENCODER_MAP.put("sha256", new StandardPasswordEncoder());
    }

    @Override
    public void insert(User user) {
        String username = user.getUsername();
        if (exist(username)){
            throw new RuntimeException("用户名已存在！");
        }
        encryptPassword(user);
        localhostMapper.insertUser(user);
    }

    @Override
    public User getByUsername(String username) {
    	return localhostMapper.getUser(username);
    }

    /**
     * 判断用户是否存在
     */
    private boolean exist(String username){
        return (getByUsername(username) != null);
    }
    
    /**
     * 加密密码
     */
    private void encryptPassword(User userDO){
        String password = userDO.getPassword();
        // 随机使用加密方式
//        Random random = new Random();
//        int x = random.nextInt(5);
//        String encoderType = ENCODER_TYPE.get(x);
//        PasswordEncoder passwordEncoder = ENCODER_MAP.get(encoderType);
//        password = String.format(PASSWORD_FORMAT, encoderType, passwordEncoder.encode(userDO.getPassword()));
        //BCrypt算法加密
        password = new BCryptPasswordEncoder().encode(password);
        userDO.setPassword(password);
    }

}
