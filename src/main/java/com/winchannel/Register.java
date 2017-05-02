package com.winchannel;


/*
@Service("register") 
public class Register implements InitializingBean, ApplicationContextAware {
	private Map<String, DemoService> serviceImplMap = new HashMap<String, DemoService>(); 
    private ApplicationContext applicationContext; 
  
    // 获取spring的上下文 
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException { 
        this.applicationContext = applicationContext; 
    } 
  
    // 获取接口实现类的所有bean，并按自己定的规则放入map中 
    @Override
    public void afterPropertiesSet() throws Exception { 
        Map<String, DemoService> beanMap = applicationContext.getBeansOfType(DemoService.class); 
        // 以下代码是将bean按照自己定的规则放入map中，这里我的规则是key：service.toString();value:bean 
        // 调用时，参数传入service.toString()的具体字符串就能获取到相应的bean 
        // 此处也可以不做以下的操作，直接使用beanMap,在调用时，传入bean的名称 
        for (DemoService serviceImpl : beanMap.values()) { 
            serviceImplMap.put(serviceImpl.toString(), serviceImpl); 
        } 
    } 
      
    public DemoService getServiceImpl(String name) { 
        return serviceImplMap.get(name); 
    } 
}
*/
