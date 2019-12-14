package mars.springboot.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService dbUserDetailsService;
    private AuthenticationProvider customAuthenticationProvider;
    private AuthenticationProvider ipAuthenticationProvider;
    private PersistentTokenRepository persistentTokenRepository;
    private PasswordEncoder passwordEncoder;

	// Autowired实现注入
    @Autowired
    public void setUserDetailsService(UserDetailsService dbUserDetailsService){
        this.dbUserDetailsService = dbUserDetailsService;
    }
    
    @Autowired
    @Qualifier("CustomAuthenticationProvider")
    private void setCustomAuthenticationProvider(AuthenticationProvider authenticationProvider){
    	this.customAuthenticationProvider = authenticationProvider;
    }
    
    @Autowired
    @Qualifier("IpAuthenticationProvider")
    private void setIpAuthenticationProvider(AuthenticationProvider authenticationProvider){
    	this.ipAuthenticationProvider = authenticationProvider;
    }
    
    @Autowired
    private void setPersistentTokenRepository(PersistentTokenRepository persistentTokenRepository){
    	this.persistentTokenRepository = persistentTokenRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
    	this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 复写这个方法来配置 {@link HttpSecurity}. 
     * 通常，子类不能通过调用 super 来调用此方法，因为它可能会覆盖其配置。 默认配置为：
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     * 下面四大项，分别代表了http请求相关的安全配置，这些配置项无一例外的返回了Configurer类（e.g.FormLoginConfigurer,CsrfConfigurer），其都是HttpConfigurer的细化配置项。
     * 
     * 默认启用 CSRF
     */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// 配置路径拦截，表明路径访问所对应的权限，角色，认证信息。所有请求都得经过认证和授权。
			.authorizeRequests()
				// 匹配"/"等路径，不需要权限即可访问
                .antMatchers("/", "/home", "/register", "/login", "/iplogin", "/heartbeat").permitAll()
                // 设置访问权限，匹配"/user"及其以下所有路径，都需要"USER"权限，默认前面加了ROLE_
                .antMatchers("/user/**").hasAnyAuthority("USER", "ADMIN")
//                .antMatchers("/super").hasAuthority("ADMIN")
                // 设置访问角色，需要在UserDetails中额外添加role
                .antMatchers("/super").hasRole("ADMIN")
                // 自定义限制条件
//                .antMatchers("/user/**").access("hasRole('ADMIN') and hasIpAddress('192.168.0.1')")
                // 其他所有资源都需要认证，登陆后访问
                .anyRequest().authenticated();
		http
			// 对应表单认证相关的配置。登入的URL是/login，退出成功后，跳转到/user路径，失败跳转到login?error。
			.formLogin().loginPage("/login").defaultSuccessUrl("/user").failureUrl("/login?error")
				.and()
			// 对应了注销相关的配置。退出的URL是/logout，退出成功后，跳转到/login路径。
			.logout().logoutUrl("/logout").logoutSuccessUrl("/login");
		http
            // 记住我的时间/秒，配置数据库固化token，所有网页请求都支持rememberMe，自定义rememberMe的name值（需要与网页传来的name一致才支持rememberMe，默认remember-Me），
        	.rememberMe().tokenValiditySeconds(300).tokenRepository(persistentTokenRepository).alwaysRemember(false).rememberMeParameter("remember-me");
		http
			// 关闭匿名登录
//			.anonymous().disable()
			// 关闭csrf
			.csrf().disable()
			// 可以配置basic登录。
            .httpBasic().disable();
		/**
		 *  通过在不同Filter的doFilter()方法中加断点调试，可以判断哪个filter先执行，从而判断filter的执行顺序 。
		 */
		http
        	// 在UsernamePasswordAuthenticationFilter前添加IpAuthenticationFilter
        	.addFilterBefore(ipAuthenticationFilterConfig(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
        	
        	// 在atFilter相同位置添加filter， 此filter不覆盖filter
        	.addFilterAt(customFromLoginFilterConfig(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
        	
        	// 在 CsrfFilter 后添加 AfterCsrfFilter
        	.addFilterAfter(new AfterCsrfFilter(), CsrfFilter.class);
	}
	
	/**
	 * 忽略静态资源
	 */
	@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/static/**", "/resources/templates/**", "/resources/public/**");
    }
    
	/**
	 * 通过 {@link #authenticationManager()} 方法的默认实现尝试获取一个 {@link AuthenticationManager}.
     * 如果被复写, 应该使用{@link AuthenticationManagerBuilder}来指定 {@link AuthenticationManager}.
     * 例如, 可以使用以下配置在内存中进行注册公开内存的身份验证{@link UserDetailsService}:
     * 
     * @Override 只用来构建一个“本地” AuthenticationManager，它是全局认证器的一个子实现。自定义身份验证管理器，可以本地化配置你保护的资源，而不用担心影响全局缺省。
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 在内存中创建一个名为 "user" 的用户，密码为 "pwd"，拥有 "USER" 权限，定义密码加密方式。此用户可用于登入验证
		auth.inMemoryAuthentication().passwordEncoder(this.passwordEncoder).withUser("user").password(this.passwordEncoder.encode("pwd")).roles("USER");
		// 添加 UserDetailsService， 实现自定义登录校验
		auth.userDetailsService(this.dbUserDetailsService).passwordEncoder(this.passwordEncoder);
		auth.authenticationProvider(this.ipAuthenticationProvider);
		auth.authenticationProvider(this.customAuthenticationProvider);
	}

	/**
	 * @Autowired 默认的全局 AuthenticationManager（只有一个用户），除非你提供自定义AuthenticationManager类型的bean。
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
	}
	
    //配置封装ipAuthenticationToken的过滤器
	public CustomFromLoginFilter customFromLoginFilterConfig(AuthenticationManager authenticationManager) {
		CustomFromLoginFilter customFromLoginFilter = new CustomFromLoginFilter("/login");
        //为过滤器添加认证器
		customFromLoginFilter.setAuthenticationManager(authenticationManager);
        //重写认证成功时的跳转页面
		customFromLoginFilter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/user"));
        //重写认证失败时的跳转页面
		customFromLoginFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
        /**
         * 设置通过这个Filter绑定的AuthenticationProvider的authenticate认证后，不再需要通过接下来的认证。即，直接成功跳出AuthenticationProvider
         * 但如果这个认证失败，则认为认证全失败，也不再做下面的认证。即，直接失败跳出AuthenticationProvider。所以认证的顺序很重要。
         * 默认即为false
         * 同一个Token（即，support的class类型一样），只需要通过一个认证即可，不需要通过这个Token的所有类型。即，类似于此变量为false。
         */
		customFromLoginFilter.setContinueChainBeforeSuccessfulAuthentication(true);
		return customFromLoginFilter;
    }
	
    //配置封装ipAuthenticationToken的过滤器
	public IpAuthenticationFilter ipAuthenticationFilterConfig(AuthenticationManager authenticationManager) {
        IpAuthenticationFilter ipAuthenticationFilter = new IpAuthenticationFilter("/login");
        //为过滤器添加认证器
        ipAuthenticationFilter.setAuthenticationManager(authenticationManager);
        //重写认证成功时的跳转页面
        ipAuthenticationFilter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/user"));
        //重写认证失败时的跳转页面
        ipAuthenticationFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/iplogin?error"));
        /**
         * 设置通过这个AuthenticationProvider的认证后，还需要通过接下来的认证。即，执行doFilter，而不是直接成功跳出AuthenticationProvider。
         */
        ipAuthenticationFilter.setContinueChainBeforeSuccessfulAuthentication(true);
        return ipAuthenticationFilter;
    }
}