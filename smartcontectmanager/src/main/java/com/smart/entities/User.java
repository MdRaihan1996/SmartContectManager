package com.smart.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name="User")
public class User implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotEmpty(message="Name must be required")
	private String name;
	
	@Column(unique = true)
	@Size(min=3,max=20,message="min 3 charactor and max 20 charactor")
	private String email;
	private String password;
	private String role;
	private boolean enabled;
	private String imageUrl;
	
	@Column(length = 500)
	private String about;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "user")
	private List<Contect> contects=new ArrayList<>();

	public User() {
		super();
		
	}

	public User(int id, String name, String email, String password, String role, boolean enabled, String imageUrl,
			String about) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
		this.imageUrl = imageUrl;
		this.about = about;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public List<Contect> getContects() {
		return contects;
	}

	public void setContects(List<Contect> contects) {
		this.contects = contects;
	}

	/*
	 * @Override public String toString() { return "User [id=" + id + ", name=" +
	 * name + ", email=" + email + ", password=" + password + ", role=" + role +
	 * ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about +
	 * ", contects=" + contects + "]"; }
	 */
	
	
	
	
}
