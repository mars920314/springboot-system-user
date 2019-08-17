package mars.springboot.service.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class AfterCsrfFilter extends GenericFilterBean {

	/**
	 * 自定义filter主要完成功能：提取认证参数。调用认证，成功则填充SecurityContextHolder的Authentication，失败则抛出异常。
	 */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//    	System.out.println("This is a filter after CsrfFilter.");
        // 继续调用Filter链
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
