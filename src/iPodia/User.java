package iPodia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class User {
	public static enum Type {
		Admin,
		Student,
		Registrar
	};

	private User.Type m_type;
	private String m_email;
	private String m_name;
	private String m_university;
	private HashMap<Integer, String> m_classes;
	private String recommendedTopicNum;

	public User() {
		m_type = null;
		m_email = null;
		m_name = null;
		m_university = null;
		m_classes = new HashMap<Integer, String>();
		recommendedTopicNum = "";
	}

	public User(ResultSet rs) throws SQLException {
		initializeFromResultSet(rs);
	}

	public void initializeFromResultSet(ResultSet rs) throws SQLException {
		determineType(rs.getString("level"));
		setEmail(rs.getString("email"));
		setName(rs.getString("firstName"), rs.getString("lastName"));
		setUniversity(rs.getString("university"));
		m_classes = new HashMap<Integer, String>();
	}

	public boolean isAuthenticated() {
		return m_type != null;
	}

	public boolean isAdmin() {
		return m_type == User.Type.Admin;
	}

	public boolean isStudent() {
		return m_type == User.Type.Student;
	}

	public boolean isRegistrar() {
		return m_type == User.Type.Registrar;
	}

	public void setType(User.Type type) {
		m_type = type;
	}

	public void determineType(String type) {
		if (type.startsWith("admin"))
			setType(User.Type.Admin);
		else if (type.startsWith("student"))
			setType(User.Type.Student);
		else if (type.startsWith("registrar"))
			setType(User.Type.Registrar);
	}

	public String getHome() {
		switch (m_type) {
		case Admin:
			return "/admin";
		case Student:
			return "/student";
		case Registrar:
			return "/registrar";
		default:
			return "/"; // Unknown type
		}
	}

	public String getEmail() {
		return m_email;
	}

	public String getSafeEmail() {
		return Defaults.createSafeString(m_email);
	}

	public void setEmail(String email) {
		m_email = email;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String firstName, String lastName) {
		m_name = firstName + " " + lastName;
	}

	public String getUniversity() {
		return m_university;
	}

	public void setUniversity(String university) {
		m_university = university;
	}

	public HashMap<Integer, String> getClasses() {
		return m_classes;
	}

	public boolean hasClass(int classId) {
		return m_classes.containsKey(classId);
	}

	public boolean hasClass(String classId) {
		return hasClass(Integer.parseInt(classId));
	}

	public void addClass(int classId, String className) {
		m_classes.put(classId, className);
	}

	public void addClass(String classId, String className) {
		addClass(Integer.parseInt(classId), className);
	}

	public String getClassName(int classId) {
		return m_classes.get(classId);
	}

	public String getClassName(String classId) {
		return getClassName(Integer.parseInt(classId));
	}
	
	public void setRecommendedTopicNum (String num) {
		recommendedTopicNum = num;
	}
	
	public String getRecommendedTopicNum() {
		return recommendedTopicNum;
	}
}
