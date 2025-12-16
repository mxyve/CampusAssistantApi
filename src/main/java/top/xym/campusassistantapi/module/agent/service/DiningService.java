package top.xym.campusassistantapi.module.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.campusassistantapi.module.agent.model.entity.DiningHall;
import top.xym.campusassistantapi.module.agent.model.entity.Menu;

import java.time.LocalDate;
import java.util.List;

public interface DiningService extends IService<DiningHall> {
    /**
     * 查询菜单
     */
    List<Menu> getMenu(String diningHall, LocalDate date);

    /**
     * 查询所有食堂
     */
    List<DiningHall> getAllDiningHalls();
}