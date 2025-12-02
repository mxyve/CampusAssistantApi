package top.xym.campusassistantapi.module.user.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xym.campusassistantapi.common.exception.ServerException;
import top.xym.campusassistantapi.module.user.convert.UserConvert;
import top.xym.campusassistantapi.module.user.mapper.UserMapper;
import top.xym.campusassistantapi.module.user.model.dto.UserDTO;
import top.xym.campusassistantapi.module.user.model.dto.UserEditDTO;
import top.xym.campusassistantapi.module.user.model.entity.UserEntity;
import top.xym.campusassistantapi.module.user.model.vo.UserVO;
import top.xym.campusassistantapi.module.user.service.UserService;


/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserVO getById(Long id) {
        UserEntity entity = userMapper.selectById(id);
        if (entity == null) {
            throw new ServerException("用户不存在");
        }
        return UserConvert.INSTANCE.convertToVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UserEditDTO dto, Long userId) {
        // 检查用户是否存在
        UserEntity exitEntity = userMapper.selectById(userId);
        if (exitEntity == null) {
            throw new ServerException("用户不存在");
        }

        // 只允许修改昵称、头像、性别、邮箱
        UserEntity entity = new UserEntity();
        entity.setId(userId);

        if (StrUtil.isNotBlank(dto.getNickname())) {
            entity.setNickname(dto.getNickname());
        }
        if (StrUtil.isNotBlank(dto.getAvatar())) {
            entity.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            entity.setGender(dto.getGender());
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            entity.setEmail(dto.getEmail());
        }

        userMapper.updateById(entity);
    }
}
