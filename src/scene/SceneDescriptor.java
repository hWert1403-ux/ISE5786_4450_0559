package scene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// יש להוסיף את ספריית Gson לפרויקט
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import geometries.impl.Sphere;
import lighting.AmbientLight;
import primitives.Color;
import primitives.Double3;
import primitives.Point;

public class SceneDescriptor {

	public static Scene loadSceneFromJSON(String filePath) {
		try {
			// קריאת כל הקובץ למחרוזת
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JsonObject jsonRoot = JsonParser.parseString(content).getAsJsonObject();

			// יצירת הסצנה עם השם מהקובץ
			Scene scene = new Scene(jsonRoot.get("name").getAsString());

			// 1. צבע רקע
			if (jsonRoot.has("background")) {
				scene.setBackground(parseColor(jsonRoot.get("background").getAsString()));
			}

			// 2. תאורה סביבתית
			if (jsonRoot.has("ambientLight")) {
				JsonObject ambient = jsonRoot.getAsJsonObject("ambientLight");
				Color color = parseColor(ambient.get("color").getAsString());
				double ka = ambient.get("ka").getAsDouble();
				// חישוב העוצמה הסופית (צבע כפול מקדם ka)
				Color finalIntensity = color.scale(new Double3(ka));

				// שימוש בבנאי שמקבל רק צבע אחד כפי שנדרש בשלב 2
				scene.setAmbientLight(new AmbientLight(finalIntensity));

			}

			// 3. גאומטריות (מימוש בסיסי להדגמה)
			if (jsonRoot.has("geometries")) {
				JsonArray geoArray = jsonRoot.getAsJsonArray("geometries");

				for (JsonElement geoElem : geoArray) {
					JsonObject geoObj = geoElem.getAsJsonObject();
					String type = geoObj.get("type").getAsString();

					// בדיקה האם הגוף הוא כדור
					if (type.equals("Sphere")) {
						// א. שליפת המרכז כנקודה (Point)
						String centerStr = geoObj.get("center").getAsString();
						String[] coords = centerStr.split(" ");
						Point center = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]),
								Double.parseDouble(coords[2]));

						// ב. שליפת הרדיוס
						double radius = geoObj.get("radius").getAsDouble();

						// ג. יצירת האובייקט והוספה לרשימת הגאומטריות של הסצנה
						scene._geometries.add(new Sphere(center, radius));
					}

					// הערה: כאן תוכלי להוסיף בעתיד if (type.equals("Triangle")) וכו'
				}
			}

			return scene;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load JSON scene", e);
		}
	}

	private static Color parseColor(String colorStr) {
		String[] rgb = colorStr.split(" ");
		return new Color(Double.parseDouble(rgb[0]), Double.parseDouble(rgb[1]), Double.parseDouble(rgb[2]));
	}
}