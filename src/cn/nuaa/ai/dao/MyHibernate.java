package cn.nuaa.ai.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

public class MyHibernate {

	public static List<?> sqlQuery(int from,int max,String com) {

		Configuration cfg = new Configuration();
		// cfg.configure();可带参数指定配置文件.返回值还是一个configuration 但是其拥有了配置选项;
		SessionFactory sf = cfg.configure().buildSessionFactory();
		// 打开session;
		Session session = sf.openSession();

		// 开始事务;
		session.beginTransaction();
		// 参数是一个字符串,是HQL的查询语句.注意此时的的UserU为大写,为对象的,而不是表的.
		Query query = session.createQuery(com);
		
        //从第一个开始查起.可以设置从第几个查起;
        query.setFirstResult(from);
        //最大条数;
        query.setMaxResults(max);
		
		// 使用List方法.
		List<?> userList = query.list();
		/*
		// 迭代器去迭代.
		for (Iterator<?> iter = userList.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
			System.out.println("id=" + user.getUserId() + "name=" + user.getUserName());
		}
		*/
		// 获取事务并提交;
		session.getTransaction().commit();

		session.close();
		sf.close();
		return userList;
	}
	public static List<?> sqlQueryByOrder(int from,int max,String com) {

		Configuration cfg = new Configuration();
		// cfg.configure();可带参数指定配置文件.返回值还是一个configuration 但是其拥有了配置选项;
		SessionFactory sf = cfg.configure().buildSessionFactory();
		// 打开session;
		Session session = sf.openSession();

		// 开始事务;
		session.beginTransaction();
		// 参数是一个字符串,是HQL的查询语句.注意此时的的UserU为大写,为对象的,而不是表的.
		Query query = session.createQuery(com);
		
        //从第一个开始查起.可以设置从第几个查起;
        query.setFirstResult(from);
        //最大条数;
        query.setMaxResults(max);
		
		// 使用List方法.
		List<?> userList = query.list();
		/*
		// 迭代器去迭代.
		for (Iterator<?> iter = userList.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
			System.out.println("id=" + user.getUserId() + "name=" + user.getUserName());
		}
		*/
		// 获取事务并提交;
		session.getTransaction().commit();

		session.close();
		sf.close();
		return userList;
	}
	
	public static long sqlGetRecordNum(String com){
		Configuration cfg = new Configuration();
		// cfg.configure();可带参数指定配置文件.返回值还是一个configuration 但是其拥有了配置选项;
		SessionFactory sf = cfg.configure().buildSessionFactory();
		// 打开session;
		Session session = sf.openSession();
		// 开始事务;
		session.beginTransaction();
		Query query = session.createQuery(com);
		// 获取事务并提交;
		session.getTransaction().commit();
		//获取长度;
		long sum=(long) query.uniqueResult();
		session.close();
		sf.close();
		return sum;
		//Criteria criteria = session.createCriteria(User.class);
		//criteria.setProjection(Projections.rowCount());
		//return criteria.list().size();
	}
	
	public static void sqlSaveOrUpdate(Object object){
		Configuration cfg = new Configuration();
		// cfg.configure();可带参数指定配置文件.返回值还是一个configuration 但是其拥有了配置选项;
		SessionFactory sf = cfg.configure().buildSessionFactory();
		// 打开session;
		Session session = sf.openSession();
		// 开始事务;
		session.beginTransaction();
		session.saveOrUpdate(object);
		// 获取事务并提交;
		session.getTransaction().commit();
		//关闭;
		session.close();
		sf.close();
	}
	
	public static void sqlDelete(Object object){
		Configuration cfg = new Configuration();
		// cfg.configure();可带参数指定配置文件.返回值还是一个configuration 但是其拥有了配置选项;
		SessionFactory sf = cfg.configure().buildSessionFactory();
		// 打开session;
		Session session = sf.openSession();
		// 开始事务;
		session.beginTransaction();
		session.delete(object);
		// 获取事务并提交;
		session.getTransaction().commit();
		//关闭;
		session.close();
		sf.close();
	} 
}
