package com.cmc.enums.handler;

import com.cmc.enums.TagStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(TagStatus.class)
@MappedJdbcTypes(JdbcType.VARCHAR) // 数据库字段类型是 varchar
public class TagStatusTypeHandler extends BaseTypeHandler<TagStatus> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TagStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode()); // 存库用 code
    }

    @Override
    public TagStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return TagStatus.fromCode(rs.getString(columnName)); // 取库时用 code 转枚举
    }

    @Override
    public TagStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return TagStatus.fromCode(rs.getString(columnIndex));
    }

    @Override
    public TagStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return TagStatus.fromCode(cs.getString(columnIndex));
    }
}
