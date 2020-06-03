package com.ysd.overview.front.common;

import com.jfinal.config.Routes;
import com.ysd.overview.front.index.IndexController;
import com.ysd.overview.front.problem.ProblemController;
import com.ysd.overview.front.project.doc.DocController;
import com.ysd.overview.front.project.event.EventController;
import com.ysd.overview.front.project.letter.LetterController;
import com.ysd.overview.front.project.newspaper.WeeklyNewspaperController;
import com.ysd.overview.front.project.organizer.OrganizerController;
import com.ysd.overview.front.project.picture.PictureController;
import com.ysd.overview.front.project.prop.PropController;
import com.ysd.overview.front.upload.UploadController;
import com.ysd.overview.front.vrshow.VrshowController;
import com.ysd.overview.front.work.WorkController;

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
