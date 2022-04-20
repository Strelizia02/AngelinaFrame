package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminMapper {

    Integer closeFunction(@Param("groupId") Long groupId, @Param("funcName") String funcName);

    Integer openFunction(@Param("groupId") Long groupId, @Param("funcName") String funcName);

    Integer canUseFunction(@Param("groupId") Long groupId, @Param("funcName") String funcName);

    List<String> getCloseFunction(@Param("groupId") Long groupId);

    Integer initFunctionTable();
}
