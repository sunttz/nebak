package usi.biz.dao;

import usi.biz.entity.NeServerModule;
import usi.sys.dto.PageObj;

import java.util.List;

public interface NeServerModuleDao {

    /**
     * 模块列表（分页）
     * @param pageObj
     * @param neServerModuleId
     * @return
     */
    List<NeServerModule> getPageAllModule(PageObj pageObj, String neServerModuleId);

    /**
     * 模块列表（不分页）
     * @param neServerModuleId
     * @return
     */
    List<NeServerModule> getAllModule(String neServerModuleId);

    /**
     * 新增模块
     * @param neServerModule
     * @return
     */
    int saveNeServerModule(NeServerModule neServerModule);

    /**
     * 更新模块
     * @param neServerModule
     * @return
     */
    int updateNeServerModule(NeServerModule neServerModule);

    /**
     * 删除模块
     * @param moduleId
     * @return
     */
    int deleteNeServerModule(Long moduleId);

    /**
     * 查询模块数量
     * @param neServerModuleId
     * @return
     */
    String getModuleNum(String neServerModuleId);
}
