import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	// Datei einlesen
	public static String readFile(String name) {
		String filename = name;
		String text = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = in.readLine()) != null) {
					text += line+"\n";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}
	// Zeilenumbrueche und Headerzeilen entfernen
	public static String simpleParse(String seq) {
		String[] tmp;
		tmp = seq.split("\n");
		seq = " ";
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].charAt(0) != '>') {
				seq += tmp[i];
			}
		}
		return seq;
	}
}