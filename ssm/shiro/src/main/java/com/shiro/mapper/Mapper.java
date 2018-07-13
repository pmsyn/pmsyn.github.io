package com.shiro.mapper;

import com.shiro.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * @author pms
 * @create 2017/12/17 16:09
 * @since 1.0
 * 通用查询接口
 */
@Repository
public interface Mapper {
    @Select("select * from ${tableName} where ${whereSql}")
    public List<Map<String, Object>> findList(@Param("tableName") String tableName, @Param("whereSql") String whereSql);

    @Select("select * from ${tableName} where ${whereSql}")
    public Map<String, Object> getSingleResult(@Param("tableName") String tableName, @Param("whereSql") String
            whereSql);

    @Select("select ${column} from ${tableName} where ${whereSql}")
    public Map<String, Object> getSingleColumn(@Param("column") String column, @Param("tableName") String tableName,
                                               @Param("whereSql") String whereSql);

    @Select("select * from (select rownum rn,a.* from (${sql}) a where rownum <= #{end}) a where rn > #{start}")
    public List<Map<String, Object>> queryListForPages(@Param("sql") String sql, @Param("end") int end, @Param
            ("start") int start);

    @Select("select * from (select rownum rn,a.* from (select ${column} from ${tableName} where ${conditionSql}) a " +
            "where rownum <= #{end}) a where  rn > #{start}")
    public List<Map<String, Object>> queryColumnListForPages(@Param("column") String column, @Param("tableName") String
            tableName, @Param("conditionSql") String conditionSql, @Param("end") int end, @Param("start") int start);

    @Select("${sql}")
    public List<Map<String, Object>> queryListBySql(@Param("sql") String sql);

    @Insert("${sql}")
    public int insert(Map<String, Object> map);

    @Update("${sql}")
    public int update(Map<String, Object> map);

    @Update("${sql}")
    public int updateBySql(@Param("sql") String sql);

    @Delete("delete from ${tableName} where #{column} = #{value}")
    public int deleteByColumn(@Param("tableName") String tableName, @Param("column") String column, @Param("value")
            String value);

    @Delete("delete from ${tableName} where ${conditionSql}")
    public int deleteBySql(@Param("tableName") String tableName, @Param("conditionSql") String conditionSql);

    @Delete("delete from ${table} where id = #{id}")
    public int deleteById(@Param("table") String table, @Param("id") String id);

    @Select("select * from user where account=#{account}")
    public User getUserByAccount(@Param("account") String account);

}
