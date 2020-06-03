package com.ysd.overview.api.common;

import com.jfinal.config.Routes;
import com.ysd.overview.api.auth.AuthController;
import com.ysd.overview.api.auth.AuthInterceptor;
import com.ysd.overview.api.doc.DocController;
import com.ysd.overview.api.model.ModelController;
import com.ysd.overview.api.problem.ProblemApiController;
import com.ysd.overview.api.project.ProjectApiController;
import com.ysd.overview.api.schedule.ScheduleController;
import com.ysd.overview.api.weather.WeatherApiController;
import com.ysd.overview.api.work.WorkApiController;

/**
 * api路由
 */
public class ApiRoutes extends Routes {

	@Override
	public void config() {
		// 添加api拦截器，将拦截在此方法中注册的所有 Controller
		addInterceptor(new ExceptionInterceptor());
		addInterceptor(new CrossDomainInterceptor());
		addInterceptor(new AuthInterceptor());
		
		add("/api/auth", AuthController.class);
		add("/api/project", ProjectApiController.class);
		add("/api/problem", ProblemApiController.class);
		add("/api/work", WorkApiController.class);
		add("/api/weather", WeatherApiController.class);
		add("/api/schedule", ScheduleController.class);
		add("/api/doc", DocController.class);
		add("/api/model",ModelController.class);
	}

}
