package cn.feie.oa.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import cn.feie.oa.action.form.WorkflowBean;
import cn.feie.oa.domain.DocumentTemplate;
import cn.feie.oa.domain.Form;
import cn.feie.oa.domain.User;
import cn.feie.oa.service.UserService;
import cn.feie.oa.service.WorkflowService;

@Controller
@Scope("prototype")
public class WorkflowAction {
		
	 @Resource
	 private UserService userService;
	 @Resource
	 private WorkflowService workflowService;
	 
	 /**
	  *	审批流程管理
	  * @return
	  */
	 @RequestMapping("workflow_list")
	 public String workflow_list(HttpSession session){
		//1.查询部署对象信息,对应表(act_re_deployment)
		List<Deployment> findDeploymenList = workflowService.findDeploymenList();
		//2.查询流程定义的信息,对应表(act_re_procdef)
		List<ProcessDefinition> findProcessDefinitionList = workflowService.findProcessDefinitionList();
		//放置到上下文对象中
		session.setAttribute("depList",findDeploymenList);
		session.setAttribute("pdList",findProcessDefinitionList);
		return "Flow_ProcessDefinition/list";
	 }
	 /**
	  * 表单模版管理
	  * @return
	  */
	 @RequestMapping("workflow_templateList")
	 public String workflow_templateList(Model model,HttpSession session){
		 List<DocumentTemplate> DocumentTemplateList = workflowService.findDocumentTemplateList();
		 session.setAttribute("DocumentTemplateList", DocumentTemplateList);
		return "Flow_HtmlFormTemplate/list";
	 }
	 /**
	  * 起草申请
	  * @return
	  */
	 @RequestMapping("workflow_applyFor")
	 public String workflow_applyFor(){
		 
		return "Flow_FormFlow/myTemplateList";
	 }
	 /**
	  * 我的申请查询
	  */
	 @RequestMapping("workflow_myApplyFor")
	 public String workflow_myApplyFor(Integer uid,Model model,HttpSession session){
		 User u = (User)session.getAttribute("user");
		 List<Form> forms = workflowService.findFormByid(u.getUid());
		 model.addAttribute("forms",forms);
		return "Flow_FormFlow_Old/mySubmittedList";
	 }
	 
	 /**
	  * 待我审批
	  */
	 @RequestMapping("workflow_ForMyApproval")
	 public String workflow_ForMyApproval(HttpSession session){
		User u = (User)session.getAttribute("user");
		 /**使用当前用户查询正在执行的任务列表，获取当前任务的集合List<Task> */
		List<Task> task = workflowService.findTackByName(u.getUname());
		session.setAttribute("taskList",task);
		return "Flow_FormFlow/myTaskList";
	 }
	 
