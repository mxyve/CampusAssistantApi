package top.xym.campusassistantapi.module.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.xym.campusassistantapi.module.agent.model.entity.Building;

/**
 * 建筑表 Mapper 接口（适配MP规范）
 *
 * @author moqi
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {

}