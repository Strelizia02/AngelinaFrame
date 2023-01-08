package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BlackListMapper {

    @Insert("insert into a_blacklist (qq) values (#{qq});")
    Integer insertBlackList(String qq);

    @Delete("delete from a_blacklist where qq = #{qq};")
    Integer deleteBlackList(String qq);

    @Select("select qq from a_blacklist;")
    List<String> selectBlackList();

    @Select("select count(qq) from a_blacklist where qq = #{qq};")
    Integer selectBlackByQQ(String qq);
}
