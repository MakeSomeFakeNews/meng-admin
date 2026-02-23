package me.dqq.admin.module.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dqq.admin.common.exception.BusinessException;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.common.result.ResultCode;
import me.dqq.admin.common.utils.PasswordUtil;
import me.dqq.admin.module.log.entity.SysLogLogin;
import me.dqq.admin.module.log.service.LogLoginService;
import me.dqq.admin.module.profile.dto.UpdateInfoDTO;
import me.dqq.admin.module.profile.dto.UpdatePasswordDTO;
import me.dqq.admin.module.profile.service.ProfileService;
import me.dqq.admin.module.user.entity.SysUser;
import me.dqq.admin.module.user.service.UserService;
import me.dqq.admin.module.user.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserService userService;
    private final LogLoginService logLoginService;

    @Override
    public UserVO getInfo(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) throw new BusinessException(ResultCode.USER_NOT_FOUND);

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    @Override
    public void updateInfo(Long userId, UpdateInfoDTO dto) {
        SysUser user = userService.getById(userId);
        if (user == null) throw new BusinessException(ResultCode.USER_NOT_FOUND);

        if (StringUtils.hasText(dto.getNickname())) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        userService.updateById(user);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 校验文件类型
        if (!ext.equalsIgnoreCase(".jpg") && !ext.equalsIgnoreCase(".jpeg") && !ext.equalsIgnoreCase(".png")) {
            throw new BusinessException("只支持 JPG/PNG 格式的图片");
        }
        // 文件大小限制 2MB
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过2MB");
        }

        // 保存文件到 upload 目录（生产环境应上传至OSS）
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String uploadDir = System.getProperty("user.dir") + "/upload/avatar/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        try {
            file.transferTo(new File(uploadDir + fileName));
        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusinessException("头像上传失败");
        }

        String avatarUrl = "/upload/avatar/" + fileName;
        SysUser user = userService.getById(userId);
        if (user != null) {
            user.setAvatar(avatarUrl);
            userService.updateById(user);
        }
        return avatarUrl;
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        SysUser user = userService.getById(userId);
        if (user == null) throw new BusinessException(ResultCode.USER_NOT_FOUND);

        // 验证旧密码
        if (!PasswordUtil.verify(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        user.setPassword(PasswordUtil.encode(dto.getNewPassword()));
        userService.updateById(user);
    }

    @Override
    public PageResult<SysLogLogin> getLoginLogs(Long userId, int current, int size) {
        // 获取用户名
        SysUser user = userService.getById(userId);
        if (user == null) throw new BusinessException(ResultCode.USER_NOT_FOUND);
        return logLoginService.pageListByUsername(user.getUsername(), current, size);
    }
}
