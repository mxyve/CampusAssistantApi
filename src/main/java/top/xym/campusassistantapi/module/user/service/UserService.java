package top.xym.campusassistantapi.module.user.service;

import top.xym.campusassistantapi.module.user.model.dto.UserDTO;
import top.xym.campusassistantapi.module.user.model.dto.UserEditDTO;
import top.xym.campusassistantapi.module.user.model.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    UserVO getById(Long id);

    /**
     * 修改个人信息
     *
     * @param dto 用户信息
     * @param userId 用户 ID
     */
    void updateProfile(UserEditDTO dto, Long userId);
}
