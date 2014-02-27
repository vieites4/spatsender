package its.app.spat.sender.utils;

import its.fac.messages.api.exceptions.ValueOutOfRangeException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class TLTopology {

	private final static Logger logger = LoggerFactory
			.getLogger(TLTopology.class);

	// TAGS SAX Parser
	private SAXParserFactory factory;
	private SAXParser saxParser;
	private DefaultHandler handler;
	private boolean tagDescription = false;
	private boolean tagPlacemark = false;
	private boolean tagNamePlacemark = false;

	// JSOUP
	private Document doc;
	private Elements divs;
	private int tamdivs;
	private String[] accessTable = null;

	// Intersection Point
	private String intersectionId = null;
	private String regulatorId = null;
	private String laneDescripcionIntersec = null;

	// Lanes
	private String intersectionPoint = null;
	private String laneDescripcion = null;
	private String laneApproach = null;
	private int laneNumber = 0;
	private int laneNumberId = 0;
	private String tlIfTurn = null;
	private String possibleTurns = null;
	private String tl = null;
	private String tlColors = null;
	private String tlShape = null;
	private String tlType = null;
	private String tlNum = "";
	private String laneID = null;
	private int trackId = 0;
	String[] tableOfEgress;

	private String namePlacemark = null;
	private String[] laneCad = null;

	private String[] tlLaneTypeBlanks = new String[30];
	private int laneNumTL = 0;
	Functions functions = new Functions();

	public TLTopology() {

	}

	public String[] generateTLTopology(String kml)
			throws ValueOutOfRangeException, SAXException, IOException,
			ParserConfigurationException {

		for (int i = 0; i < tlLaneTypeBlanks.length; i++) {
			tlLaneTypeBlanks[i] = "";
		}

		// SAX Parser
		factory = SAXParserFactory.newInstance();
		saxParser = factory.newSAXParser();

		handler = new DefaultHandler() {

			@Override
			public void startDocument() throws SAXException {
			}

			@Override
			public void endDocument() throws SAXException {
			}

			public void startElement(String s, String s1, String elementName,
					Attributes attributes) throws SAXException {

				// Search Placemark
				if (elementName.equalsIgnoreCase("placemark")) {
					tagPlacemark = true;
				}
				// Search Placemark Name
				if (elementName.equalsIgnoreCase("name")
						&& tagPlacemark == true) {
					tagNamePlacemark = true;
				}
				// Search Placemark Description
				if (elementName.equalsIgnoreCase("description")
						&& tagPlacemark == true) {
					tagDescription = true;
					tagPlacemark = false;
				}

			}

			public void endElement(String s, String s1, String element)
					throws SAXException {

			}

			public void characters(char[] ch, int start, int length)
					throws SAXException {

				// Tag Placemark Name
				if (tagNamePlacemark) {
					namePlacemark = new String(ch, start, length);
					tagNamePlacemark = false;
				}

				// Tag Description
				if (tagDescription) {
					laneDescripcion = null;
					laneDescripcion = new String(ch, start, length);

					// Description of center Point in Intersection
					if (namePlacemark.contains("REFERENCE")) {
						// Description parser with JSoup
						doc = Jsoup.parse(laneDescripcion);
						// Search div tag
						divs = doc.getElementsByTag("div");
						// Size of the table (Number of divs -1)
						tamdivs = divs.size();
						// Create Table (first lane = properties, other
						// lanes = track)
						accessTable = new String[tamdivs - 1];
						for (Element div : divs) {

							if (div.id().equalsIgnoreCase("properties")) {
								laneDescripcionIntersec = div.text();
							} else {
								accessTable[Integer.parseInt(div.id()) - 1] = div
										.text();
							}

						}

						tableOfEgress = new String[accessTable.length];
						for (int i = 0; i < tableOfEgress.length; i++) {
							tableOfEgress[i] = "";
						}
						// Reference Point description
						laneCad = laneDescripcionIntersec.split(",");
						// Interseccion Id
						intersectionId = laneCad[0];
						regulatorId = laneCad[5];
					}

					// Description of lanes
					if (namePlacemark.contains("LANE")) {

						laneCad = laneDescripcion.split(",");

						// Number of Intersection [XXX]
						intersectionPoint = laneCad[0];

						// Approach if XXX=LANE_XXX

						if (intersectionId.equalsIgnoreCase(intersectionPoint)) {
							// XXX,Y,Z,SPEED,WIDTH,{ATTRB,TLIFTURN},TL,{(COLORS),(TYPE)}
							// Number of Approach [Y]
							laneApproach = laneCad[1];
							// Number of lane [Z]
							laneNumber = Integer.parseInt(laneCad[2]);

							// Possible turns [{S:R:L]
							possibleTurns = laneCad[5];

							// TrafficLight if turn [0:1:2}]
							tlIfTurn = laneCad[6];

							// Traffic Light
							tl = laneCad[7];

							// Traffic Light type
							tlColors = laneCad[8];
							tlShape = laneCad[9];
							tlType = tlColors + "," + tlShape;

							// Unique laneNumber for each lane
							laneNumberId = Integer.parseInt(laneApproach
									+ laneNumber);

							// Fill array with TL:LANE:TYPE
							tlLaneTypeBlanks[laneNumTL] = tl + ":"
									+ laneNumberId + ":" + tlType;

							// Counting Traffic Lights
							if (!tlNum.contains("-" + tl + "-")) {
								tlNum = tlNum + "-" + tl + "-" + ",";
							}

							laneNumTL++;

							// Unique id for each lane
							laneID = laneCad[0] + "_" + laneCad[1] + "_"
									+ laneCad[2];
							// Search in table which approach is of the lane
							for (int i = 0; i < accessTable.length; i++) {
								if (accessTable[i].contains(laneID)) {
									trackId = i;
								}
							}

						} else { // EGRESS
							// Number of Approach [Y]
							laneApproach = laneCad[1];
							// Number of lane [Z]
							laneNumber = Integer.parseInt(laneCad[2]);

							// Unique laneNumber for each lane
							if (!intersectionPoint.equalsIgnoreCase("999")) {
								laneNumberId = -Integer.parseInt(laneApproach
										+ laneNumber);
							} else {
								laneNumberId = -Integer
										.parseInt(laneApproach + 0);
							}
							// Unique id for each lane
							laneID = laneCad[0] + "_" + laneCad[1] + "_"
									+ laneCad[2];
							// Search in table which approach is of the lane
							for (int i = 0; i < accessTable.length; i++) {
								if (accessTable[i].contains(laneID)) {
									trackId = i;
								}
							}

							for (int i = 0; i < tableOfEgress.length; i++) {
								if (trackId == i) {
									tableOfEgress[i] = tableOfEgress[i]
											+ laneNumberId + ",";

								}
							}
						}
					}

					tagDescription = false;
				}

			}

		};
		saxParser.parse(kml, handler);

		tlNum = tlNum.replaceAll("-", "");
		tlNum = tlNum.substring(0, tlNum.length() - 1);
		String[] tlNumArray = tlNum.split(",");
		// Calculate biggest number of TrafficLight
		int maxTL = 0;
		for (int i = 0; i < tlNumArray.length; i++) {
			int tlNumToInt = Integer.parseInt(tlNumArray[i]);
			if (tlNumToInt > maxTL) {
				maxTL = tlNumToInt;
			}
		}

		// Delete blank spaces
		String[] tlLaneType = new String[laneNumTL];
		int numLane = 0;
		for (int i = 0; i < tlLaneTypeBlanks.length; i++) {
			if (!tlLaneTypeBlanks[i].equalsIgnoreCase("")) {
				tlLaneType[numLane] = tlLaneTypeBlanks[i];

				numLane++;
			}
		}

		// Create arrays to fill with Traffic Lights
		int finalSize = maxTL;
		String[] tlLaneTypeSorted = new String[finalSize];
		String[] tlTypeSorted = new String[finalSize];
		for (int i = 0; i < tlLaneTypeSorted.length; i++) {
			tlLaneTypeSorted[i] = "";
			tlTypeSorted[i] = "";
		}

		// Sorted Traffic Lights
		for (int i = 0; i < tlLaneType.length; i++) {
			String[] laneTotal = tlLaneType[i].split(":"); // 1:13:{(R:A:V),(L:L:L)}
			String[] laneTotalType = tlLaneType[i].split("\\{"); // 1:13:{(R:A:V),(L:L:L)}
			tlLaneTypeSorted[Integer.parseInt(laneTotal[0]) - 1] = tlLaneTypeSorted[Integer
					.parseInt(laneTotal[0]) - 1] + laneTotal[1] + ",";
			tlTypeSorted[Integer.parseInt(laneTotal[0]) - 1] = laneTotalType[1];
		}

		// Delete blank spaces
		String[] tlLaneTypeSortedBlank = new String[tlNumArray.length];
		String[] tlTypeSortedBlank = new String[tlNumArray.length];
		int numLaneB = 0;
		for (int i = 0; i < tlLaneTypeSorted.length; i++) {
			if (!tlLaneTypeSorted[i].equalsIgnoreCase("")) {
				tlLaneTypeSortedBlank[numLaneB] = i + 1 + ";"
						+ tlLaneTypeSorted[i];
				tlTypeSortedBlank[numLaneB] = tlTypeSorted[i];
				numLaneB++;
			}
		}

		// Sorted Traffic Lights with Type (TL;LANES;TYPE;REGULATOR)
		for (int i = 0; i < tlLaneTypeSortedBlank.length; i++) {
			if (!tlLaneTypeSortedBlank[i].equalsIgnoreCase(""))
				tlLaneTypeSortedBlank[i] = tlLaneTypeSortedBlank[i].substring(
						0, tlLaneTypeSortedBlank[i].length() - 1);
			tlLaneTypeSortedBlank[i] = tlLaneTypeSortedBlank[i] + ";{"
					+ tlTypeSortedBlank[i] + ";" + regulatorId;
		}

		return tlLaneTypeSortedBlank;

	}
}