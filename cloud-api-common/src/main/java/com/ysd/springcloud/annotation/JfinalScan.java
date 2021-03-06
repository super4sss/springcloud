//package com.ysd.springcloud.annotation;
//
//import com.ysd.springcloud.config.JfinalControlScannerRegistrar;
//import org.springframework.beans.factory.support.BeanNameGenerator;
//import org.springframework.context.annotation.Import;
//
//import java.lang.annotation.*;
//
///**
// * 在指定包下扫面标志类,将其加载到Spring上下文中
// * @author chenmin
// */
//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.TYPE)
//@Documented
//@Import(JfinalControlScannerRegistrar.class)
//public @interface JfinalScan {
//
//    /**
//     * 包扫描路径(不填时从当前路径下扫描)
//     * @return
//     */
//    String[] basePackages() default {};
//
//    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
//
//    Class<? extends Annotation> annotationClass() default Annotation.class;
//    /**
//     * 标识类
//     * @return
//     */
//    Class<?>[] markerInterfaces() default {};
//
//}
