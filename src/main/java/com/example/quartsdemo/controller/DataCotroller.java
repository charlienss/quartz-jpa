package com.example.quartsdemo.controller;

import com.example.quartsdemo.entity.JobEntity;
import com.example.quartsdemo.entity.ResultBody;
import com.example.quartsdemo.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 这里添加相关的任务的控制
 */
@RestController
@RequestMapping("/data")
@Slf4j
public class DataCotroller {

    @Autowired
    private DataService dataService;

    /**
     * 规定的返回格式
     * {
     * "code": 200,
     * "msg": "",
     * "count": 10,
     * "data": [{}, {}]
     * }
     * 这里请求的时候默认的带有?page=1&limit=10
     *
     * @return
     */
    @RequestMapping("/jobList")
    public ResultBody jobList(int page, int limit) {
        log.warn("page->" + page + "limit->" + limit);
        ResultBody all = dataService.getAllByPage(page, limit);
        return all;
    }

    /**
     * 增加
     */
    @RequestMapping("/addJob")
    public ResultBody addJob(JobEntity job) {
        log.info("job==>"+job);
        dataService.save(job);
        ResultBody all = dataService.getAll();
        return all;
    }

    /**
     * 更改相关的开启的状态
     */
    @RequestMapping("/updStatus")
    public String updStatus(boolean start, int id) {
        log.info("start==>" + start + "id==>" + id);
        String status = dataService.updStatus(start, id);
        return status;
    }

    /**
     * 返回要更新的数据
     */
    @RequestMapping("/getUpdData")
    public JobEntity getUpdData(int id) {
        JobEntity jobEntity = dataService.getUpdData(id);
        return jobEntity;
    }

    /**
     * 修改为新的内容
     *
     */
    @RequestMapping("/updData")
    public ResultBody updData(JobEntity job) {
        dataService.updData(job);
        ResultBody all = dataService.getAll();
        return all;
    }


    /**
     * 删除数据
     */
    @RequestMapping("/delJob")
    public void delJob(int id) {
         dataService.delJob(id);
    }

}
