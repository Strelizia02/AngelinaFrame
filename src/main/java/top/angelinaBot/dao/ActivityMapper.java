package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface ActivityMapper {

    @Insert("insert into a_activity (type) values (0);")
    Integer getGroupMessage();

    @Insert("insert into a_activity (type) values (1);")
    Integer getFriendMessage();

    @Insert("insert into a_activity (type) values (2);")
    Integer getEventMessage();

    @Insert("insert into a_activity (type) values (3);")
    Integer sendMessage();

    @Select("CREATE TABLE IF NOT EXISTS `a_activity`  (\n" +
            "        `type` int(255) NOT NULL,\n" +
            "        `time` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
            "        );")
    void initActivityTable();

    @Delete("delete from a_activity where time &lt; datetime('now','-1 day');")
    void clearActivity();
}
