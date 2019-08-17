package mars.springboot.service.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 自定义表单登录
 */
public class CustomFromLoginFilter extends AbstractAuthenticationProcessingFilter {

    CustomFromLoginFilter(String defaultFilterProcessesUrl) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl, HttpMethod.POST.name()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String username = httpServletRequest.getParameter("username");
        String password = httpServletRequest.getParameter("password");
        username = username.trim();
        SimpleGrantedAuthority simpleGrantedAuthority = customCheck(username, password);
        
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        if(simpleGrantedAuthority!=null)
        	simpleGrantedAuthorities.add(simpleGrantedAuthority);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password, simpleGrantedAuthorities);
//        System.out.println(this);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private SimpleGrantedAuthority customCheck(String username, String password){
    	if(username==null)
            throw new RuntimeException("用户名不合法！");
    	else if(username.equals("lj_g"))
    		return new SimpleGrantedAuthority("ADMIN");
    	else
    		return null;
    }

}
