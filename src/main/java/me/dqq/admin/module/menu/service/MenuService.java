package me.dqq.admin.module.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.dqq.admin.module.menu.dto.MenuSaveDTO;
import me.dqq.admin.module.menu.entity.SysMenu;
import me.dqq.admin.module.menu.vo.MenuTreeVO;

import java.util.List;

public interface MenuService extends IService<SysMenu> {

    List<MenuTreeVO> getAllMenuTree();

    List<MenuTreeVO> getMenuTreeByUserId(Long userId);

    List<SysMenu> getAllMenuList();

    List<String> getAllPermissions();

    List<String> getPermissionsByUserId(Long userId);

    void saveMenu(MenuSaveDTO dto);

    void updateMenu(MenuSaveDTO dto);

    void deleteMenu(Long id);
}
