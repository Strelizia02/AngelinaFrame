package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import top.angelinaBot.model.MessageCount;

import java.util.List;

public interface ActivityMapper {

    @Insert("insert into a_activity (type, count) values (0, 1) ON CONFLICT(type) DO update set count = count + 1;")
    Integer getGroupMessage();

    @Insert("insert into a_activity (type, count) values (1, 1) ON CONFLICT(type) DO update set count = count + 1;")
    Integer getFriendMessage();

    @Insert("insert into a_activity (type, count) values (2, 1) ON CONFLICT(type) DO update set count = count + 1;")
    Integer getEventMessage();

    @Insert("insert into a_activity (type, count) values (3, 1) ON CONFLICT(type) DO update set count = count + 1;")
    Integer sendMessage();

    @Select("CREATE TABLE IF NOT EXISTS `a_activity`  (\n" +
            "        `type` int(255) NOT NULL,\n" +
            "        `count` int(255) NOT NULL DEFAULT 0,\n" +
            "  PRIMARY KEY (\"type\"));")
    void initActivityTable();

    @Delete("delete from a_activity;")
    void clearActivity();

    @Select("select type, count from a_activity")
    List<MessageCount> selectCount();
}
