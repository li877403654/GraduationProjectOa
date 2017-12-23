package cn.feie.oa.dao;

import java.util.List;

import cn.feie.oa.domain.DocumentTemplate;
import cn.feie.oa.domain.Form;

public interface FormDao {


	List<DocumentTemplate> findDocumentTemplateList();

	int updateForm(DocumentTemplate doc);

	DocumentTemplate getDocumentTemplateByid(Long id);

	int deleteDocumentTemplate(Long id);

	int addForm(DocumentTemplate doc);

	int saveUrl(String url,Long id);

	int saveMyForm(Form form);

	List<Form> findFormByid(Integer uid);

	Form getFormeByid(Long id);


	void updateStatusById(Long id, int i);
}
