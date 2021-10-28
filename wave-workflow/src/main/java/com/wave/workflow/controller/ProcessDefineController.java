package com.wave.workflow.controller;

import org.camunda.bpm.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lijinyang
 * @date 2021/10/25 16:14
 */
@RestController
@RequestMapping("process-define")
public class ProcessDefineController {

    @Autowired
    RepositoryService repositoryService;


}
