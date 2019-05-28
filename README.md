# Quartz定时调度jar包的执行Demo

#### 1.Quartz简介

​	Quartz框架的核心是调度器。调度器负责管理Quartz应用运行时环境。调度器不是靠自己做所有的工作，而是依赖框架内一些非常重要的部件。Quartz不仅仅是线程和线程管理。为确保可伸缩性，Quartz采用了基于多线程的架构。启动时，框架初始化一套worker线程，这套线程被调度器用来执行预定的作业。这就是Quartz怎样能并发运行多个作业的原理。Quartz依赖一套松耦合的线程池管理部件来管理线程环境。

####  2.项目相关

​	该定时器Demo用于定时执行制定路径下的jar包的编译,也可以用于普通的任务调度.通过对任务的查询修改删除来管理整个列表文件.可以通关开启和关闭来更改jar的开始执行状态(开启后如果需要停止只能关闭或重启服务器才能生效,具体的解决办法还在改进中).相关的一整套的UI界面比较简略,可以进行二次开发,主要整合了JPA的分页查询,可以查看相关代码部分.

![test.gif](https://upload-images.jianshu.io/upload_images/12057079-45628ea79747f5e4.gif?imageMogr2/auto-orient/strip)

##### 3.测试路径

```java
//主页路径:
http://localhost:9090/
//启动jar的路径(这里的我只是建了一个简单的springboot的helloword的Jar包,相关jar包在项目中可以找到)
http://localhost:8088/sayHello
```

##### 4.代码相关

##### QuartzServiceImpl:

```java
@Service
public class QuartzServiceImpl implements QuartzService {

    @Autowired
    private JobEntityRepository repository;

    @Override
    public JobEntity getById(int id) {
        return repository.getById(id);
    }

    //通过Id获取Job
    public JobEntity getJobEntityById(Integer id) {
        return repository.getById(id);
    }
    //从数据库中加载获取到所有Job
    public List<JobEntity> loadJobs() {
        List<JobEntity> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }
    //获取JobDataMap.(Job参数对象)
    public JobDataMap getJobDataMap(JobEntity job) {
        JobDataMap map = new JobDataMap();
        map.put("name", job.getName());
        map.put("group", job.getGroup());
        map.put("cronExpression", job.getCron());
        map.put("parameter", job.getParameter());
        map.put("JobDescription", job.getDescription());
        map.put("vmParam", job.getVmParam());
        map.put("jarPath", job.getJarPath());
        map.put("status", job.getStatus());
        return map;
    }
    //获取JobDetail,JobDetail是任务的定义,而Job是任务的执行逻辑,JobDetail里会引用一个Job Class来定义
    public JobDetail geJobDetail(JobKey jobKey, String description, JobDataMap map) {
        return JobBuilder.newJob(DynamicJob.class)
                .withIdentity(jobKey)
                .withDescription(description)
                .setJobData(map)
                .storeDurably()
                .build();
    }
    //获取Trigger (Job的触发器,执行规则)
    public Trigger getTrigger(JobEntity job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(job.getName(), job.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                .build();
    }
    //获取JobKey,包含Name和Group
    public JobKey getJobKey(JobEntity job) {
        return JobKey.jobKey(job.getName(), job.getGroup());
    }
}
```

##### DynamicJob:

```java
/**
 * :@DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行.
 * :注意org.quartz.threadPool.threadCount线程池中线程的数量至少要多个,否则@DisallowConcurrentExecution不生效
 * :假如Job的设置时间间隔为3秒,但Job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution //没有异常就更新数据 可用?
@Component
@Slf4j
public class DynamicJob implements Job {
    private Logger logger = LoggerFactory.getLogger(DynamicJob.class);

    /**
     * 核心方法,Quartz Job真正的执行逻辑.
     *   executorContext JobExecutionContext中封装有Quartz运行所需要的所有信息
     * @throws JobExecutionException execute()方法只允许抛出JobExecutionException异常
     */
    @Override
    public void execute(JobExecutionContext executionContext) throws JobExecutionException {

        JobDataMap map = executionContext.getMergedJobDataMap();

        String jarPath = map.getString("jarPath");
        String parameter = map.getString("parameter");
        String vmParam = map.getString("vmParam");
        logger.info("Running Job name : {} ", map.getString("name"));
        logger.info("Running Job description : " + map.getString("JobDescription"));
        logger.info("Running Job group: {} ", map.getString("group"));
        logger.info("Running Job cron : " + map.getString("cronExpression"));
        logger.info("Running Job jar path : {} ", jarPath);
        logger.info("Running Job parameter : {} ", parameter);
        logger.info("Running Job vmParam : {} ", vmParam);

        long startTime = System.currentTimeMillis();
        if (!StringUtils.getStringUtil.isEmpty(jarPath)) {

            File jar = new File(jarPath);
            if (jar.exists()) {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(jar.getParentFile());
                /**
                 * 这个是java的执行命令
                 * java -jar
                 */
                List<String> commands = new ArrayList<>();
                commands.add("java");
                if (!StringUtils.getStringUtil.isEmpty(vmParam)) {
                    commands.add(vmParam);
                }
                commands.add("-jar");
                commands.add(jarPath);
                commands.add(" &");
                System.out.println("commands->\n"+commands);

                if (!StringUtils.getStringUtil.isEmpty(parameter)) {

                    commands.add(parameter);
                    processBuilder.command(commands);
                    logger.info("Running Job details as follows >>>>>>>>>>>>>>>>>>>>: ");
                    logger.info("Running Job commands : {}  ", StringUtils.getStringUtil.getListString(commands));
                    try {
                        Process process = processBuilder.start();
                        logProcess(process.getInputStream(), process.getErrorStream());

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                } else {

                    throw new JobExecutionException("Job Jar not found >>  " + jarPath);
                }

                long endTime = System.currentTimeMillis();
                logger.info(">>>>>>>>>>>>> Running Job has been completed , cost time :  " + (endTime - startTime) + "ms\n");
            }


        }


    }

    //打印Job执行内容的日志
    private void logProcess(InputStream inputStream, InputStream errorStream) throws IOException {
        String inputLine;
        String errorLine;
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        while ((inputLine = inputReader.readLine()) != null){
            logger.info(inputLine);
        }
        while ((errorLine = errorReader.readLine()) != null) {
            logger.error(errorLine);
        }
    }

}

```

