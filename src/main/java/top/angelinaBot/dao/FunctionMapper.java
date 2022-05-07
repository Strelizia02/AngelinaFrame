package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface FunctionMapper {
    @Insert("insert into a_funcCount (func, count) values (#{funcName}, 1) ON CONFLICT(func) DO update set count = count + 1")
    Integer insertFunction(String funcName);

    @Select("CREATE TABLE IF NOT EXISTS `a_funcCount`  (\n" +
            "        `func` varchar(255) NOT NULL,\n" +
            "        `count` int(255)\n" +
            "        );")
    void initFunctionTable();
}
