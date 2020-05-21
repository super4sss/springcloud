
package com.ysd.springcloud.kit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import com.jfinal.plugin.activerecord.Model;

/**
 * 使用 Jsoup 对 html 进行过滤
 */
public class JsoupKit {
	
	/**
	 * 用于过滤 content 字段的白名单，需要允许比较多的 tag
	 */
	private static final Whitelist contentWhitelist = createContentWhitelist();
	private static final Document.OutputSettings notPrettyPrint = new Document.OutputSettings().prettyPrint(false);

	private static Whitelist createContentWhitelist() {
		return  Whitelist.relaxed()
		/**
		 * 必须要删除应用在 a 与 img 上的 protocols，否则就只有使用了这些 protocol 的才不被过滤，
		 * 在 remove 的时候，后面的 protocols 要完全一个不露的对应上 jsoup 默认已经添加的，否则仍然会被过滤掉
		 * 在升级 jsoup 后需要测试这 a 与 img 的过滤是否正常
		 */
		.removeProtocols("a", "href", "ftp", "http", "https", "mailto")
		.removeProtocols("img", "src", "http", "https")

		.addAttributes("a", "href", "title", "target")  // 官方默认会将 target 给过滤掉

		/**
		 * 在 Whitelist.relaxed() 之外添加额外的白名单规则
         */
		.addTags("div", "span", "embed", "object", "param")
		.addAttributes(":all", "style", "class", "id", "name")
		.addAttributes("object", "width", "height", "classid", "codebase")
		.addAttributes("param", "name", "value")
		.addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen", "allowScriptAccess", "flashvars", "name", "type", "pluginspage");
	}

	/**
	 * 过滤 model 中的 title 与 content 字段，其中 title 过滤为纯 text
	 * content 使用 contentWhitelist 过滤
	 */
	@SuppressWarnings("rawtypes")
	public static void filterTitleAndContent(Model m) {
		String title = m.getStr("title");
		if (title != null) {
			m.set("title", getText(title));
		}
		String content = m.getStr("content");
		if (content != null) {
			m.set("content", filterContent(content));
		}
	}
	
	/**
	 * 获取 html 中的纯文本信息，过滤所有 tag
 	 */
	public static String getText(String html) {
		return html != null ? Jsoup.clean(html, Whitelist.none()) : null;
	}
	
	/**
	 * 对 content 字段过滤
	 */
	public static String filterContent(String content) {
		// return content != null ? Jsoup.clean(content, contentWhitelist) : null;
		// 添加 notPrettyPrint 参数，避免重新格式化，主要是不会在超链前面添加 "\n"
		return content != null ? Jsoup.clean(content, "", contentWhitelist, notPrettyPrint) : null;
	}
	
}
