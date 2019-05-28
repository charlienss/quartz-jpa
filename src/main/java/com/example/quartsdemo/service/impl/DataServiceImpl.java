package com.example.quartsdemo.service.impl;

import com.example.quartsdemo.entity.JobEntity;
import com.example.quartsdemo.entity.ResultBody;
import com.example.quartsdemo.repository.JobEntityRepository;
import com.example.quartsdemo.service.DataService;
import com.example.quartsdemo.service.QuartzService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private JobEntityRepository repository;

    @Autowired
    private QuartzService jobService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public ResultBody getAll() {


//        List<JobEntity> jobEntityList = ;
//
//        int count = jobEntityList.size();
//
//        System.out.println("count==>"+count);
//
//        ResultBody resultBody = new ResultBody(200,"",count,jobEntityList);

        return getAllByPage(1, 10);
    }


    @Override
    public void save(JobEntity job) {
        JobEntity save = repository.save(job);
    }

    /**
     *  Specification<Model> sp = new Specification<Model>() {
     *             @Override
     *             public Predicate toPredicate(Root<Model> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
     *
     *                 List<Predicate> list = new ArrayList<Predicate>();
     * //                Path<String> platform = root.get("platformName");
     * //                Path<Integer> app = root.get("appId");
     * //                Path<String> type = root.get("modelType");
     * //                Path<String> name = root.get("modelName");
     * //                if(StringUtils.isNotBlank(platformName)) {
     * //                    list.add(cb.equal(platform, platformName));
     * //                }
     * //                if(StringUtils.isNotBlank(modelType)) {
     * //                    list.add(cb.equal(type, modelType));
     * //                }
     * //                if(StringUtils.isNotBlank(String.valueOf(appId))) {
     * //                    list.add(cb.equal(app, appId));
     * //                }
     * //                list.add(cb.like(name, "%"+modelName+"%"));
     * //
     * //                query.orderBy(cb.desc(root.get("modelName")));
     *
     * ////分页信息（从0开始）
     * //                Pageable pageable = new PageRequest(pageNum-1,pageSize);
     * //// 分页查询（sp就是Specification）
     * //                Page<Account> page = accountDao.findAll(sp,pageable);
     * //// 总条数
     * //                long totalElements = page.getTotalElements();
     * //// list集合
     * //                List<Account> content = page.getContent();
     *
     *                 PageRequest of = PageRequest.of(page - 1, limit, null);
     *                 query.where(list.toArray(new Predicate[list.size()]));
     *             }
     *
     *         };
     */

    /**
     * 分页查询数据的实现
     *
     * @param page
     * @param limit
     * @return
     */
    @Override
    public ResultBody getAllByPage(int page, int limit) {
        Specification<Model> sp = new Specification<Model>() {
            @Override
            public Predicate toPredicate(Root<Model> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> list = new ArrayList<Predicate>();

                CriteriaQuery<?> where = query.where(list.toArray(new Predicate[list.size()]));
                return where.getRestriction();
            }
        };
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<JobEntity> pageResult = repository.findAll(sp, pageable);

        long totalElements = pageResult.getTotalElements();
        List<JobEntity> content = pageResult.getContent();

        ResultBody resultBody = new ResultBody(200, "", totalElements, content);

        return resultBody;
    }

    /**
     * 这里的0表示success
     * 1表示faile
     *
     * @param start
     * @param id
     * @return
     */
    @Override
    public String updStatus(boolean start, int id) {

        String str = "";
        //先获得这个id的obj
        JobEntity job = repository.getById(id);
        if (start == true) {
            str = "OPEN";
        } else {
            str = "CLOSE";
        }
        job.setStatus(str);

        JobEntity save = repository.save(job);
        /**
         * 这里要把原来的job实例给停止 重新加载
         */
        String result;
        JobEntity entity = jobService.getJobEntityById(id);
        synchronized (this) {
            try {
            JobKey jobKey = jobService.getJobKey(entity);
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseJob(jobKey);
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
            scheduler.deleteJob(jobKey);
            JobDataMap map = jobService.getJobDataMap(entity);
            JobDetail jobDetail = jobService.geJobDetail(jobKey, entity.getDescription(), map);
                if (entity.getStatus().equals("OPEN")) {
                    scheduler.scheduleJob(jobDetail, jobService.getTrigger(entity));
                    result = "Refresh Job : " + entity.getName() + "\t jarPath: " + entity.getJarPath() + " success !";
                } else {
                    System.out.println("进来关闭了啊!!");
//                    result = "Refresh Job : " + entity.getName() + "\t jarPath: " + entity.getJarPath() + " failed ! , " +
//                            "Because the Job status is " + entity.getStatus();
//                    scheduler.unscheduleJob(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
//                    scheduler.deleteJob(jobKey);

//                    Thread.currentThread().stop();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        String status = save.getStatus();
        return status;

    }

    @Override
    public JobEntity getUpdData(int id) {

        JobEntity jobEntity = repository.getById(id);
        return jobEntity;
    }

    public void delJob(int id) {

        JobEntity job = repository.getById(id);

        repository.delete(job);
    }

    //修改
    @Override
    public void updData(JobEntity job) {


        if (job != null) {
            JobEntity jobEntity = repository.getById(job.getId());

            System.out.println("old-->" + jobEntity);

            jobEntity.setName(job.getName());
            jobEntity.setGroup(job.getGroup());
            jobEntity.setCron(job.getCron());
            jobEntity.setParameter(job.getParameter());
            jobEntity.setDescription(job.getDescription());
            jobEntity.setVmParam(job.getVmParam());
            jobEntity.setJarPath(job.getJarPath());
            jobEntity.setStatus(job.getStatus());

            System.out.println("new -->" + jobEntity);

            repository.save(jobEntity);

//            System.out.println(job);
//            repository.updateJob(job);

        }

    }
}
