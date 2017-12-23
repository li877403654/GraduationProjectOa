package cn.feie.oa.untils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.feie.oa.domain.User;
import cn.feie.oa.service.UserService;

/**
 * 员工经理任务分配
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		/**重新查询当前用户，再获取当前用户对应的领导 */
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		try {
			User u  = (User) session.getAttribute("user");
			//使用当前用户差查询用户的 详细信息
			//从web中获取spring容器
			WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
			UserService Service=  (UserService) ac.getBean("userService");
			String name = Service.getParentById(u.getUid()); 
			//设置个人任务办理人 
			delegateTask.setAssignee(name);
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
