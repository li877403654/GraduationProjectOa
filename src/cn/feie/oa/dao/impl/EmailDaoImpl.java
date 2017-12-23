package cn.feie.oa.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import cn.feie.oa.dao.EmailDao;
import cn.feie.oa.domain.Email;
import cn.feie.oa.domain.User;

@Repository
public class EmailDaoImpl  extends JdbcDaoSupport implements EmailDao{

	@Resource(name = "dataSource")
	public void gerJdbcDaoSupport(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	/**
	 * 发信息
	 */
	public boolean addEmail(Email email){
		int i = this.getJdbcTemplate().update("INSERT INTO email VALUES(null,?,?,?,?,?,?,?,?,null,1)",email.getEtitle(),
				email.getEtext(),
				email.getGoid(),
				email.getComeid(),
				email.getDraft(),
				email.getWaste(),
				new Date(),
				email.getPlace());
		return i>0;
	}
	
	/**
	 * 收信箱
	 */
	public List<Email> comeEmail(int goid,int comeid,int startnum,int endnum){
		List<Email> list = this.getJdbcTemplate().query("SELECT  u.uname,e.* FROM email e LEFT JOIN user u ON u.uid=e.goid  WHERE comeid=?  AND waste=0 AND draft=0 ORDER BY godate DESC  LIMIT ?,?",new RowMapper<Email>() {
			@Override										 
			public Email mapRow(ResultSet rs, int arg1) throws SQLException {
				Email e = new Email();
				e.setEid(rs.getInt("eid"));
				e.setEtitle(rs.getString("etitle"));
				e.setEtext(rs.getString("etext"));
				e.setGoid(rs.getInt("goid"));
				e.setDraft(rs.getInt("draft"));
				e.setComeid(rs.getInt("comeid"));
				e.setWaste(rs.getInt("waste"));
				e.setGodate(rs.getTimestamp("godate"));
				e.setPlace(rs.getInt("place"));
				User user = new User();
				user.setUname(rs.getString("uname"));
				e.setUser(user);
				return e;
			}
		},comeid,startnum,endnum);
		return list;
	}
	/**
	 * 查询有多少邮件
	 */
	public int sumEmail(){
		@SuppressWarnings("unchecked")
		List<Integer> i = this.getJdbcTemplate().query("SELECT COUNT(eid) FROM email", new RowMapper() {

			@Override
			public Integer mapRow(ResultSet rs, int arg1) throws SQLException {
				return rs.getInt(1);
			}
		});
		return i.get(0);
	}
	/**
	 * 垃圾箱
	 */
	public List<Email> waste(int goid,int comeid,int startnum,int endnum){
		List<Email> list = this.getJdbcTemplate().query("SELECT u.uname,e.* FROM email e LEFT JOIN user u ON u.uid=e.goid  WHERE comeid=? AND waste=1  AND draft=0 ORDER BY deldate DESC LIMIT ?,?",new RowMapper<Email>() {
			@Override									
			public Email mapRow(ResultSet rs, int arg1) throws SQLException {
				Email e = new Email();
				e.setEid(rs.getInt("eid"));
				e.setEtitle(rs.getString("etitle"));
				e.setEtext(rs.getString("etext"));
				e.setGoid(rs.getInt("goid"));
				e.setDraft(rs.getInt("draft"));
				e.setComeid(rs.getInt("comeid"));
				e.setWaste(rs.getInt("waste"));
				e.setGodate(rs.getTimestamp("godate"));
				e.setPlace(rs.getInt("place"));
				User user = new User();
				user.setUname(rs.getString("uname"));
				e.setUser(user);
				return e;
			}
		},comeid,startnum,endnum);
		return list;
	}
	
	/**
	 * 草稿箱
	 */
	public List<Email> draftbox(int goid,int comeid,int startnum,int endnum){
		List<Email> list = this.getJdbcTemplate().query("SELECT u.uname,e.* FROM email e LEFT JOIN user u ON u.uid=e.comeid  WHERE goid=? AND waste=0  AND draft=1 ORDER BY eid DESC LIMIT ?,?",new RowMapper<Email>() {
			@Override  
			public Email mapRow(ResultSet rs, int arg1) throws SQLException {
				Email e = new Email();
				e.setEid(rs.getInt("eid"));
				e.setEtitle(rs.getString("etitle"));
				e.setEtext(rs.getString("etext"));
				e.setGoid(rs.getInt("goid"));
				e.setDraft(rs.getInt("draft"));
				e.setComeid(rs.getInt("comeid"));
				e.setWaste(rs.getInt("waste"));
				e.setGodate(rs.getTimestamp("godate"));
				e.setPlace(rs.getInt("place"));
				User user = new User();
				user.setUname(rs.getString("uname"));
				e.setUser(user);
				return e;
			}
		},goid,startnum,endnum);
		return list;
	}
	
	/**
	 * 发信箱
	 */
	public List<Email> outemail(int goid,int comeid,int startnum,int endnum){
		List<Email> list = this.getJdbcTemplate().query("SELECT u.uname,e.* FROM email e LEFT JOIN user u ON u.uid=e.comeid WHERE goid=?  AND waste=0  AND draft=0 ORDER BY eid DESC LIMIT ?,?",new RowMapper<Email>() {
			@Override
			public Email mapRow(ResultSet rs, int arg1) throws SQLException {
				Email e = new Email();
				e.setEid(rs.getInt("eid"));
				e.setEtitle(rs.getString("etitle"));
				e.setEtext(rs.getString("etext"));
				e.setGoid(rs.getInt("goid"));
				e.setDraft(rs.getInt("draft"));
				e.setComeid(rs.getInt("comeid"));
				e.setWaste(rs.getInt("waste"));
				e.setGodate(rs.getTimestamp("godate"));
				e.setPlace(rs.getInt("place"));
				User user = new User();
				user.setUname(rs.getString("uname"));
				e.setUser(user);
				return e;
			}
		},goid,startnum,endnum);
		return list;
	}
	
	/**
	 * 通过ID获取邮件
	 */
	@Override
	public Email getEmailByEid(int eid) {
		Email email = this.getJdbcTemplate().queryForObject("SELECT u.uname,u1.uname u1name,e.* FROM email e LEFT JOIN user u ON u.uid=e.comeid LEFT JOIN user u1 ON u1.uid=e.goid WHERE eid=?",new RowMapper<Email>() {

			@Override
			public Email mapRow(ResultSet rs, int arg1) throws SQLException {
				Email e  = new Email();
				e.setEid(rs.getInt("eid"));
				e.setEtitle(rs.getString("etitle"));
				e.setEtext(rs.getString("etext"));
				e.setGoid(rs.getInt("goid"));
				e.setDraft(rs.getInt("draft"));
				e.setComeid(rs.getInt("comeid"));
				e.setWaste(rs.getInt("waste"));
				e.setGodate(rs.getTimestamp("godate"));
				e.setPlace(rs.getInt("place"));
				User u = new User();
				u.setUname(rs.getString("uname"));
				e.setUser(u);
				User u1 = new User();
				u1.setUname(rs.getString("u1name"));
				e.setUser1(u1);
				return e;
			}
		},eid);
		return email;
	}
	
	/**
	 * 删除邮件
	 */
	@Override
	public void wasteEmail(int eid) {
		this.getJdbcTemplate().update("UPDATE email SET waste=1,deldate=NOW() WHERE eid = ?;",eid);
	}
	/**
	 * 从数据库中删除邮件
	 */
	@Override
	public void delEmail(int eid) {
		this.getJdbcTemplate().update("delete from email WHERE eid = ?;",eid);
	}
	
	/**
	 * 保存到草稿箱
	 */
	@Override
	public void draftEmail(int eid) {
		this.getJdbcTemplate().update("update email set draft =1 where eid=?",eid);
	}
	
	/**
	 * 恢复邮件
	 */
	@Override
	public void restoreEmail(int eid) {
		this.getJdbcTemplate().update("UPDATE email SET waste =0 WHERE eid = ?;",eid);
	}
	
	/**
	 * 查看邮件
	 */
	@Override
	public void newsno(int eid){
		this.getJdbcTemplate().update("UPDATE email SET news =0 WHERE eid = ?;",eid);
	}
	
	/**
	 * 没查看的邮件数量
	 */
	public int newsnum(int uid){
		int i = this.getJdbcTemplate().queryForInt("SELECT COUNT(*) FROM email WHERE news = 1 and waste =0 and draft = 0   AND comeid = ?",uid);
		return i;
	}

}
