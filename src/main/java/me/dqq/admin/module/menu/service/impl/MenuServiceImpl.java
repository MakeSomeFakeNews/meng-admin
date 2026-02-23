package me.dqq.admin.module.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.exception.BusinessException;
import me.dqq.admin.module.menu.dto.MenuSaveDTO;
import me.dqq.admin.module.menu.entity.SysMenu;
import me.dqq.admin.module.menu.mapper.MenuMapper;
import me.dqq.admin.module.menu.service.MenuService;
import me.dqq.admin.module.menu.vo.MenuTreeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, SysMenu> implements MenuService {

    @Override
    public List<MenuTreeVO> getAllMenuTree() {
        List<SysMenu> menus = list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getDeleted, 0)
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort));
        return buildTree(menus);
    }

    @Override
    public List<MenuTreeVO> getMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = baseMapper.selectMenusByUserId(userId);
        return buildTree(menus);
    }

    @Override
    public List<SysMenu> getAllMenuList() {
        return list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getDeleted, 0)
                .orderByAsc(SysMenu::getSort));
    }

    @Override
    public List<String> getAllPermissions() {
        return list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getMenuType, 2)
                .eq(SysMenu::getStatus, 1)
                .eq(SysMenu::getDeleted, 0)
                .isNotNull(SysMenu::getPermission))
                .stream()
                .map(SysMenu::getPermission)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return baseMapper.selectPermissionsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(MenuSaveDTO dto) {
        SysMenu menu = new SysMenu();
        copyDtoToEntity(dto, menu);
        save(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuSaveDTO dto) {
        SysMenu menu = getById(dto.getId());
        if (menu == null) throw new BusinessException("菜单不存在");
        copyDtoToEntity(dto, menu);
        updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        removeById(id);
    }

    /**
     * 递归构建菜单树
     */
    private List<MenuTreeVO> buildTree(List<SysMenu> menus) {
        Map<Long, MenuTreeVO> menuMap = new LinkedHashMap<>();
        for (SysMenu menu : menus) {
            menuMap.put(menu.getId(), toVO(menu));
        }
        List<MenuTreeVO> roots = new ArrayList<>();
        for (MenuTreeVO vo : menuMap.values()) {
            if (vo.getParentId() == null || vo.getParentId() == 0L) {
                roots.add(vo);
            } else {
                MenuTreeVO parent = menuMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        return roots;
    }

    private MenuTreeVO toVO(SysMenu menu) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setTitle(menu.getTitle());
        vo.setName(menu.getName());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setMenuType(menu.getMenuType());
        vo.setPermission(menu.getPermission());
        vo.setSort(menu.getSort());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        return vo;
    }

    private void copyDtoToEntity(MenuSaveDTO dto, SysMenu menu) {
        menu.setParentId(dto.getParentId());
        menu.setTitle(dto.getTitle());
        menu.setName(dto.getName());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setIcon(dto.getIcon());
        menu.setMenuType(dto.getMenuType());
        menu.setPermission(dto.getPermission());
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }
}
