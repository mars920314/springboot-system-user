package mars.springboot.service.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Qualifier("IpAuthenticationProvider")
@Service
public class IpAuthenticationProvider implements AuthenticationProvider {
	
	public IpAuthenticationProvider(){
		super();
        ipAuthorityMap.put("127.0.0.1", new SimpleGrantedAuthority("ADMIN"));
        ipAuthorityMap.put("0:0:0:0:0:0:0:1", new SimpleGrantedAuthority("ADMIN"));
        ipForbiddenMap.put("10.236.69.104", null);
	}

    //维护一个ip白名单和一个黑名单列表，每个ip对应一定的权限
    private Map<String, SimpleGrantedAuthority> ipAuthorityMap = new HashMap<String, SimpleGrantedAuthority>();
    private Map<String, SimpleGrantedAuthority> ipForbiddenMap = new HashMap<String, SimpleGrantedAuthority>();

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        IpAuthenticationToken ipAuthenticationToken = (IpAuthenticationToken) authentication;
        String ip = ipAuthenticationToken.getIp();
        if(ipAuthorityMap.containsKey(ip)){
            //封装权限信息，并且此时身份已经被认证
            SimpleGrantedAuthority simpleGrantedAuthority = ipAuthorityMap.get(ip);
            return new IpAuthenticationToken(ip, Arrays.asList(simpleGrantedAuthority));
        }
        //在黑名单列表中
        else if (ipForbiddenMap.containsKey(ip)) {
            return null;
        } else {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("USER");
            return new IpAuthenticationToken(ip, Arrays.asList(simpleGrantedAuthority));
        }
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (IpAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
