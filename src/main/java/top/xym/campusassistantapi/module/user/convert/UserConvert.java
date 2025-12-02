package top.xym.campusassistantapi.module.user.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.xym.campusassistantapi.module.user.model.entity.UserEntity;
import top.xym.campusassistantapi.module.user.model.vo.UserVO;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    /**
     * Entity 转 vo
     */
    UserVO convertToVO(UserEntity entity);


}
