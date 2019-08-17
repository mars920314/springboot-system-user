package mars.springboot.service.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mars.springboot.orm.UserDaoService;
import mars.springboot.orm.model.User;

/**
 * 进行认证的时候需要一个 UserDetailsService 来获取用户的信息 UserDetails，其中包括用户名、密码和所拥有的权限等。
 */
@Service
@Primary
public class DbUserDetailsService implements UserDetailsService {
    
    private final String role_ = "ROLE_";

    private final UserDaoService userDaoService;
	
    @Autowired
    DbUserDetailsService(UserDaoService userService){
        this.userDaoService = userService;
    }

    /**
     * 根据用户名，从数据库中加载已注册的用户，选择出下一步需要用于比对的已注册的用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDaoService.getByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("用户不存在！");
        }
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        /** new SimpleGrantedAuthority("...")时：
         * 加前戳是Role，通过hasRole()获取，用来认证角色；
         * 不加前戳是Authoritiy,通过hasAuthority()获取，用来鉴定权限；
         */
        if(user.getNickname().equals("admin")){
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(role_ + "ADMIN"));
        }
        else
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), simpleGrantedAuthorities);
        return userDetails;
    }

}
