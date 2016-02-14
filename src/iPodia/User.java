package iPodia;

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

	public User() {
		m_type = null;
		m_email = null;
		m_name = null;
		m_university = null;
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

	public String getHome() {
		switch (m_type) {
		case Admin:
			return "/admin";
		case Student:
			return "/student";
		case Registrar:
			return "/registrar";
		default:
			return"";
		}
	}

	public String getEmail() {
		return m_email;
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

	public boolean hasClass(String className) {
		return m_classes.containsValue(className);
	}

	public void addClass(int classId, String className) {
		m_classes.put(classId, className);
	}

	public String getClassName(int classId) {
		return m_classes.get(classId);
	}
}
