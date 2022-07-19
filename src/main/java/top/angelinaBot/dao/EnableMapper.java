package top.angelinaBot.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface EnableMapper {

    //开关某群组聊天，默认开启
    @Insert({"insert into a_group_close (group_id, group_close)\n" +
            " VALUES (#{groupId}, 1)\n" +
            " ON CONFLICT(group_id)\n" +
            " DO update set\n" +
            " group_id = #{groupId}, group_close = #{groupClose};"})
    Integer closeGroup(@Param("groupId") Long groupId, @Param("groupClose") Integer groupClose);

    //开关Bilibili解析，默认关闭
    @Insert({"insert into a_group_close (group_id, bilibili_close)\n" +
            " VALUES (#{groupId}, 0)\n" +
            " ON CONFLICT(group_id)\n" +
            " DO update set\n" +
            " group_id = #{groupId}, bilibili_close = #{bilibiliClose};"})
    Integer closeBilibili(@Param("groupId") Long groupId, @Param("bilibiliClose") Integer bilibiliClose);


    //查询群组聊天
    @Select({"select count(group_id) from a_group_close where group_id=#{groupId} and group_close=#{groupClose};"})
    Integer canUseGroup(@Param("groupId") Long groupId, @Param("groupClose") Integer groupClose);

    //查询Bilibili解析
    @Select({"select count(group_id) from a_group_close where group_id=#{groupId} and bilibili_close=#{bilibiliClose};"})
    Integer canUseBilibili(@Param("groupId") Long groupId, @Param("bilibiliClose") Integer bilibiliClose);

}
