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
	private List<String> provinceList;
	private List<String> municipalityList;
	private List<String> schoolList;
	
	public Profile(long id, String teachingRole, double score){
		this.id = id;
		this.teachingRole = teachingRole;
		this.score =  score;
		this.provinceList = new ArrayList<String>();
		this.municipalityList = new ArrayList<String>();
		this.schoolList = new ArrayList<String>();
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
	
	public List<String> getProvinceList(){
		return provinceList;
	}
	
	public List<String> getMunicipalityList(){
		return municipalityList;
	}
	
	public List<String> getSchoolList(){
		return schoolList;
	}
	
	public void addProvince(String province){
		provinceList.add(province);
	}
	
	public void addMunicipality(String municipality){
		municipalityList.add(municipality);
	}
	
	public void addSchool(String school){
		schoolList.add(school);
	}

	public static Profile createProfile(long id,String teachingRole,double score) {
		Profile profile = new Profile(id,teachingRole,score);
		return profile;
	}
	
}
