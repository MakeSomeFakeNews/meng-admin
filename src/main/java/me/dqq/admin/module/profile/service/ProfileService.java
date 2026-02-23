package me.dqq.admin.module.profile.service;

import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.module.log.entity.SysLogLogin;
import me.dqq.admin.module.profile.dto.UpdateInfoDTO;
import me.dqq.admin.module.profile.dto.UpdatePasswordDTO;
import me.dqq.admin.module.user.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    UserVO getInfo(Long userId);

    void updateInfo(Long userId, UpdateInfoDTO dto);

    String uploadAvatar(Long userId, MultipartFile file);

    void updatePassword(Long userId, UpdatePasswordDTO dto);

    PageResult<SysLogLogin> getLoginLogs(Long userId, int current, int size);
}
