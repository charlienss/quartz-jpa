# Quarts的相关内容

```java
文档参考地址:
https://www.w3cschool.cn/quartz_doc/quartz_doc-1xbu2clr.html
```

###  第一章 Quarts的Demo

#### 1.引入依赖

注意:这里引如依赖要注意版本的问题,如果引如过低的版本,如1.8的可能有些类找不到

```java
<!-- https://mvnrepository.com/artifact/org.quartz-scheduler/quartz -->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>2.3.0</version>
        </dependency>
```

#### 2.相关类

\HelloJob

```java
//这个是job的具体内容
public class HelloJob   implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("job1执行了");

    }
}

```

\QuartzTest

```java
public class QuartzTest {

    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        // 使用我们定义的HelloJob进行相关的调用
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity("job1", "group1")
                .build();

        //触发器的配置

        /**
         * 配置相关的触发器
         */
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInSeconds(2)
                                .repeatForever()
                ).build();

        // 最后让我们的job按照触发器的配置进行运作
        scheduler.scheduleJob(job, trigger);
        // scheduler.shutdown();
    }
}

```

### 第二章 Quartz API

Quartz API的关键接口是：

- Scheduler - 与调度程序交互的主要API。
- Job - 由希望由调度程序执行的组件实现的接口。
- JobDetail - 用于定义作业的实例。
- Trigger（即触发器） - 定义执行给定作业的计划的组件。
- JobBuilder - 用于定义/构建JobDetail实例，用于定义作业的实例。
- TriggerBuilder - 用于定义/构建触发器实例。

 Scheduler的生命期，从SchedulerFactory创建它时开始，到Scheduler调用shutdown()方法时结束；Scheduler被创建后，可以增加、删除和列举Job和Trigger，以及执行其它与调度相关的操作（如暂停Trigger）。但是，Scheduler只有在调用start()方法后，才会真正地触发trigger（即执行job)。

  Quartz提供的“builder”类，可以认为是一种领域特定语言（DSL，Domain Specific Language)。教程一中有相关示例，这里是其中的代码片段：（校对注：这种级联的API非常方便用户使用，大家以后写对外接口时也可以使用这种方式）

```java
当Job的一个trigger被触发（稍后会讲到）时，execute（）方法由调度程序的一个工作线程调用。传递给execute()方法的JobExecutionContext对象向作业实例提供有关其“运行时”环job的一个trigger被触发后（稍后会讲到），execute()方法会被scheduler的一个工作线程调用；传递给execute()方法的JobExecutionContext对象中保存着该job运行时的一些信息 ，执行job的scheduler的引用，触发job的trigger的引用，JobDetail对象引用，以及一些其它信息。

JobDetail对象是在将job加入scheduler时，由客户端程序（你的程序）创建的。它包含job的各种属性设置，以及用于存储job实例状态信息的JobDataMap。本节是对job实例的简单介绍，更多的细节将在下一节讲到。

Trigger用于触发Job的执行。当你准备调度一个job时，你创建一个Trigger的实例，然后设置调度相关的属性。Trigger也有一个相关联的JobDataMap，用于给Job传递一些触发相关的参数。Quartz自带了各种不同类型的Trigger，最常用的主要是SimpleTrigger和CronTrigger。

SimpleTrigger主要用于一次性执行的Job（只在某个特定的时间点执行一次），或者Job在特定的时间点执行，重复执行N次，每次执行间隔T个时间单位。CronTrigger在基于日历的调度上非常有用，如“每个星期五的正午”，或者“每月的第十天的上午10:15”等。

为什么既有Job，又有Trigger呢？很多任务调度器并不区分Job和Trigger。有些调度器只是简单地通过一个执行时间和一些job标识符来定义一个Job；其它的一些调度器将Quartz的Job和Trigger对象合二为一。在开发Quartz的时候，我们认为将调度和要调度的任务分离是合理的。在我们看来，这可以带来很多好处。

例如，Job被创建后，可以保存在Scheduler中，与Trigger是独立的，同一个Job可以有多个Trigger；这种松耦合的另一个好处是，当与Scheduler中的Job关联的trigger都过期时，可以配置Job稍后被重新调度，而不用重新定义Job；还有，可以修改或者替换Trigger，而不用重新定义与之关联的Job。
```

### 第三章 Job与JobDetail介绍

我们传给scheduler一个JobDetail实例，因为我们在创建JobDetail时，将要执行的job的类名传给了JobDetail，所以scheduler就知道了要执行何种类型的job；每次当scheduler执行job时，在调用其execute(…)方法之前会创建该类的一个新的实例；执行完毕，对该实例的引用就被丢弃了，实例会被垃圾回收；这种执行策略带来的一个后果是，job必须有一个无参的构造函数（当使用默认的JobFactory时）；另一个后果是，在job类中，不应该定义有状态的数据属性，因为在job的多次执行中，这些属性的值不会保留。

