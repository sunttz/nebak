package usi.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import usi.biz.entity.NeServerModule;
import usi.biz.service.NeServerModuleService;
import usi.sys.dto.PageObj;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/neServerModule")
public class NeServerModuleController {

    @Autowired
    NeServerModuleService neServerModuleService;

    /**
     * 网元模块配置页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/neServerModuleConfig.do", method = RequestMethod.GET)
    public String neServerModuleConfig(Model model){
        return "ne_server/neServerModuleConfig";
    }

    /**
     * 生成关联ID
     * @param model
     * @return
     */
    @RequestMapping(value = "/neServerModuleConfigId.do", method = RequestMethod.GET)
    @ResponseBody
    public String neServerModuleConfigId(Model model){
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
    }

    /**
     * 查询所有模块信息（分页）
     * @param getAllJob
     * @return
     */
    @RequestMapping(value = "/getPageAllModule.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getPageAllModule(PageObj getAllJob,String neServerModuleId){
        return neServerModuleService.getPageAllModule(getAllJob, neServerModuleId);
    }

    /**
     * 添加网元模块
     * @param neServerModule
     * @return
     */
    @RequestMapping(value = "/addNeserverModule.do", method = RequestMethod.POST)
    @ResponseBody
    public boolean addNeserverModule(NeServerModule neServerModule){
        boolean flag = false;
        try {
            int result = -1;
            Long moduleId = neServerModule.getModuleId();
            if(moduleId != null){
                result = neServerModuleService.updateNeServerModule(neServerModule);
            }else{
                result = neServerModuleService.saveNeServerModule(neServerModule);
            }
            if(result == 1){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除网元模块
     * @param moduleId
     * @return
     */
    @RequestMapping(value = "/deleteNeserverModule.do", method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteNeserverModule(Long moduleId){
        boolean flag = false;
        try {
            int result = neServerModuleService.deleteNeServerModule(moduleId);
            if(result == 1){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询模块数量
     * @param neServerModuleId
     * @return
     */
    @RequestMapping(value = "/getModuleNum.do", method = RequestMethod.POST)
    @ResponseBody
    public String getModuleNum(String neServerModuleId){
        String moduleNum = "";
        if(neServerModuleId != null && !"".equals(neServerModuleId)){
            moduleNum = neServerModuleService.getModuleNum(neServerModuleId);
        }
        return moduleNum;
    }

}
