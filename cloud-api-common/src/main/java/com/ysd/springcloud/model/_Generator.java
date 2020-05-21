package com.ysd.springcloud.model;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;
import com.ysd.springcloud.kit.NetKit;

import javax.sql.DataSource;

/**
 * Model、BaseModel、_MappingKit 生成器
 */
public class _Generator {

	/**
	 * 部分功能使用 Db + Record 模式实现，无需生成 model 的 table 在此配置
	 */
	private static String[] excludedTable = {
			"bus_plan_user","bus_plan_count","bus_task_exec"
	};

	/**
	 * 重用 AppConfig 中的数据源配置，避免冗余配置
	 */
	public static DataSource getDataSource() {

//		PropKit.use("config.properties");
//		DruidPlugin druidPlugin =new DruidPlugin("jdbc:mysql://localhost:3306/db2019?serverTimezone=UTC&useSSL=false&autoReconnect=true&tinyInt1isBit=false&useUnicode=true&characterEncoding=utf8","root","root");
		DruidPlugin druidPlugin =new DruidPlugin("jdbc:mysql://localhost:9253/ysd_overview?serverTimezone=UTC&useSSL=false&autoReconnect=true&tinyInt1isBit=false&useUnicode=true&characterEncoding=utf8","bim","bim@prod1&2019$");
//		DruidPlugin druidPlugin =new DruidPlugin("jdbc:mysql://192.168.78.247:3306/ysd_overview?serverTimezone=UTC&useSSL=false&autoReconnect=true&tinyInt1isBit=false&useUnicode=true&characterEncoding=utf8","root","root-2020");
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}

	public static void main(String[] args) {
    NetKit kit= new NetKit();
    try {
      kit.SSh();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // base model 所使用的包名
		String baseModelPackageName = "com.ysd.springcloud.model.base";
		// base model 文件保存路径
//		String baseModelOutputDir = PathKit.getWebRootPath() + "/temp/base";
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/ysd/springcloud//model/base";
    System.out.println("classpath:"+PathKit.getWebRootPath());
    System.out.println("111111111111111111"+baseModelOutputDir);
		System.out.println("输出路径："+ baseModelOutputDir);

		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.ysd.springcloud.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";

		// 创建生成器
		Generator gen = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 设置数据库方言
		gen.setDialect(new MysqlDialect());
		// 添加不需要生成的表名
		for (String table : excludedTable) {
			gen.addExcludedTable(table);
		}
		// 设置是否在 Model 中生成 dao 对象
		gen.setGenerateDaoInModel(false);
		// 设置是否生成字典文件
		gen.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		gen.setRemovedTableNamePrefixes("bus_");
		// 生成
		gen.generate();

		kit.close();
	}
}
