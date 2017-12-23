package cn.feie.oa.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import cn.feie.oa.dao.FormDao;
import cn.feie.oa.domain.DocumentTemplate;
import cn.feie.oa.domain.Form;
import cn.feie.oa.domain.User;

@Repository
public class FormDaoImpl  extends JdbcDaoSupport implements FormDao {
	
	@Resource(name = "dataSource")
	public void gerJdbcDaoSupport(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	

	@Override
	public List<DocumentTemplate> findDocumentTemplateList() {
		List<DocumentTemplate> d = this.getJdbcTemplate().query("select * from documentTemplate",new Object[] {},new BeanPropertyRowMapper<DocumentTemplate>(DocumentTemplate.class));
		return d;
	}


	@Override
	public int updateForm(DocumentTemplate doc) {
		String sql = "UPDATE documenttemplate SET NAME = ?,processName = ? where id = ?";
		int update = this.getJdbcTemplate().update(sql,doc.getName(),doc.getProcessName(),doc.getId());
		return update;
	}


	@Override
	public DocumentTemplate getDocumentTemplateByid(Long id) {
		DocumentTemplate d = this.getJdbcTemplate().queryForObject("select * from documentTemplate where id = ?",new Object[] {id},new BeanPropertyRowMapper<DocumentTemplate>(DocumentTemplate.class));
		return d;
	}


	@Override
	public int deleteDocumentTemplate(Long id) {
		int update = this.getJdbcTemplate().update("DELETE FROM documentTemplate WHERE id = ?",id);
		return update;
	}


	@Override
	public int addForm(DocumentTemplate doc) {
		String sql = "INSERT INTO documentTemplate(name,processName,url) VALUES(?,?,?);";
		int update = this.getJdbcTemplate().update(sql,doc.getName(),doc.getProcessName(),doc.getUrl());
		return update;
	}
	


	@Override
	public int saveUrl(String url, Long id) {
		return this.getJdbcTemplate().update("UPDATE documenttemplate SET url = ? where id = ?",url,id);
	}
	
	@Override
	public int saveMyForm(final Form form) {
		String sql = "INSERT INTO form(id,title,applyTime,status,uid,formurl,documentid) VALUES(?,?,now(),1,?,?,?);";
		int update = this.getJdbcTemplate().update(sql,form.getId(),form.getTitle(),form.getUid(),form.getFormurl(),form.getDocumentid());;
		return update;
	}
	@Override
	public List<Form> findFormByid(Integer uid) {
		List<Form> forms = this.getJdbcTemplate().query("SELECT * FROM form WHERE uid =?",new BeanPropertyRowMapper<Form>(Form.class),uid);
		return forms;
	}
	@Override
	public Form getFormeByid(Long id) {
		Form f = this.getJdbcTemplate().queryForObject("select * from form f left join user u on f.uid = u.uid where id = ?",new Object[] {id},new RowMapper<Form>(){

			@Override
			public Form mapRow(ResultSet rs, int arg1) throws SQLException {
				Form form = new Form();
				form.setId(rs.getLong("id"));
				form.setApplyTime(rs.getDate("applyTime"));
				form.setDocumentid(rs.getLong("documentid"));
				form.setStatus(rs.getInt("status"));
				form.setTitle(rs.getString("title"));
				form.setUid(rs.getInt("uid"));
				form.setFormurl(rs.getString("formurl"));
				User u = new User();
				u.setUname(rs.getString("uname"));
				form.setUser(u);
				return form;
			}});
		return f;
	}
	@Override
	public void updateStatusById(Long id,int i) {
		this.getJdbcTemplate().update("UPDATE form SET status = ? where id = ?",i,id);
	}
}
