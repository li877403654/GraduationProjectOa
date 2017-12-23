package cn.feie.oa.service;

import java.util.List;

import cn.feie.oa.domain.Email;

public interface EmailService {
	
	boolean addEmail(Email email);
	int sumEmail();
	List<Email> box(int goid,int comeid, int startnum, int endnum,String whatbox);
	int sumnum(int sizi);
	Email getEmailByEid(int eid);
	void wasteEmail(int eid);
	void delEmail(int eid);
	void draftEmail(int eid);
	void restoreEmail(int eid);
	void newsno(int uid);
	int newsnum(int uid);
	void addDrafyEmail(Email email);
}
