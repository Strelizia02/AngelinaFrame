package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import top.angelinaBot.model.FunctionCount;

import java.util.List;

public interface FunctionMapper {
    @Insert("insert into a_funcCount (func, count) values (#{funcName}, 1) ON CONFLICT(func) DO update set count = count + 1")
    Integer insertFunction(String funcName);

    @Select("select func as name, count from a_funcCount")
    List<FunctionCount> selectFunction();

    @Select("CREATE TABLE IF NOT EXISTS `a_funcCount`  (\n" +
            "        `func` varchar(255) NOT NULL,\n" +
            "        `count` int(255),\n" +
            "        PRIMARY KEY (\"func\"));")
    void initFunctionTable();

    @Delete("delete from a_funcCount")
    void deleteFunctionTable();
}
