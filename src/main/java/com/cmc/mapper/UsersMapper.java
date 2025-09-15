package com.cmc.mapper;

import com.cmc.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author C
 * @since 2025-09-10
 */
@Repository
public interface UsersMapper extends BaseMapper<Users> {

    Users getUserInfoById(@Param("id") Integer id);
}
