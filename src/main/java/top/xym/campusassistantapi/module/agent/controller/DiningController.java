package top.xym.campusassistantapi.module.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.campusassistantapi.module.agent.model.entity.DiningHall;
import top.xym.campusassistantapi.module.agent.model.entity.Menu;
import top.xym.campusassistantapi.module.agent.service.DiningService;

import java.time.LocalDate;
import java.util.List;

/**
 * 食堂控制器
 *
 * @author moqi
 */
@RestController
@RequestMapping("/api/dining")
@RequiredArgsConstructor
@Tag(name = "食堂相关", description = "食堂相关接口")
public class DiningController {

    private final DiningService diningService;

    /**
     * 查询菜单
     */
    @GetMapping("/menu")
    @Operation(summary = "查询菜单")
    public List<Menu> getMenu(@RequestParam String diningHall,
                              @RequestParam(required = false) String date) {
        LocalDate localDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return diningService.getMenu(diningHall, localDate);
    }

    /**
     * 查询所有食堂
     */
    @GetMapping("/halls")
    @Operation(summary = "查询所有食堂")
    public List<DiningHall> getAllDiningHalls() {
        return diningService.getAllDiningHalls();
    }

    /**
     * 查询食堂详情
     */
    @GetMapping("/hall/{id}")
    @Operation(summary = "查询食堂详情")
    public DiningHall getDiningHall(@PathVariable Long id) {
        return diningService.getById(id);
    }
}