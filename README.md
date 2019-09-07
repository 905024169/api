###  boot通用工程使用说明

项目技术：SpringBoot+Spring+SpringMvc+Spring Security OAth2+Mybatis Plus+Lombok+Redis 

特点：

创建表 生成实体 即可url CRUD 不再写controller service等  

基于MybatisPlus最新版本  反射资源请求封装   

MP代码生成器，已调整为当前项目配置  


例子：  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0904/121541_03ff0554_1165160.png "屏幕截图.png")
  
http://ip:端口/项目名/api/表简写?属性拼接 请花5分阅读如下


使用约定：

- 表需要前缀  例如  t_user   如果需要其他前缀 自行去修改CODE生成器
- 表字段必须全部小写(反驼峰格式小写)  例如 user_name
- 表字段不允许与Mysql关键字冲突   例如 desc、select
- 接口请求参数为驼峰


使用方式     /api/表名简写(自动映射关联)  例如t_user 则为user 其他前缀需要单独配置   

普通查询直接  /api/表名简写?属性=值 如果需要分页带上&page=值&size=值  

复杂查询请认真阅读如下规则

condition为复杂参数 例如like左右全模糊匹配,与,或混合 、条件查询  

condition=() 括号内写表达式 如最上  

;是或者  ,是并且 （条件运算）

condition表达式 如图  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/200300_c682b01d_1165160.png "屏幕截图.png")


基础表达式

- orderDesc=ctime   倒序字段
- orderAsc=ctime    正序字段
- groupBy=num     分组字段 分组条件,需要配合函数使用,暂时没用



生成代码时候 输入关键字 相当于为整体代码创建一个目录    

例如business 所有的CRUD业务代码包含文件夹会创建于business内   


![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194706_ab8924d3_1165160.png "屏幕截图.png")


代码生成案例：  

先删除生成的JAVA代码和对应的Mybatis Xml   

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194716_2008376e_1165160.png "屏幕截图.png")

CodeGenerator文件右键运行 输入模块名称   执行即可  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/195652_c4c9e7cc_1165160.png "屏幕截图.png")

Application右键运行 整个项目   

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194900_155f539f_1165160.png "屏幕截图.png")  



### 请求实例：遵守Resful规范  本文下列图是旧版   新版请务必在请求的路径前面加入/api  

首先获取Token  

密码方式：http://localhost:9100/oauth/token?username=admin&password=admin&grant_type=password&scope=select&client_id=client_2&client_secret=123456

客户端方式：http://localhost:9100/oauth/token?grant_type=client_credentials&scope=select&client_id=client_1&client_secret=123456

得到Token访问接口需要添加上

1.要么加入到Url 例如：http://localhost:9100/api/user?username=admin&page=1&size=1&orderDesc=id&OTM=false&access_token=89a212d2-562d-4b77-a603-7d9ad872788e

2.要么加入请求头 如下图

![输入图片说明](https://images.gitee.com/uploads/images/2019/0904/183538_e1052be9_1165160.png "屏幕截图.png")
 



GET------------------->请求全部列表,包含分页  


![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194911_6d8bfc5e_1165160.png "屏幕截图.png")
</br>



GET------------------->根据ID查询数据  


![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194931_065ad214_1165160.png "屏幕截图.png")




POST------------------->新增或者更新（同一接口） 有ID则更新，无ID则保存  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194944_09d4bca3_1165160.png "屏幕截图.png")




PUT------------------->根据ID更新数据  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194954_f2fc9702_1165160.png "屏幕截图.png")




DELETE------------------->根据ID删除信息  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/194958_a4b65015_1165160.png "屏幕截图.png")




DELETE------------------->根据ID批量删除数据  

路径特殊注意：/表名/batch(固定值)/id集合  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0830/195004_c1bc931b_1165160.png "屏幕截图.png")


### 最新加入关联查询（尚不稳定)  
 
支持开关控制、 &OTM=true 代表开启 关联查询  
请加入OneToManyBoot注解  

![输入图片说明](https://images.gitee.com/uploads/images/2019/0901/120954_26f0e906_1165160.png "屏幕截图.png")  

实际效果 ref为关联结果集

![输入图片说明](https://images.gitee.com/uploads/images/2019/0901/121206_ff1b5a59_1165160.png "屏幕截图.png")

后续增加更强大功能
