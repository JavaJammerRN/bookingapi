package com.webservice;


import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.classmate.Filter;

//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
@SpringBootApplication
public class App extends SpringBootServletInitializer{
	

	 
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	
	@Bean
	public WebMvcConfigurer corsConfigurer(){
		return new WebMvcConfigurer(){
			
			@Override
			public void addCorsMappings(CorsRegistry registry){
				registry.addMapping("booking/*").allowedOrigins("*");
			}

			@Override
			public void addFormatters(FormatterRegistry registry) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Validator getValidator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void configurePathMatch(PathMatchConfigurer configurer) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public MessageCodesResolver getMessageCodesResolver() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void configureViewResolvers(ViewResolverRegistry registry) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(applicationClass);
//    }
//
//    private static Class<App> applicationClass = App.class;
	
}


