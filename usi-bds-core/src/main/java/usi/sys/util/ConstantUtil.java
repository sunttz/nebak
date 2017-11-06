package usi.sys.util;

/**
 * 常量公用类
 * @author lmwang
 * 2013-9-22 下午5:45:38
 */
public class ConstantUtil {
	//用户的登陆信息标识
	public static final String AUTH_INFO = "authInfo";
	
	//新建员工默认登录密码
	public static final String DEFAULT_PASSWORD = "000000";
	//默认的密码有效期
	public static final int DEFAULT_DURATION = 30;
	//根机构父机构Id
	public static final long ORG_ROOT_PARENT = -99999L;
	
	//机构序列第一层级长度
	public static final int FIRST_LEVEL_ORG_LENGTH = 5;
	
	/**
	 * 公告的附件分组代码
	 */
	public static final String NOTICE_FILE_GROUP_CODE = "notice";
	
	//绩效管理员角色名称
	public static final String PERMADMIN = "绩效管理员";
	
	//是否过滤菜单权限
	public static final String MENUFILTER = "menuFilter";
	//数据结构(菜单对应的角色列表)名称
	public static final String MENU_ROLES = "menu_roles";
	
	//是否记录操作日志
	public static final boolean WRITELOG = Boolean.parseBoolean(ConfigUtil.getValue("writeOptLog"));
	
	public static final int PRIMARY_KEY_CACHE_COUNT = 20;
	
	//页面操作类型(用于记录系统操作日志)
	public static final String OPT_LOGIN = "登陆";
	public static final String OPT_SYS_SCENE_UPDATE = "更新业务场景";
	public static final String OPT_SYS_SCENE_DELETE = "删除业务场景";
	public static final String OPT_SYS_SCENE_INSERT = "新增业务场景";
	public static final String OPT_DICT_UPDATE = "更新字典项";
	public static final String OPT_DICT_DELETE = "删除字典项";
	public static final String OPT_DICT_INSERT = "新增字典项";
	public static final String OPT_MENU_UPDATE = "更新菜单";
	public static final String OPT_MENU_DELETE = "删除菜单";
	public static final String OPT_MENU_INSERT = "新增菜单";
	public static final String OPT_ROLE_UPDATE = "更新角色";
	public static final String OPT_ROLE_DELETE = "删除角色";
	public static final String OPT_ROLE_INSERT = "新增角色";
	public static final String OPT_ROLE_MENUS_UPDATE = "更新角色拥有的菜单";
	public static final String OPT_ROLE_STAFF_DELETE = "删除角色下人员";
	public static final String OPT_ROLE_STAFF_INSERT = "添加角色下人员";
	public static final String OPT_ORG_UPDATE = "更新机构";
	public static final String OPT_ORG_DELETE = "删除机构";
	public static final String OPT_ORG_INSERT = "新增机构";
	public static final String OPT_STAFF_UPDATE = "更新人员信息";
	public static final String OPT_STAFF_DELETE = "删除人员信息";
	public static final String OPT_STAFF_INSERT = "新增人员信息";
	public static final String OPT_STAFF_ROLES_REMOVE = "取消人员角色";
	public static final String OPT_STAFF_ROLES_ADD = "赋予人员角色";
	
	//缓存名称(业务字典缓存、脚本缓存)
	public static final String CACHE_NAME_DICT = "dictCache";
	public static final String CACHE_NAME_SCRIPT = "scriptCache";
	
	//脚本类型(xquery  groovy)
	public static final int SCRIPT_TYPE_XQUERY = 1;
	public static final int SCRIPT_TYPE_GROOVY = 2;
}
