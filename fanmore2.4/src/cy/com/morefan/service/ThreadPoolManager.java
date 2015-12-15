package cy.com.morefan.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadPoolManager {

	private ExecutorService service;
	
	private ThreadPoolManager(){
		//Java虚拟机的可用的处理器数量
		int num = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(num*5);
	}
	
	private static ThreadPoolManager manager;
	
	
	public static ThreadPoolManager getInstance(){
		if(manager==null)
		{
			manager= new ThreadPoolManager();
		}
		return manager;
	}
	
	public void addTask(Runnable runnable){
		service.submit(runnable);
	}
	public void stop(){
		service.shutdown();
		//service.shutdownNow();
	}
	

}
