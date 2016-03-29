package com.myou.appback.resource;

/***
 * 
 * @author Steven
 * http://www.lifeba.org
 */
import java.util.HashMap;

import com.myou.appback.vo.Student;

public class ResourceHelper {
	public static int maxId = 0;
	public static HashMap<Integer,Student> students= new HashMap<Integer,Student>();
	
	static {
		 Student student = new Student();
		 student.setId(1);
		 student.setClsId(201001);
		 student.setName("Steven");
		 student.setSex(1);
		 maxId = student.getId();
		 students.put(student.getId(), student);
	}
	
	 public static Student getDefaultStudent(){
		 return students.get(1);
	 }
	 
	 public static Student findStudent(int id){
		 return students.get(id);
	 }
	 
	 public static int addStudent(Student student){
		 students.put(student.getId(), student);
		 return student.getId();
	 }
	 
	 public static int updateStudent(Student student){
		 return addStudent(student);
	 }
	 
	 public static int deleteStudent(int id){
		 if(students.get(id)!=null){
			 students.remove(id);
			 return 1;
		 }
		 return 0;
	 }
	
}
