package mars.springboot.orm.model;

import java.util.Date;

import lombok.Data;

/**
 * 支持Remember Me功能，允许验证成功的用户能够被系统记住，前次访问未注销，而再次访问时，无需登录。
 * PersistentTokenBasedRememberMeServices为每个用户创建一个唯一的series，
 * 用户在继续认证和交互时要使用series来查找对应的token，与存储在cookie中的Token进行对比完成认证。
 * series和token都是随机生成的，被暴力破解的难度大大降低。
 */
@Data
public class PersistentLogins {
	
    private String username;

    private String series;
    
    private String token;
    
    private Date last_used;

}
