package com.ysd.springcloud.front.common;

import com.jfinal.config.Routes;
import com.ysd.springcloud.front.index.IndexController;
import com.ysd.springcloud.front.problem.ProblemController;
import com.ysd.springcloud.front.project.doc.DocController;
import com.ysd.springcloud.front.project.event.EventController;
import com.ysd.springcloud.front.project.letter.LetterController;
import com.ysd.springcloud.front.project.newspaper.WeeklyNewspaperController;
import com.ysd.springcloud.front.project.organizer.OrganizerController;
import com.ysd.springcloud.front.project.picture.PictureController;
import com.ysd.springcloud.front.project.prop.PropController;
import com.ysd.springcloud.front.upload.UploadController;
import com.ysd.springcloud.front.vrshow.VrshowController;
import com.ysd.springcloud.front.work.WorkController;

/**
 * 前台路由
 */
public class FrontRoutes extends Routes {

	@Override
	public void config() {
		// 添加拦截器，将拦截在此方法中注册的所有 Controller
		addInterceptor(new ExceptionInterceptor());
		addInterceptor(new ProjectUserInterceptor());
		
		setBaseViewPath("/WEB-INF/views");
		
		add("/", IndexController.class);
		add("/project/prop", PropController.class, "/prop");
		add("/project/event", EventController.class, "/event");
		add("/project/letter", LetterController.class, "/letter");
		add("/project/newspaper",WeeklyNewspaperController.class,"/newspaper");
		add("/project/picture", PictureController.class, "/picture");
		add("/project/doc", DocController.class, "/document");
		add("/project/organizer", OrganizerController.class, "/organizer");
		add("/upload", UploadController.class);
		add("/problem", ProblemController.class);
		add("/vrshow", VrshowController.class);
		add("/work", WorkController.class);
	}


}
