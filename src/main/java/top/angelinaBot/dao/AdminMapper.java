package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdminMapper {

    @Insert("insert into a_group_func_close (group_id, func_name) values (#{groupId}, #{funcName});")
    Integer closeFunction(@Param("groupId") String groupId, @Param("funcName") String funcName);

    @Delete("delete from a_group_func_close where group_id=#{groupId} and func_name=#{funcName};")
    Integer openFunction(@Param("groupId") String groupId, @Param("funcName") String funcName);

    @Select("select (select count(group_id) from a_group_func_close where group_id=#{groupId} and func_name=#{funcName}) " +
            "+ (select count(func_name) from a_func_close where func_name=#{funcName}) as count;")
    Integer canUseFunction(@Param("groupId") String groupId, @Param("funcName") String funcName);

    @Select("select func_name from a_group_func_close where group_id=#{groupId};")
    List<String> getCloseFunction(@Param("groupId") String groupId);

    @Select("CREATE TABLE IF NOT EXISTS `a_group_func_close`  (\n" +
            "        `group_id` varchar(255) NOT NULL,\n" +
            "        `func_name` varchar(255) NOT NULL " +
            "        );")
    Integer initAdminTable();

    @Select("CREATE TABLE IF NOT EXISTS `t_id`  (\n" +
            "        `id` varchar(255) NOT NULL);")
    Integer initIdTable();

    @Select("select id from t_id limit 1;")
    String selectId();

    @Select("update `t_id` set `id` = #{id}")
    Integer updateId(@Param("id") String id);

    @Select("CREATE TABLE IF NOT EXISTS `a_func_close` (`func_name` varchar(255) NOT NULL);")
    Integer initStopTable();
    @Delete("delete from a_func_close where func_name=#{funcName};")
    Integer startFunction(@Param("funcName") String funcName);

    @Insert("insert into a_func_close (func_name) values (#{funcName});")
    Integer stopFunction(@Param("funcName") String funcName);

    @Select("select func_name from a_func_close;")
    List<String> getStopFunction();
}
