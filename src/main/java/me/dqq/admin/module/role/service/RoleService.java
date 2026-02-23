package me.dqq.admin.module.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.module.role.dto.RoleQueryDTO;
import me.dqq.admin.module.role.dto.RoleSaveDTO;
import me.dqq.admin.module.role.entity.SysRole;
import me.dqq.admin.module.role.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<SysRole> {

    PageResult<RoleVO> pageList(RoleQueryDTO query);

    List<RoleVO> listAll();

    void saveRole(RoleSaveDTO dto);

    void updateRole(RoleSaveDTO dto);

    void deleteRole(Long id);

    List<Long> getRoleMenuIds(Long roleId);

    void assignMenus(Long roleId, List<Long> menuIds);
}
