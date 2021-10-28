package com.wave.camunda;

import com.alibaba.fastjson.JSONObject;
import com.wave.workflow.WorkflowApplication;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijinyang
 * @date 2021/10/25 19:31
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorkflowApplication.class)
public class AppTests {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    public void testReadFlowDefine() throws FileNotFoundException {
        String pdKey = "request_flow";
        DeploymentWithDefinitions deployWithResult = repositoryService.createDeployment().name(pdKey).addInputStream("request_flow.bpmn",
                new FileInputStream("D:\\xm_work\\3_camunda\\request_flow.bpmn")).deployWithResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().active().processDefinitionKey(pdKey).latestVersion().singleResult();
        log.info(" deploy result {}", processDefinition);
    }

    @Test
    public void testRequestTvFactory() throws FileNotFoundException {
        String pdKey = "request-tv-factory";
        DeploymentWithDefinitions deployWithResult = repositoryService.createDeployment().name(pdKey).addInputStream("request-tv-factory.bpmn",
                new FileInputStream("D:\\xm_work\\3_camunda\\request-tv-factory.bpmn")).deployWithResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().active().processDefinitionKey(pdKey).latestVersion().singleResult();
        log.info(" deploy result {}", processDefinition);
    }

    @Test
    public void startTvFactory() {
        Map<String, Object> params = new HashMap<>();
        params.put("dataId", "R2021102601");
        params.put("enterprise", 1001);
        String pdKey = "request-tv-factory";
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(pdKey, "R2021102601", params);
        log.info("==== {}", instance.getId());
    }

    // 6601
    @Test
    public void executeTask() {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId("6801").active().list();
        String executionId = tasks.get(0).getExecutionId();
        log.info("==== {}", tasks);
    }

    @Test
    public void startFlow() {
        Map<String, Object> params = new HashMap<>();
        params.put("dataId", "R245875");
        params.put("requestInfo", "{}");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("request_flow", params);
        log.info("==== {} ", instance.getId());
    }

    @Test
    public void testGetCurrentNode() {
        /*List<Execution> taskList = runtimeService.createExecutionQuery().processInstanceId("6101").list();
        log.info("==== task list {}", taskList);*/
        // 4901
        List<Task> tasks = taskService.createTaskQuery().processInstanceId("6101").active().list();

        log.info("=== task {}", tasks);
    }
}
