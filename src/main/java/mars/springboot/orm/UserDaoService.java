package mars.springboot.orm;

import mars.springboot.orm.model.User;

public interface UserDaoService {

    /**
     * 添加新用户
     *
     * username 唯一， 默认 USER 权限
     */
    void insert(User userDO);

    /**
     * 查询用户信息
     * @param username 账号
     * @return UserEntity
     */
    User getByUsername(String username);

}
