import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

/**
 * This class represents an utility matrix
 * 
 */
public class UtilityMatrix {

	private static final String TARGET = "target";
	private static final String TYPE_ID = "typeId";
	private static final String KEY = "key";
	private static final String LAST_EVENT_DATA = "lastEventData";
	private static final String USER_ID = "userId";
	private static final String CONTEXT = "context";
	private static final String USER_SCORE = "userScore";
	private static final String EVENT_TYPE = "eventType";

	private List<Long> matrixUser;
	private List<String> matrixProvince;
	private List<String> matrixMunicipality;
	private List<String> matrixSchool;
	private ArrayList<ArrayList<Integer>> values;
	private int placeSize;

	public UtilityMatrix() {
		matrixUser = new ArrayList<Long>();
		matrixProvince = new ArrayList<String>();
		matrixMunicipality = new ArrayList<String>();
		matrixSchool = new ArrayList<String>();
		values = new ArrayList<ArrayList<Integer>>();
	}

	private void addUser(Document doc) {
		long userId = doc.getLong(USER_ID);
		if (!matrixUser.contains(userId)) {
			matrixUser.add(userId);
		}
	}

	private void addProvince(Document doc) {
		String province = doc.getString(KEY);
		if (!matrixProvince.contains(province)) {
			matrixProvince.add(province);
		}
	}

	private void addMunicipality(Document doc) {
		String municipality = doc.getString(KEY);
		if (!matrixMunicipality.contains(municipality)) {
			matrixMunicipality.add(municipality);
		}
	}

	private void addSchool(Document doc) {
		String school = doc.getString(KEY);
		if (!matrixSchool.contains(school)) {
			matrixSchool.add(school);
		}
	}

	private void initializeValues() {
		placeSize = matrixMunicipality.size() + matrixProvince.size()
				+ matrixSchool.size();

		for (ArrayList<Integer> it : values) {
			for (int i = 0; i < placeSize; i++) {
				it.add(0);
			}
		}
	}

	private int computeValue(int score, int userScore, long eventType) {
		int value = 0;
		if (userScore > score + 5) {
			value = 1;
			if (eventType == 2) {
				value++;
			}
		}
		if (userScore <= score + 5 && userScore >= score - 5) {
			value = 2;
		}
		if (userScore <= score - 5)
			value = 3;
		return value;
	}

	public void fillMatrixWithWatches(ArrayList<Document> list, int score) {
		if (list.size() < 1) {
			System.out.println("You must fill matrix with not empty list");
		}

		for (Document doc : list) {
			this.addUser(doc);
			values.add(new ArrayList<Integer>());

			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);
			System.out.println(typeId);
			switch ((int) typeId) {
			case 1:
				this.addProvince(target);
				break;

			case 2:
				this.addMunicipality(target);
				break;

			case 3:
				this.addSchool(target);
				break;
			}

		}

		this.initializeValues();
		for (Document doc : list) {
			long userId = doc.getLong(USER_ID);
			int indexUser = matrixUser.indexOf(userId);
			Document target = (Document) doc.get(TARGET);
			long typeId = target.getLong(TYPE_ID);
		
			int value = 0;
			switch ((int) typeId) {
			case 1:

				String province = target.getString(KEY);
				int indexProvince = matrixProvince.indexOf(province);
				Document ledProvince = (Document) doc.get(LAST_EVENT_DATA);
				Document ctxProvince = (Document) ledProvince.get(CONTEXT);
				int usProvince = ctxProvince.getInteger(USER_SCORE);
				value = computeValue(score, usProvince, ledProvince.getLong(EVENT_TYPE));
			
				values.get(indexUser).set(indexProvince, value);
				break;

			case 2:
				String municipality = target.getString(KEY);
				int indexMunicipality = matrixMunicipality
						.indexOf(municipality);
				Document ledMunicipality = (Document) doc.get(LAST_EVENT_DATA);
				Document ctxMunicipality = (Document) ledMunicipality.get(CONTEXT);
				int usMunicipality = ctxMunicipality.getInteger(USER_SCORE);
				value = computeValue(score, usMunicipality, ledMunicipality.getLong(EVENT_TYPE));
				
				values.get(indexUser).set(
						indexMunicipality + matrixProvince.size(), value);
				break;

			case 3:
				String school = target.getString(KEY);
				int indexSchool = matrixSchool.indexOf(school);
				Document ledSchool = (Document) doc.get(LAST_EVENT_DATA);
				Document ctxSchool = (Document) ledSchool.get(CONTEXT);
				int usSchool = ctxSchool.getInteger(USER_SCORE);
				value = computeValue(score, usSchool, ledSchool.getLong(EVENT_TYPE));
				
				
				values.get(indexUser).set(
						indexSchool + matrixProvince.size()
								+ matrixMunicipality.size(), value);
				break;
			}
		}
	}
	
	public void fillMatrixWithLogs(ArrayList<Document> list) {
		for (Document doc : list) {
			System.out.println(doc.size());
		}
	}
	
	public void printUtilityMatrix(){
		int i = 0;
		for(Long userId : matrixUser){
			System.out.println("[User:"+userId+"]");
			int j = 0;
			for(String province: matrixProvince){
				System.out.println("Province:"+province + " Value: ["+ values.get(i).get(j) +"]");
				j++;
			}
			for(String municipality: matrixMunicipality){
				System.out.println("Municipality:"+municipality + " Value: ["+ values.get(i).get(j) +"]");
				j++;
			}
			for(String school: matrixSchool){
				System.out.println("School:"+school + " Value: ["+ values.get(i).get(j) +"]");
				j++;
			}
			i++;
		}
	}
}