那么如何给job实例增加属性或配置呢？如何在job的多次执行中，跟踪job的状态呢？答案就是:JobDataMap，JobDetail对象的一部分。

#### JobDataMap

JobDataMap中可以包含不限量的（序列化的）数据对象，在job实例执行的时候，可以使用其中的数据；JobDataMap是Java Map接口的一个实现，额外增加了一些便于存取基本类型的数据的方法。

将job加入到scheduler之前，在构建JobDetail时，可以将数据放入JobDataMap，如下示例：

```java
//这个是job的具体内容
public class HelloJob   implements Job {
    public HelloJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        /**
         * JobExecutionContext中包含对
         * trigger的引用，JobDetail对象引用，以及一些其它信息。
         */
        System.out.println("job1开始执行了");

        JobKey key = context.getJobDetail().getKey();

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        String jobSays = jobDataMap.getString("myJob");

        float myFloatValue = jobDataMap.getFloat("myFloatValue");

        System.err.println("Instance " + key + " of HelloJob says: " + jobSays + ", and val is: " + myFloatValue);
    }
}
```

#### Job状态与并发

关于job的状态数据（即JobDataMap）和并发性，还有一些地方需要注意。在job类上可以加入一些注解，这些注解会影响job的状态和并发性。

@DisallowConcurrentExecution：将该注解加到job类上，告诉Quartz不要并发地执行同一个job定义（这里指特定的job类）的多个实例。请注意这里的用词。拿前一小节的例子来说，如果“SalesReportJob”类上有该注解，则同一时刻仅允许执行一个“SalesReportForJoe”实例，但可以并发地执行“SalesReportForMike”类的一个实例。所以该限制是针对JobDetail的，而不是job类的。但是我们认为（在设计Quartz的时候）应该将该注解放在job类上，因为job类的改变经常会导致其行为发生变化。

@PersistJobDataAfterExecution：将该注解加在job类上，告诉Quartz在成功执行了job类的execute方法后（没有发生任何异常），更新JobDetail中JobDataMap的数据，使得该job（即JobDetail）在下一次执行的时候，JobDataMap中是更新后的数据，而不是更新前的旧数据。和 @DisallowConcurrentExecution注解一样，尽管注解是加在job类上的，但其限制作用是针对job实例的，而不是job类的。由job类来承载注解，是因为job类的内容经常会影响其行为状态（比如，job类的execute方法需要显式地“理解”其”状态“）。

如果你使用了@PersistJobDataAfterExecution注解，我们强烈建议你同时使用@DisallowConcurrentExecution注解，因为当同一个job（JobDetail）的两个实例被并发执行时，由于竞争，JobDataMap中存储的数据很可能是不确定的。

### 第四章 Quartz中Triggers介绍

### Trigger的公共属性

所有类型的trigger都有TriggerKey这个属性，表示trigger的身份；除此之外，trigger还有很多其它的公共属性。这些属性，在构建trigger的时候可以通过TriggerBuilder设置。

trigger的公共属性有：

- jobKey属性：当trigger触发时被执行的job的身份；
- startTime属性：设置trigger第一次触发的时间；该属性的值是java.util.Date类型，表示某个指定的时间点；有些类型的trigger，会在设置的startTime时立即触发，有些类型的trigger，表示其触发是在startTime之后开始生效。比如，现在是1月份，你设置了一个trigger–“在每个月的第5天执行”，然后你将startTime属性设置为4月1号，则该trigger第一次触发会是在几个月以后了(即4月5号)。
- endTime属性：表示trigger失效的时间点。比如，”每月第5天执行”的trigger，如果其endTime是7月1号，则其最后一次执行时间是6月5号。

```java
/**
  * 配置相关的触发器
   */
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(  dailyAtHourAndMinute(15,44))
                .build();

        // 最后让我们的job按照触发器的配置进行运作
        scheduler.scheduleJob(job, trigger);
```

指定时间触发 

```java
JobDetail job = JobBuilder.newJob(DumbJob.class)
                .withIdentity("job1", "group1")
                .usingJobData("myJob","Hello world!")
                .usingJobData("jobSays","Hello world!")
                .usingJobData("myFloatValue",3.14F )

                .build();
//配置相关的排除日期
 HolidayCalendar cal =  new HolidayCalendar();
        cal.addExcludedDate(new Date("2019/5/13"));

        scheduler.addCalendar("myHolidays",cal,false,false);

/**
  * simpleTrigger
  */
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("simpleTriger","group1")
                .startAt(parse)
                .forJob("job1","group1")
                .build()
                ;

```

