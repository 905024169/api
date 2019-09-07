package com.test.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: api
 * @description:
 * @author: duanwei
 * @create: 2019-09-01 11:23
 **/
@Data
@Accessors(chain = true)
public class BaseModel extends Model {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Object ref;
}
