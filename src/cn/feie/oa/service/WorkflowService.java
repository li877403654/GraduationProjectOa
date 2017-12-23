package cn.feie.oa.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import cn.feie.oa.action.form.WorkflowBean;
import cn.feie.oa.domain.DocumentTemplate;
import cn.feie.oa.domain.Form;

public interface WorkflowService {

	List<Deployment> findDeploymenList();

	List<ProcessDefinition> findProcessDefinitionList();

	void saveNewDeploye(File file, String filename);

	InputStream findImageInputStream(String deploymentId, String imageName);

	void deleteProcessDefinitionByDeploymentId(String deploymentId);

	List<DocumentTemplate> findDocumentTemplateList();

	DocumentTemplate getDocumentTemplateByid(Long id);

	int deleteDocumentTemplate(Long id);

	int updateForm(DocumentTemplate doc);

	int addForm(DocumentTemplate doc);

	int saveUrl(String url,Long id);
	
	int saveMyForm(Form form);
	
	void saveStartProcess(Form form,String ProcessName);

	List<Form> findFormByid(Integer uid);

	Form getFormeByid(Long id);

	List<Task> findTackByName(String uname);

	void saveSubmitTask(WorkflowBean workflowBean);

	Form getFormByExecutionId(String executionId);

	List<String> findOutComeListByTaskId(String taskid);

	List<Comment> findCommentByTaskId(String taskid);

	List<Comment> findCommentBybussinKey(String bussinKey);

	Long getFormIdByProcinstId(Integer executionId);

	int getPendingnum(String string);
}
