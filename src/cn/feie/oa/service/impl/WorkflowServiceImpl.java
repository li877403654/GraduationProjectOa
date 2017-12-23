package cn.feie.oa.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import cn.feie.oa.action.form.WorkflowBean;
import cn.feie.oa.dao.FormDao;
import cn.feie.oa.dao.UserDao;
import cn.feie.oa.domain.DocumentTemplate;
import cn.feie.oa.domain.Form;
import cn.feie.oa.service.UserService;
import cn.feie.oa.service.WorkflowService;

@Service
public class WorkflowServiceImpl implements WorkflowService {
	/**管理流程定义 */
	@Resource
	private RepositoryService repositoryService;
	/**正在运行 */
	@Resource
	private RuntimeService runtimeService;
	/**任务 */
	@Resource
	private TaskService taskService;
	/** 任务表单管理，一个可选服务 */
	@Resource
	private FormService formService;
	/**历史管理(执行完的数据的管理) */
	@Resource
	private HistoryService historyService;
	/** 组织机构管理*/
	
	@Resource
	private FormDao formDao;
	@Resource
	private UserDao userDao;
	@Resource
	private UserService userService;
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
	
	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
	
	
	/** 查询部署对象信息，对应表(act_re_deployment)*/
	@Override
	public List<Deployment> findDeploymenList() {
		List<Deployment> list = repositoryService.createDeploymentQuery()//创建部署对象查询
				.orderByDeploymenTime().asc()//按照时间排序
				.list();
		return list;
	}
	/**
	 * 查询流程定义,对应表(act_re_procdef)
	 */
	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		//descripltion_
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
				.orderByProcessDefinitionVersion().asc()//按照版本升序 
				.list();
		/*
         * Map<String,ProcessDefinition>
         * map集合的key:流程定义的key
         * map集合的value:流程定义的对象
         * 特点：当map集合key值相同的情况下，后一次的值将替换前一次的值
         */
        Map<String,ProcessDefinition> map = new LinkedHashMap<String,ProcessDefinition>();
        if(list != null && list.size() >0){
            for(ProcessDefinition pd:list){
                map.put(pd.getKey(), pd);
            }
        }
        List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());
		return pdList;
	}
	/**部署流程定义 */
	@Override
	public void saveNewDeploye(File file, String filename) {
		try {
			//2:将File类型的文件转换成ZipInputStream流
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			repositoryService.createDeployment()//创建部署对象
				.name(filename)//添加部署名称
				.addZipInputStream(zipInputStream)
				.deploy();//完成部署
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查看流程图
	 */
	@Override
	public InputStream findImageInputStream(String deploymentId,
			String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}
	/**
	 * 删除流程
	 */
	@Override
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
	}
	/**更新请假状态，启动流程实例，让启动的流程实例关联业务 */
	@Override
	public void saveStartProcess(Form form,String ProcessName) {
		//1：获取请假单ID，使用请假单ID，查询请假单带的对象LeaveBill
		Calendar calendar = Calendar.getInstance();
		Long id = calendar.getTime().getTime()-1495080000000L;
		form.setId(id);
		//2：更新请假单的请假状态从0变成1(初始录入-->审核中)
		//3：使用当前对象获取到流程定义的key(对象的名称就是流程定义的key)
		String key = ProcessName;
		
		/**4：从session中获取当前任务的办理人，使用流程变量设置下一个任务办理人
		 		*inputUser是流程变量名称
		 		*获取的办理人是流程变量的值
		*/
		Map<String, Object> variables = new HashMap<String, Object>();
		//办理人
		variables.put("inputUser",form.getUser().getUname());
		/** 
		 * 	(1)使用流程变量设置字符串（格式：LeaveBill.id的形式）,通过设置，让流程(流程实例)关联业务
		 * 	(2)使用正在执行对象表中的一个字段BUSINESS_KEY(Activiti提供一个字段),让启动的流程(流程实例)g关联业务
		 * */
		String objId = key+"."+id;
		variables.put("objId", objId);
		//6：使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象中的字段BUSINESS_KEY添加业务数据
		runtimeService.startProcessInstanceByKey(key,objId,variables);
	}
	/**
	 * 通过那么获取processDefinition对象
	 * @param ProcessName
	 * @return
	 */
	public ProcessDefinition getProcessDefinitionByName(String ProcessName){
		ProcessDefinition processDefinitionName = repositoryService.createProcessDefinitionQuery()
			.processDefinitionName(ProcessName)
			.singleResult();
		return processDefinitionName;
	}
	
	@Override
	public List<Form> findFormByid(Integer uid) {
		return formDao.findFormByid(uid);
	}
	/**使用当前用户查询正在执行的任务列表，获取当前任务的集合List<Task> */
	@Override
	public List<Task> findTackByName(String name) {
		List<Task> list = taskService.createTaskQuery()
			.taskAssignee(name)//指定个人任务查询
			.orderByTaskCreateTime().asc()
			.list();
		return list;
	}
	/**
	 * 办理任务
	 */
	@Override
	public void saveSubmitTask(WorkflowBean workflowBean) {
				//获取任务ID 
				String taskId = workflowBean.getTaskId();
				//获取连线名称
				String outcome = workflowBean.getOutcome();
				//批注信息
				String message = workflowBean.getComment();
				//获取请假单ID
				Long id = workflowBean.getId();
				/** 1：在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录当前申请人 的一些审核信息 */
				//使用任务ID查询任务对象获取流程实例ID
				Task task = taskService.createTaskQuery()
						.taskId(taskId)
						.singleResult();
				//获取流程实例ID
				
				String processInstanceId = task.getProcessInstanceId();
				/** 
				 * 注意：添加批注的时候由于Activiti的底层代码是使用：
				 * 		String userId = Authenticationn.getAuthenticatedUserId();
				 * 		CommentEntiy comment = new CommentEntity();
				 * 		coment.setUserId(userId);
				 * 所有需要从Session中获取当前登录人，最为该任务的办理人（审核人）对应act_hi_comment
				 * 表中的User_ID的字段，不过不添加审核人，该字段为null
				 * 所以要求，添加配置执行使用Authentication.setAuticatedUserId()添加当前任务的审核人;
				 * */
				Authentication.setAuthenticatedUserId(workflowBean.getUname());
				taskService.addComment(taskId, processInstanceId, message);
				/**2：如果连线的名称是“默认提交”那么就不需要设置，如果不是，就需要设置流程变量  
				 * 	  在完成任务之前，设置流程变量，按照连线的名称，去完成任务
				 * 	 流程变量名称：outcome
				 *	 流程变量的值：连线的名称
				 */ 
				Map<String, Object> variables = new HashMap<>();
				if(outcome!=null&&!outcome.equals("默认提交")) {
					variables.put("outcome",outcome);
				}
				if (outcome.equals("驳回")) {
					HistoricProcessInstance pr = historyService.createHistoricProcessInstanceQuery()
							.processInstanceId(processInstanceId)
							.singleResult();
					String businessKey = pr.getBusinessKey();
					//分割字符串得到formid
					String[] split = businessKey.split("\\.");
					Form formeByid = getFormeByid(Long.valueOf(split[1]));
					String uname = formeByid.getUser().getUname();
					variables.put("inputUser",uname);
				}else{
					String uname = userService.getParentById(workflowBean.getUid());
					variables.put("inputUser",uname);
				}
				//使用任务ID，完成当前个人任务，同时设置流程变量
				taskService.complete(taskId, variables);
				//4：当前任务完成之后，需要指定下一个任务的办理人（使用类）---已经完成开发
				
				/**
				 * 5:在完成任务之后，判断流程是否结束
				 * 如果流程结束更新请假单表的状态从1变成2（审核中-->审核完成）
				 */
				ProcessInstance pi = runtimeService.createProcessInstanceQuery()
					.processInstanceId(processInstanceId)
					.singleResult();
				if (pi==null) {
					HistoricProcessInstance pr = historyService.createHistoricProcessInstanceQuery()
							.processInstanceId(processInstanceId)
							.singleResult();
					String businessKey = pr.getBusinessKey();
					//分割字符串得到formid
					String[] split = businessKey.split("\\.");
					Form formeByid = getFormeByid(Long.valueOf(split[1]));
					if (outcome.equals("不同意")) {
						formDao.updateStatusById(formeByid.getId(),2);
					}else{
							formDao.updateStatusById(formeByid.getId(),0);
					}
				}
	}
	/**
	 * 通过procinstid获取Form对象
	 */
	@Override
	public Form getFormByExecutionId(String executionId) {
		ProcessInstance pr = runtimeService.createProcessInstanceQuery()
				.processInstanceId(executionId)//使用流程实例ID查询
				.singleResult();
		if (pr!=null) {
			String businessKey = pr.getBusinessKey();
			//分割字符串得到formid
			String[] split = businessKey.split("\\.");
			System.out.println(split[0]);
			Form formeByid = getFormeByid(Long.valueOf(split[1]));
			return formeByid;
		}
		return null;
	}
	@Override
	public List<Comment> findCommentByTaskId(String taskId) {
		//使用任务ID查询任务对象获取流程实例ID
				List<Comment> listComment = new ArrayList<Comment>();
				Task task = taskService.createTaskQuery()
						.taskId(taskId)
						.singleResult();
				//获取流程实例
				String processInstanceId = task.getProcessInstanceId();
				listComment = taskService.getProcessInstanceComments(processInstanceId);
		return listComment;
	}
	/**二：已知任务ID，查询ProcesessDefinitonEntiy对象，从而获取当前任务完成之后的连线*/
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		List<String> list = new ArrayList<>();
		//1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()
			.taskId(taskId)//使用ID查询
			.singleResult();
		//2:获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//3:查询流程定义的实体对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
		//使用任务对象,Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pr = runtimeService.createProcessInstanceQuery()
			.processInstanceId(processInstanceId)//使用流程实例ID查询
			.singleResult();
		//获取当前活动的ID
		String activityId = pr.getActivityId();
		//4:获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//5:获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if (pvmList!=null&&pvmList.size()>0) {
			for (PvmTransition pvm : pvmList) {
				String name = (String) pvm.getProperty("name");
				if (StringUtils.isNotBlank(name)) {
					list.add(name);
				}else{
					list.add("默认提交");
				}
			}
		}
		return list;
	}
	
	/**通过bussinKey查询批注对象 */
	@Override
	public List<Comment> findCommentBybussinKey(String bussinKey) {
		List<Comment> list = new ArrayList<>();
		HistoricVariableInstance singleResult = historyService.createHistoricVariableInstanceQuery()
			.variableValueEquals("objId", bussinKey)
			.singleResult();
		if (singleResult!=null) {
		String processInstanceId = singleResult.getProcessInstanceId();
		list = taskService.getProcessInstanceComments(processInstanceId);
		}
		return list;
	}
	@Override
	public Long getFormIdByProcinstId(Integer executionId) {
		HistoricProcessInstance pr = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(String.valueOf(executionId))
				.singleResult();
		String businessKey = pr.getBusinessKey();
		//分割字符串得到formid
		String[] split = businessKey.split("\\.");
		return Long.valueOf(split[1]);
	}
	@Override
	public int getPendingnum(String uname) {
		List<Task> list = taskService.createTaskQuery()
				.taskAssignee(uname)//指定个人任务查询
				.orderByTaskCreateTime().asc()
				.list();
		return list.size();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public List<DocumentTemplate> findDocumentTemplateList() {
		List<DocumentTemplate> form= formDao.findDocumentTemplateList();
		return form;
	}
	@Override
	public int addForm(DocumentTemplate doc) {
		return formDao.addForm(doc);
	}
	@Override
	public int deleteDocumentTemplate(Long id) {
		return formDao.deleteDocumentTemplate(id);
	}
	@Override
	public DocumentTemplate getDocumentTemplateByid(Long id) {
		return formDao.getDocumentTemplateByid(id);
	}
	@Override
	public int updateForm(DocumentTemplate doc) {
		return formDao.updateForm(doc);
	}
	@Override
	public int saveUrl(String url, Long id) {
		return formDao.saveUrl(url, id);
	}
	@Override
	public int saveMyForm(Form form) {
		return formDao.saveMyForm(form);
	}

	@Override
	public Form getFormeByid(Long id) {
		return formDao.getFormeByid(id);
	}
}
