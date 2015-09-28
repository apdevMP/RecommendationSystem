package core;
import java.util.ArrayList;

/**
 * Classe per rappresentare il profilo (forse non serve)
 *
 */
public class Profile {
	private long id;
	private String name;
	private ArrayList<String> regionList;
	private ArrayList<String> provinceList;
	private ArrayList<String> municipalityList;
	private ArrayList<String> schoolList;
	
	public Profile(long id){
		this.id = id;
		this.regionList = new ArrayList<String>();
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
	
	public ArrayList<String> getRegionList(){
		return regionList;
	}
	
	public ArrayList<String> getProvinceList(){
		return provinceList;
	}
	
	public ArrayList<String> getMunicipalityList(){
		return municipalityList;
	}
	
	public ArrayList<String> getSchoolList(){
		return schoolList;
	}
	
	
	public void addRegion(String region){
		regionList.add(region);
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
	
}