	/**
	 * 跳转发布流程
	 * @return
	 */
	 @RequestMapping("workflow_newdeployUI")
	public String newdeployUI(){
		return "Flow_ProcessDefinition/deployUI";
	} 
	/**
	 * 发布流程
	 * @return
	 */
	 @RequestMapping("workflow_newdeploy")
	public String newdeploy(@RequestParam(value="file",required = false)MultipartFile file,HttpServletRequest request){
		String Filename = file.getOriginalFilename();
        CommonsMultipartFile cf= (CommonsMultipartFile)file; 
        DiskFileItem fi = (DiskFileItem)cf.getFileItem(); 
        File f = fi.getStoreLocation();
 		workflowService.saveNewDeploye(f,Filename); 
		return "redirect:/workflow_list.action";
	}
	 /**
		 * 查看流程图
		 * @throws IOException 
		 */
	 	@RequestMapping("workflow_viewImage")
		public String viewImage(String deploymentId,String imageName,HttpServletResponse response) throws IOException{
			//获取流程定义ID
			//获取流程图名称
			System.out.println(deploymentId+"  "+imageName);
			//2：获取资源文件表(act_ge_bytearray)中资源图片输入流InoutStream
			InputStream in = workflowService.findImageInputStream(deploymentId,imageName);
			//3:从response对象获取输出流
			OutputStream out = response.getOutputStream();
			//将输入流中的数据读取出来，写到输出流中
			for (int b = -1;(b=in.read())!=-1;) {
				out.write(b);
				System.out.println(deploymentId+"  "+imageName);
			}
			out.close();
			in.close();
			System.out.println(deploymentId+"  "+imageName);
			return null;
		}
	 	@RequestMapping("workflow_deleteProcess")
	 	public String workflow_deleteProcess(String deploymentId){
	 		workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);
			return "redirect:/workflow_list.action";
	 	}
	 	/**
	 	 * 添加表单模板
	 	 * @return
	 	 */
	 	@RequestMapping("workflow_saveUIForm")
	 	public String workflow_saveUIForm(){
			return "Flow_HtmlFormTemplate/saveUI";
	 	}
	 	/**
	 	 * 修改表单模板
	 	 */
	 	@RequestMapping("workflow_updateUIForm")
	 	public String workflow_updateUIForm(Long id,Model model){
	 		DocumentTemplate d = workflowService.getDocumentTemplateByid(id);
	 		model.addAttribute("documentTemplate",d);
			return "Flow_HtmlFormTemplate/saveUI";
	 	}
	 	/**
	 	 * 删除表单模板
	 	 */
	 	@RequestMapping("workflow_deleteForm")
	 	public String workflow_deleteForm(Long id){
	 		int i = workflowService.deleteDocumentTemplate(id);
	 		return "redirect:/workflow_templateList.action";
	 	}
	 	/**
	 	 * 添加修改模板
	 	 */
	 	@RequestMapping("workflow_addAndUpdateForm")
	 	public String workflow_addAndUpdateForm(DocumentTemplate doc,@RequestParam(value="file",required = false)MultipartFile file,HttpServletResponse response,HttpServletRequest request){
	 		if (doc.getId()!=null) {
				//更新
	 			String filesupload = filesupload(file, response, request);
	 			if (!file.isEmpty()) {
	 				workflowService.saveUrl(filesupload,doc.getId());
				}
	 			int i = workflowService.updateForm(doc);
			}else{
				//添加
				String filesupload = filesupload(file, response, request);
				doc.setUrl(filesupload);
				int i = workflowService.addForm(doc);
			}
			return "redirect:/workflow_templateList.action";
	 	}
	 
	 	/**
	 	 * 跳转申请模版下载上传页面
	 	 * @param id
	 	 * @param model
	 	 * @return
	 	 */
	 	@RequestMapping("workflow_submitUI")
	 	public String workflow_submitUI(Long id,Model model){
	 		model.addAttribute("id",id);
			return "Flow_FormFlow/submitUI";
	 	}
	 	
     @RequestMapping("workflow_submit")
     public String workflow_submit(@RequestParam(value="resource",required = false)MultipartFile resource,Long id,Form form,HttpServletRequest request,HttpServletResponse response){
    	 //通过id获取DocumentTemplate对象
    	 DocumentTemplate documentTemplateByid = workflowService.getDocumentTemplateByid(id);
    	 //设置申请title
    	 form.setTitle(documentTemplateByid.getName());
    	 //上传文件
    	 String filesupload = filesupload(resource, response, request);
    	 //设置上传地址
    	 form.setFormurl(filesupload);
    	 form.setDocumentid(documentTemplateByid.getId());
    	 //启动流程
    	 workflowService.saveStartProcess(form,documentTemplateByid.getProcessName());
    	 workflowService.saveMyForm(form);
		return "redirect:/workflow_myApplyFor.action";
     }
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     public Object download(String fileName,String url,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
    	 if (fileName != null) {
 			File file = new File(url);
 			if (file.exists()) {
 	           response.setContentType("application/force-download");// 设置强制下载不打开
 	            response.addHeader("Content-Disposition","attachment;fileName="+new String((fileName+".docx").getBytes(),"iso8859-1")); 
 	            byte[] buffer = new byte[1024];
 	            FileInputStream fis = null;
 	            BufferedInputStream bis = null;
 	          try {
 	                fis = new FileInputStream(file);
 	                bis = new BufferedInputStream(fis);
 	               OutputStream os = response.getOutputStream();
 	                int i = bis.read(buffer);
 	                while (i != -1) {
 	                   os.write(buffer, 0, i);
 	                    i = bis.read(buffer);
 	                }
 	            } catch (Exception e) {
 	                e.printStackTrace();
 	           } finally {
 	                if (bis != null) {
 	                    try {
 	                        bis.close();
 	                   } catch (IOException e) {
 	                        e.printStackTrace();
 	                    }
 	               }
 	                if (fis != null) {
 	                    try {
 	                       fis.close();
 	                   } catch (IOException e) {
 	                        e.printStackTrace();
 	                    }
 	               }
 				}
 			}
		}
		return response;
     }
     
     
     /**
       * 文件下载
       * @Description: 
       * @param fileName
       * @param request
       * @param response
       * @return
 * @throws UnsupportedEncodingException  Url filename 
       */
	@RequestMapping("/workflow_download")
	public String downloadFile(@RequestParam("id")Long id,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		DocumentTemplate documentTemplateByid = workflowService.getDocumentTemplateByid(id);
		String fileName = documentTemplateByid.getName();
		download(fileName, documentTemplateByid.getUrl(), request, response);
		return null;
	}
	
     /**
      * 上传文件
      * @param file
      * @param response
      * @param request
      */
     public String filesupload(MultipartFile file,HttpServletResponse response,HttpServletRequest request){
    	 	String savedDir = null;
 			if(!file.isEmpty()){   
 	            //可以对user做一些操作如存入数据库  
 	                //以下的代码是将文件file重新命名并存入Tomcat的webapps目录下项目的下级目录fileDir  
 	            String fileRealName = file.getOriginalFilename();                   //获得原始文件名;  
 	            int pointIndex =  fileRealName.lastIndexOf(".");                        //点号的位置       
 	            String fileSuffix = fileRealName.substring(pointIndex);             //截取文件后缀  
 	            if (!fileSuffix.equals(".docx")&&!fileSuffix.equals(".doc")) {
 	            	return savedDir;
 				}
 	            UUID FileId = UUID.randomUUID();                        //生成文件的前缀包含连字符  
 	            String savedFileName =  FileId+fileSuffix;
 	            savedDir = request.getRealPath("/")+"WEB-INF/myform/";			//获取服务器指定文件存取路径  
 	            File savedFile = new File(savedDir,savedFileName );
 	            savedDir+=savedFileName;
 	            boolean isCreateSuccess = false;
 				try {
 					isCreateSuccess = savedFile.createNewFile();
 					if(isCreateSuccess){                      
 		                file.transferTo(savedFile);  //转存文件  
 		            }  
 				} catch (IOException e) {
 					e.printStackTrace();
 				} 
 			}
			return savedDir;
 	 	}
 	
 	/**
 	 * 下载文档审核中()
 	 */
 	@RequestMapping("")
 	public String a(Long id,Model model){
 		
 		return "Flow_FormFlow/showForm";
 	}
   /**
     * 文件下载
     * @Description: 
     * @param fileName
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException 
     */
	@RequestMapping("/workflow_dow")
	public String download(@RequestParam("id")Long id,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		Form form =  workflowService.getFormeByid(id);
		String fileName = form.getTitle();
		download(fileName,form.getFormurl(), request, response);
		return null;		
	}
	/**
	 * 跳转审批页面
	 * @param workflowBean
	 * @param session
	 * @return
	 */
	@RequestMapping("workflow_approveUI")
	public String workflow_approveUI(String executionId,String taskid,Model model){
			Form form = workflowService.getFormByExecutionId(executionId);
			model.addAttribute("form",form);
			model.addAttribute("taskid",taskid);
			/** 获得连线名称*/
			List<String> ouotcomeList = workflowService.findOutComeListByTaskId(taskid);
			model.addAttribute("outcomeList", ouotcomeList);
			/**三：查询所有历史审核人的审核信息，帮助当前人完成审核，返回List<Comment> */
			List<Comment> comments = workflowService.findCommentByTaskId(taskid);
			model.addAttribute("commentList", comments);
			return "Flow_FormFlow/approveUI";
		}
	/**
	 * 
	 * @param workflowBean
	 * @param model
	 * @return
	 */
	@RequestMapping("workflow_myTaskList")
	public String workflow_myTaskList(WorkflowBean workflowBean,Model model){
		workflowService.saveSubmitTask(workflowBean);
		return "redirect:/workflow_ForMyApproval.action";
	}
	/**
	 * 查看历史审批信息
	 * @param workflowBean
	 * @param model
	 * @return
	 */
	@RequestMapping("workflow_approvedHistory")
	public String workflow_approvedHistory(WorkflowBean workflowBean,Integer executionId,Model model){
		Long id = null;
		if (workflowBean.getId()==null) {
			id = workflowService.getFormIdByProcinstId(executionId);
		}else{
		id = workflowBean.getId();//业务id
		}
		Form form2 = workflowService.getFormeByid(id);
		DocumentTemplate documentTemplateByid = workflowService.getDocumentTemplateByid(form2.getDocumentid());
		String key = documentTemplateByid.getProcessName();
		String bussinKey=key+"."+id;//得到BussinKey
		List<Comment> findCommentBybussinKey = workflowService.findCommentBybussinKey(bussinKey);
		model.addAttribute("commentList", findCommentBybussinKey);
		Form form = workflowService.getFormeByid(id);
		model.addAttribute("form",form);
		return "Flow_FormFlow/approvedHistory";
	}
}
