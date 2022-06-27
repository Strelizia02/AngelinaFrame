package top.angelinaBot.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface EnableMapper {

    //开关某群组聊天
    @Insert({"insert into a_group_close (group_id, close)\n" +
            " VALUES (#{groupId}, 1)\n" +
            " ON CONFLICT(group_id)\n" +
            " DO update set\n" +
            " group_id = #{groupId}, close = #{close};"})
    Integer closeGroup(@Param("groupId") Long groupId, @Param("close") Integer close);

    //查询群组聊天
    @Select({"select count(group_id) from a_group_close where group_id=#{groupId} and close=#{close};"})
    Integer canUseGroup(@Param("groupId") Long groupId, @Param("close") Integer close);

}
