package core;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;

/**
 * Classe utile ad eseguire dei filtri su collezioni di documenti di MongoDB
 * 
 */
public class Filter {

	private static final String TARGET = "target";
	private static final String TYPE_ID = "typeId";
	private static final String KEY = "key";

	/**
	 * Filtra una lista di documenti provenienti dai Watches per regione di
	 * provenienza
	 * 
	 * @param iterable
	 *            lista di documenti
	 * @param region
	 *            regione che fa da filtro
	 * @return lista filtrata per regione
	 */
	public static ArrayList<Document> filterWatchesByRegion(
			FindIterable<Document> iterable, String region) {

		// Si inizializzano le variabili
		ArrayList<Document> list = new ArrayList<Document>();
		QueryManager queryManager = new QueryManager();

		// In base al tipo di watch si verifica la presenza del luogo
		// all'interno della regione
		// 1 : Provincia
		// 2 : Comuni
		// 3 : Scuole
		// Se presenti, si aggiungono alla lista da restituire
		for (Document document : iterable) {
			int result;
			Document target = (Document) document.get(TARGET);
			long typeId = target.getLong(TYPE_ID);

			switch ((int) typeId) {
			case 1:
				String province = target.getString(KEY);
				System.out.println("provincia:"+province);
				result = queryManager.isProvinceInRegion(region, province);
				if (result == 0) {
					list.add(document);
				}
				break;

			case 2:
				String municipality = target.getString(KEY);
				result = queryManager.isMunicipalityInRegion(region,
						municipality);
				if (result == 0) {
					list.add(document);
				}

				break;

			case 3:
				String school = target.getString(KEY);
				result = queryManager.isSchoolInRegion(region, school);
				if (result == 0) {
					list.add(document);
				}
				break;

			}
		}
		// restituisce la lista filtrata
		return list;
	}

	/**
	 * Filtra una lista di documenti provenienti dai Logs per regione di
	 * provenienza
	 * 
	 * @param iterable
	 *            lista di documenti
	 * @param region
	 *            regione che fa da filtro
	 * @return lista filtrata per regione
	 */
	public static ArrayList<Document> filterLogsByRegion(
			FindIterable<Document> iterable, String region) {
		// Si inizializzano le variabili
		ArrayList<Document> list = new ArrayList<Document>();
		QueryManager queryManager = new QueryManager();

		// In base al tipo di azione del log, si verifica la presenza del luogo
		// all'interno della regione
		// webapi_municipality_aggregates : Provincia
		// webapi_school_aggregates : Comuni
		// Se presenti, si aggiungono alla lista da restituire
		for (Document document : iterable) {
			int result = 0;
			String action = document.getString("action");
			Document attributes = (Document) document.get("attributes");
			if(action == null) continue;
			switch (action) {
			case "webapi_province_aggregates":

				break;

			case "webapi_municipality_aggregates":
				String municipality = attributes.getString("codeProvince");
				System.out.println(municipality);
				result = queryManager.isProvinceInRegion(region, municipality);
				if (result == 0) {
					list.add(document);
				}
				break;

			case "webapi_school_aggregates":
				String school = attributes.getString("codeMunicipality");
				System.out.println(school);
				result = queryManager.isMunicipalityInRegion(region, school);
				if (result == 0) {
					list.add(document);
				}
				break;
			}
		}
		//restituisce la lista filtrata
		return list;
	}

}