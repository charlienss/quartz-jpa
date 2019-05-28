package com.example.quartsdemo.job;

import com.example.quartsdemo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
