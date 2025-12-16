package top.xym.campusassistantapi.module.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.module.agent.model.entity.DiningHall;
import top.xym.campusassistantapi.module.agent.model.entity.Menu;
import top.xym.campusassistantapi.module.agent.mapper.DiningHallMapper;
import top.xym.campusassistantapi.module.agent.mapper.MenuMapper;
import top.xym.campusassistantapi.module.agent.service.DiningService;

import java.time.LocalDate;
import java.util.List;

/**
 * 食堂服务实现类
 *
 * @author moqi
 */
@Service
@RequiredArgsConstructor
public class DiningServiceImpl extends ServiceImpl<DiningHallMapper, DiningHall> implements DiningService {

    private final MenuMapper menuMapper;

    @Override
    public List<Menu> getMenu(String diningHall, LocalDate date) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getDiningHall, diningHall);
        if (date != null) {
            wrapper.eq(Menu::getDate, date);
        } else {
            wrapper.eq(Menu::getDate, LocalDate.now());
        }
        return menuMapper.selectList(wrapper);
    }

    @Override
    public List<DiningHall> getAllDiningHalls() {
        return list();
    }
}
