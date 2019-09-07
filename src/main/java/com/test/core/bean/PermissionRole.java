package com.test.core.bean;

import com.test.base.BaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 
 *
 * @author black
 * @date 2019-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_permission_role")
public class PermissionRole extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER)
    private Long id;

    private Long roleId;

    private Long permissionId;


}
