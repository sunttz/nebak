package usi.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.biz.dao.NeServerModuleDao;
import usi.biz.entity.NeServerModule;
import usi.sys.dto.PageObj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NeServerModuleService {

    @Autowired
    NeServerModuleDao neServerModuleDao;

    /**
     * 模块列表（分页）
     * @param pageObj
     * @return
     */
    public Map<String,Object> getPageAllModule(PageObj pageObj, String neServerModuleId){
        Map<String,Object> map = new HashMap<String, Object>();
        List<NeServerModule> modules = neServerModuleDao.getPageAllModule(pageObj, neServerModuleId);
        map.put("total", pageObj.getTotal());
        map.put("rows", modules);
        return map;
    }

    /**
     * 新增模块
     * @param neServerModule
     * @return
     */
    public int saveNeServerModule(NeServerModule neServerModule){
        return neServerModuleDao.saveNeServerModule(neServerModule);
    }

    /**
     * 更新模块
     * @param neServerModule
     * @return
     */
    public int updateNeServerModule(NeServerModule neServerModule){
        return neServerModuleDao.updateNeServerModule(neServerModule);
    }

    /**
     * 删除模块
     * @param moduleId
     * @return
     */
    public int deleteNeServerModule(Long moduleId){
        return neServerModuleDao.deleteNeServerModule(moduleId);
    }

}
