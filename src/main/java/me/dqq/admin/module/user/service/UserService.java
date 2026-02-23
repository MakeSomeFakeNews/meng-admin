package me.dqq.admin.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.module.user.dto.UserQueryDTO;
import me.dqq.admin.module.user.dto.UserSaveDTO;
import me.dqq.admin.module.user.dto.UserUpdateDTO;
import me.dqq.admin.module.user.entity.SysUser;
import me.dqq.admin.module.user.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<SysUser> {

    /** 分页查询 */
    PageResult<UserVO> pageList(UserQueryDTO query);

    /** 根据用户名查询 */
    SysUser getByUsername(String username);

    /** 新增用户 */
    void saveUser(UserSaveDTO dto);

    /** 编辑用户 */
    void updateUser(UserUpdateDTO dto);

    /** 删除用户（逻辑删除） */
    void deleteUser(Long id);

    /** 重置密码 */
    void resetPassword(Long userId, String newPassword);

    /** 修改状态 */
    void updateStatus(Long id, Integer status);

    /** 获取用户已分配的角色ID列表 */
    List<Long> getUserRoleIds(Long userId);

    /** 分配用户角色 */
    void assignRoles(Long userId, List<Long> roleIds);
}
