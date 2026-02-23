package me.dqq.admin.module.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.exception.BusinessException;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.common.result.ResultCode;
import me.dqq.admin.module.role.dto.RoleQueryDTO;
import me.dqq.admin.module.role.dto.RoleSaveDTO;
import me.dqq.admin.module.role.entity.SysRole;
import me.dqq.admin.module.role.mapper.RoleMapper;
import me.dqq.admin.module.role.service.RoleService;
import me.dqq.admin.module.role.vo.RoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {

    @Override
    public PageResult<RoleVO> pageList(RoleQueryDTO query) {
        Page<SysRole> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
                .like(StringUtils.hasText(query.getRoleCode()), SysRole::getRoleCode, query.getRoleCode())
                .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
                .orderByAsc(SysRole::getSort);
        page(page, wrapper);
        List<RoleVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(page, voList);
    }

    @Override
    public List<RoleVO> listAll() {
        List<SysRole> roles = list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSort));
        return roles.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleSaveDTO dto) {
        long count = count(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, dto.getRoleCode()));
        if (count > 0) throw new BusinessException(ResultCode.ROLE_CODE_EXISTS);

        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setSort(dto.getSort());
        role.setStatus(dto.getStatus());
        role.setRemark(dto.getRemark());
        save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleSaveDTO dto) {
        SysRole role = getById(dto.getId());
        if (role == null) throw new BusinessException("角色不存在");

        // 如果修改了 roleCode，检查唯一性
        if (!role.getRoleCode().equals(dto.getRoleCode())) {
            long count = count(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getRoleCode, dto.getRoleCode())
                    .ne(SysRole::getId, dto.getId()));
            if (count > 0) throw new BusinessException(ResultCode.ROLE_CODE_EXISTS);
        }

        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setSort(dto.getSort());
        role.setStatus(dto.getStatus());
        role.setRemark(dto.getRemark());
        updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        removeById(id);
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return baseMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        baseMapper.deleteRoleMenus(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            baseMapper.insertRoleMenus(roleId, menuIds);
        }
    }

    private RoleVO toVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setSort(role.getSort());
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}