SimpleTrigger的Misfire策略常量：

```java
MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
MISFIRE_INSTRUCTION_FIRE_NOW
MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT
MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT
MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT
MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT
```



### 第四章 CronTrigger

CronTrigger通常比Simple Trigger更有用，如果您需要基于日历的概念而不是按照SimpleTrigger的精确指定间隔进行重新启动的作业启动计划。

使用CronTrigger，您可以指定号时间表，例如“每周五中午”或“每个工作日和上午9:30”，甚至“每周一至周五上午9:00至10点之间每5分钟”和1月份的星期五“。

即使如此，和SimpleTrigger一样，CronTrigger有一个startTime，它指定何时生效，以及一个（可选的）endTime，用于指定何时停止计划。

### Cron Expressions

Cron-Expressions用于配置CronTrigger的实例。Cron Expressions是由七个子表达式组成的字符串，用于描述日程表的各个细节。这些子表达式用空格分隔，并表示：

1. Seconds
2. Minutes
3. Hours
4. Day-of-Month
5. Month
6. Day-of-Week
7. Year (optional field)

### JDBC JobStore

JDBCJobStore也被恰当地命名 - 它通过JDBC将其所有数据保存在数据库中。因此，配置比RAMJobStore要复杂一点，而且也不是那么快。但是，性能下降并不是很糟糕，特别是如果您在主键上构建具有索引的数据库表。在相当现代的一套具有体面的LAN（在调度程序和数据库之间）的机器上，检索和更新触发triggers的时间通常将小于10毫秒。

JDBCJobStore几乎与任何数据库一起使用，已被广泛应用于Oracle，PostgreSQL，MySQL，MS SQLServer，HSQLDB和DB2。要使用JDBCJobStore，必须首先创建一组数据库表以供Quartz使用。您可以在Quartz发行版的“docs / dbTables”目录中找到表创建SQL脚本。如果您的数据库类型尚未有脚本，请查看其中一个脚本，然后以数据库所需的任何方式进行修改。需要注意的一点是，在这些脚本中，所有的表都以前缀“QRTZ_”开始（如表“QRTZ_TRIGGERS”和“QRTZ_JOB_DETAIL”）。只要你通知JDBCJobStore前缀是什么（在你的Quartz属性中），这个前缀实际上可以是你想要的。对于多个调度程序实例，使用不同的前缀可能有助于创建多组表，

创建表后，在配置和启动JDBCJobStore之前，您还有一个重要的决定。您需要确定应用程序需要哪种类型的事务。如果您不需要将调度命令（例如添加和删除triggers）绑定到其他事务，那么可以通过使用JobStoreTX作为JobStore 来管理事务（这是最常见的选择）。

如果您需要Quartz与其他事务（即J2EE应用程序服务器）一起工作，那么您应该使用JobStoreCMT - 在这种情况下，Quartz将让应用程序服务器容器管理事务。

最后一个难题是设置一个DataSource，JDBCJobStore可以从中获取与数据库的连接。DataSources在Quartz属性中使用几种不同的方法之一进行定义。一种方法是让Quartz创建和管理DataSource本身 - 通过提供数据库的所有连接信息。另一种方法是让Quartz使用由Quartz正在运行的应用程序服务器管理的DataSource，通过提供JDBCJobStore DataSource的JNDI名称。有关属性的详细信息，请参阅“docs / config”文件夹中的示例配置文件。

要使用JDBCJobStore（并假定您使用的是StdSchedulerFactory），首先需要将Quartz配置的JobStore类属性设置为org.quartz.impl.jdbcjobstore.JobStoreTX或org.quartz.impl.jdbcjobstore.JobStoreCMT - 具体取决于根据上述几段的解释，您所做的选择。

#### TerracottaJobStore

TerracottaJobStore提供了一种缩放和鲁棒性的手段，而不使用数据库。这意味着您的数据库可以免受Quartz的负载，可以将其所有资源保存为应用程序的其余部分。

TerracottaJobStore可以运行群集或非群集，并且在任一情况下，为应用程序重新启动之间持续的作业数据提供存储介质，因为数据存储在Terracotta服务器中。它的性能比通过JDBCJobStore使用数据库要好得多（约一个数量级更好），但比RAMJobStore要慢。

要使用TerracottaJobStore（并且假设您使用的是StdSchedulerFactory），只需将类名称org.quartz.jobStore.class = org.terracotta.quartz.TerracottaJobStore指定为用于配置石英的JobStore类属性，并添加一个额外的行配置来指定Terracotta服务器的位置：