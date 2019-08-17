package mars.springboot.orm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import mars.springboot.orm.model.User;

@Mapper
public interface LocalhostMapper {

	User getUser(@Param("username") String username);

    void insertUser(@Param("item") User userDO);
    
    void updateUserByUsername(@Param("nickname") String nickname, @Param("username") String username, @Param("password") String password);
}
