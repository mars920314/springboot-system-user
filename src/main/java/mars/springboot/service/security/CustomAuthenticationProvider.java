package mars.springboot.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Qualifier("CustomAuthenticationProvider")
@Service
public class CustomAuthenticationProvider implements AuthenticationProvider{

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
	
    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
    	this.passwordEncoder = passwordEncoder;
    }

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 1.获取用户输入的用户名，密码
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getName();
        String password = (String) token.getCredentials();
        // 2.加密：因为是自定义Authentication，所以必须手动加密加盐而不需要再配置
//        password = new Md5PasswordEncoder().encodePassword(password, username);
        // 3.由输入的用户名查找该用户信息，比对用户输入的密码和数据库保存的密码
        UserDetails user = userDetailsService.loadUserByUsername(username);
        // 4.密码校验，内部抛出异常
        if (!this.passwordEncoder.matches(password, user.getPassword()))
//        if (!password.equals(user.getPassword())) {
            return null;
        else
        	return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
