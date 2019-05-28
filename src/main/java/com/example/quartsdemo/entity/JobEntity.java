package com.example.quartsdemo.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "JOB_ENTITY")
@Data
public class JobEntity implements Serializable {
    private static final long serialVersionUID = -5974699134574143016L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;          //job名称
    @Column(name = "`group`", nullable = true)
    private String group;         //job组名
    private String cron;          //执行的cron
    private String parameter;     //job的参数
    private String description;   //job描述信息
    @Column(name = "vm_param")
    private String vmParam;       //vm参数
    @Column(name = "jar_path")
    private String jarPath;       //job的jar路径,在这里我选择的是定时执行一些可执行的jar包
    private String status;        //job的执行状态,这里我设置为OPEN/CLOSE且只有该值为OPEN才会执行该Job

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "`group`", nullable = true)
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVmParam() {
        return vmParam;
    }

    public void setVmParam(String vmParam) {
        this.vmParam = vmParam;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    /**
 * 新增Builder模式,可选,选择设置任意属性初始化对象
 */
//    public JobEntity(Builder builder) {
//        id = builder.id;
//        name = builder.name;
//        group = builder.group;
//        cron = builder.cron;
//        parameter = builder.parameter;
//        description = builder.description;
//        vmParam = builder.vmParam;
//        jarPath = builder.jarPath;
//        status = builder.status;
//    }
//    public static class Builder {
//        private Integer id;
//        private String name = "";          //job名称
//        private String group = "";         //job组名
//        private String cron = "";          //执行的cron
//        private String parameter = "";     //job的参数
//        private String description = "";   //job描述信息
//        private String vmParam = "";       //vm参数
//        private String jarPath = "";       //job的jar路径
//        private String status = "";        //job的执行状态,只有该值为OPEN才会执行该Job
//    }


}
