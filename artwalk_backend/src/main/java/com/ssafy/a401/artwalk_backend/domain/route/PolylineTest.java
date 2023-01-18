package com.ssafy.a401.artwalk_backend.domain.route;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;

public class PolylineTest {
	public static void main(String[] args) {
		String str = "[[129.086625, 35.157662],[129.082932, 35.153754],[129.08208, 35.152435],[129.08262, 35.149776],[129.082569, 35.149973],[129.065321, 35.149435],[129.067208, 35.139194],[129.067687, 35.137887],[129.063798, 35.137113],[129.063079, 35.136393],[129.061954, 35.135208],[129.060387, 35.134391],[129.059954, 35.130452],[129.05956, 35.129686],[129.054913, 35.126155],[129.043841, 35.115561],[129.039046, 35.105463],[129.04227, 35.102539],[129.040909, 35.102866],[129.107782, 35.079217],[129.109648, 35.0388],[128.590196, 34.635235],[126.536071, 33.520332],[126.536055, 33.520059],[126.534119, 33.518649],[126.53428, 33.518753],[126.534767, 33.519091],[126.5349, 33.519183],[126.535159, 33.518931],[126.53496, 33.518716],[126.534998, 33.518675],[126.534363, 33.518233],[126.534404, 33.518357],[126.533765, 33.517924],[126.533897, 33.517875],[126.531859, 33.516541],[126.531784, 33.515914],[126.534133, 33.515753],[126.533722, 33.515653],[126.533595, 33.515394],[126.534428, 33.514887],[126.534143, 33.514744],[126.533567, 33.5147],[126.535305, 33.514187],[126.534562, 33.515068],[126.534341, 33.515627],[126.535152, 33.516206],[126.535439, 33.516034],[126.53533, 33.515715],[126.535836, 33.515695],[126.535836, 33.515913],[126.536281, 33.515925],[126.535622, 33.516571],[126.536144, 33.516962],[126.535665, 33.517698],[126.536338, 33.517909],[126.536325, 33.518312],[126.536678, 33.518035],[126.538731, 33.518219],[126.538641, 33.517935],[126.536341, 33.517847],[126.536345, 33.515924],[126.538507, 33.515758],[126.538506, 33.515904],[126.539224, 33.5159],[126.539127, 33.518082],[126.53969, 33.518209],[126.53979, 33.515896],[126.540347, 33.515892],[126.540268, 33.518329],[126.540353, 33.518344],[126.540776, 33.518326],[126.540754, 33.518439],[126.541041, 33.518569],[126.543118, 33.521031],[126.543069, 33.521424],[126.542851, 33.521413],[126.542784, 33.521271],[126.541375, 33.521063],[126.540889, 33.521963],[126.542982, 33.522212],[126.543032, 33.521511],[126.543193, 33.521524],[126.5432, 33.521426],[126.54427, 33.521506],[126.544516, 33.522474],[126.549162, 33.522937],[126.548922, 33.524225],[126.548799, 33.524228],[126.546785, 33.522763],[126.544504, 33.522597],[126.54443, 33.523372],[126.543785, 33.523343],[126.543674, 33.524913],[126.543267, 33.524888],[126.543017, 33.525869],[126.422454, 33.772532],[126.114362, 33.878718],[125.839308, 34.028785],[125.655898, 34.297764],[125.599507, 34.481423],[125.732388, 35.231344],[125.76187, 35.92225],[125.999422, 36.835199],[126.03328, 36.913396],[126.273942, 37.071902],[126.344388, 37.225883],[126.599867, 37.455962],[126.600101, 37.454651],[126.60061, 37.454181],[126.605317, 37.457275],[126.607081, 37.456564],[126.622637, 37.454892],[126.629437, 37.452426],[126.63301, 37.453658],[126.637748, 37.459993],[126.640782, 37.46014],[126.642083, 37.462399],[126.642341, 37.462327],[126.642987, 37.462581],[126.643356, 37.462962],[126.647862, 37.468664],[126.701532, 37.470998],[126.702355, 37.470978],[126.702448, 37.471195],[126.702543, 37.471191],[126.703401, 37.471245],[126.703284, 37.471806],[126.704844, 37.472182],[126.705109, 37.473488],[126.706396, 37.473304],[126.706489, 37.474148],[126.708176, 37.474792],[126.709244, 37.47454],[126.791896, 37.482304],[126.835331, 37.492371],[126.841798, 37.492482],[126.850187, 37.495093],[126.854274, 37.493861],[126.855602, 37.493698],[126.88755, 37.480605],[126.930153, 37.484225],[126.957702, 37.479397],[126.958763, 37.480355],[126.962531, 37.478859],[126.963061, 37.479813],[126.963729, 37.479547],[126.966541, 37.481973],[126.969537, 37.48357],[126.972909, 37.482218],[126.974344, 37.483232],[126.996214, 37.488469],[127.007049, 37.49163],[127.020869, 37.49588],[127.033046, 37.499754],[127.033125, 37.499543],[127.036339, 37.500517],[127.037255, 37.50053]]";
		str = str.replace('[', ' ');
		str = str.replace(']', ' ');
		System.out.println(str);

		List<LatLng> coordList = new ArrayList<>();

		StringTokenizer st = new StringTokenizer(str, ",");
		while(st.hasMoreTokens()){
			double lng = Double.parseDouble(st.nextToken());
			double lat = Double.parseDouble(st.nextToken());
			coordList.add(new LatLng(lat, lng));
		}

		// Encode
		String encodeTxt = PolylineEncoding.encode(coordList);
		System.out.println(encodeTxt);

		// Decode
		List<LatLng> coordinates = PolylineEncoding.decode(encodeTxt);
		for (LatLng latLng : coordinates){
			System.out.print(latLng.toString() + " , ");
		}
	}
}
