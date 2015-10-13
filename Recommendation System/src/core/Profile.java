package core;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe per rappresentare il profilo 
 *
 */
public class Profile {
	private long id;
	private String name;
	private String teachingRole;
	private double score;
	
	
	public Profile(long id, String teachingRole, double score){
		this.id = id;
		this.teachingRole = teachingRole;
		this.score =  score;
		
	}
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String getTeachingRole(){
		return teachingRole;
	}
	
	
	public static Profile createProfile(long id,String teachingRole,double score) {
		Profile profile = new Profile(id,teachingRole,score);
		return profile;
	}
	
}
