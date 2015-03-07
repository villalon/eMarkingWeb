package cl.uai.client;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cl.uai.client.data.AjaxData;
import cl.uai.client.data.AjaxRequest;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.corechart.BarChartOptions;
import com.googlecode.gwt.charts.client.corechart.CandlestickChart;
import com.googlecode.gwt.charts.client.corechart.CandlestickChartOptions;
import com.googlecode.gwt.charts.client.corechart.CandlestickChartSeries;
import com.googlecode.gwt.charts.client.corechart.ColumnChart;
import com.googlecode.gwt.charts.client.corechart.ColumnChartOptions;
import com.googlecode.gwt.charts.client.corechart.ComboChart;
import com.googlecode.gwt.charts.client.corechart.BarChart;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.options.VAxis;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class EMarkingReports implements EntryPoint {

	private static Logger logger = Logger.getLogger(EMarkingReports.class.getName());
	private String action;
	private int id ;
	private String dirroot;
	private String emarking;
	private static final HorizontalAlignmentConstant CENTER = null;
	private VerticalPanel reportes = new VerticalPanel();
	private HorizontalPanel botones = new HorizontalPanel();
	private ToggleButton boton1=new ToggleButton();
	private ToggleButton boton2=new ToggleButton();
	private ToggleButton boton3=new ToggleButton();
	private ToggleButton boton4=new ToggleButton();
	private AbsolutePanel abPanel = new AbsolutePanel();
	private HorizontalPanel tabla = new HorizontalPanel();
	private HTML left = new HTML("");
	private HTML right =  new HTML("");;
	private BarChart markerChart;
	private BarChart statusChart;
	private BarChart passratioChart;
	private ComboChart marksChart;
	private LineChart line1;
	private LineChart line2;
	private CandlestickChart candlestick;
	private AreaChart coursemarksChart;
	private AreaChart areaChart;
	private ColumnChart efficiencyChart;
	private ColumnChart advanceChart;
	private ColumnChart markersadvanceChart;
	private int missing;
	private int submitted;
	private int grading;
	private int graded;
	private int regrading;
	private String []series;
	private double[] min;
	private double[] max;
	private double[] mean;
	private double[] firstQ;
	private double[] thirdQ;
	private double[]median;
	private int [][] coursem;
	private int coursemcount;
	private String [] seriesname;
	private double []rank1;
	private double [] rank2;
	private double [] rank3;
	private int current=0;
	private int countcriterion;
	private int countrate;
	private String [] criterion;
	private double[][] rate;
	private String [] advancedescriptions;
	private int [] advancerespondeds;
	private int [] advanceregradings;
	private int [] advancegradings;
	private int countadvance;
	private String [] markeradvancemarkers;
	private int [] markeradvancecorregidos;
	private int [] markeradvanceporcorregirs;
	private int[] markeradvanceporrecorregirs;
	private int countmarkeradvance;
	private int counter=0;


	public static native void console(String text)
	/*-{
       console.log(text);
    }-*/;
	
	public void onModuleLoad() {
		logger.fine("Initializing eMarking reports");
		
		action=RootPanel.get("reports").getElement().getAttribute("action");
		id =Integer.parseInt(RootPanel.get("reports").getElement().getAttribute("cmid"));
		dirroot = RootPanel.get("reports").getElement().getAttribute("url");
		emarking=RootPanel.get("reports").getElement().getAttribute("emarking");
		System.out.println(emarking);
		botones.add(boton1);
		botones.add(boton2);
		botones.add(boton3);
		botones.add(boton4);
		botones.setHeight("40px");
		tabla.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		tabla.add(left);
		abPanel.setSize((Window.getClientWidth()*0.5)+"px",(Window.getClientHeight()*0.55)+"px");
		tabla.add(abPanel);
		tabla.setCellHorizontalAlignment(right, CENTER);
		tabla.add(right);
		reportes.add(botones);
		reportes.add(tabla);



		AjaxRequest.moodleUrl = dirroot;
		AjaxRequest.ajaxRequest("action="+action+"&cmid="+id+"&emarkingids="+emarking, new AsyncCallback<AjaxData>() {

			@Override

			public void onSuccess(AjaxData result) {

				final String [] users;
				final int [] contribu;
				users=new String[10];
				contribu = new int [10];

				if(action.equals("gradereport")){
					boton1.setSize("16em", "2em");
					boton2.setSize("16em", "2em");
					boton3.setSize("16em", "2em");
					boton4.setSize("16em", "2em");

					boton1.setText("Notas por Curso");
					boton2.setText("Histograma de Notas por Curso");
					boton3.setText("Aprobacion por Curso");
					boton4.setText("Eficiencia de Criterios");



					Map<String, String> values = AjaxRequest.getValueFromResult(result);
					List<Map<String, String>> valuesMarks = AjaxRequest.getValuesFromResultString(values.get("Marks"));
					List<Map<String, String>> valuesCourseMarks = AjaxRequest.getValuesFromResultString(values.get("CourseMarks"));
					List<Map<String, String>> valuesPassRatio = AjaxRequest.getValuesFromResultString(values.get("PassRatio"));
					List<Map<String, String>> valuesEfficiencyCriterion = AjaxRequest.getValuesFromResultString(values.get("EfficiencyCriterion"));
					List<Map<String, String>> valuesEfficiencyRate = AjaxRequest.getValuesFromResultString(values.get("EfficiencyRate"));

					series= new String [400];
					min= new double [400];
					max = new double [400];
					mean = new double [400];
					firstQ= new double [400];
					thirdQ= new double [400];
					median= new double [400];
					int f=0;

					for(Map<String, String> marks : valuesMarks){
						series[f]=marks.get("series");
						min[f]=Double.parseDouble(marks.get("min"));
						max[f]=Double.parseDouble(marks.get("max"));
						mean[f]=Double.parseDouble(marks.get("mean"));
						firstQ[f]=Double.parseDouble(marks.get("firstQ"));
						thirdQ[f]=Double.parseDouble(marks.get("thirdQ"));
						median[f]=Double.parseDouble(marks.get("median"));
						f++;
					}
					coursem = new int[12][valuesCourseMarks.size()];
					coursemcount=0;
					for(Map<String, String> coursemarks : valuesCourseMarks){
						coursem[0][coursemcount]=Integer.parseInt(coursemarks.get("cero"));
						coursem[1][coursemcount]=Integer.parseInt(coursemarks.get("uno"));
						coursem[2][coursemcount]=Integer.parseInt(coursemarks.get("dos"));
						coursem[3][coursemcount]=Integer.parseInt(coursemarks.get("tres"));
						coursem[4][coursemcount]=Integer.parseInt(coursemarks.get("cuatro"));
						coursem[5][coursemcount]=Integer.parseInt(coursemarks.get("cinco"));
						coursem[6][coursemcount]=Integer.parseInt(coursemarks.get("seis"));
						coursem[7][coursemcount]=Integer.parseInt(coursemarks.get("siete"));
						coursem[8][coursemcount]=Integer.parseInt(coursemarks.get("ocho"));
						coursem[9][coursemcount]=Integer.parseInt(coursemarks.get("nueve"));
						coursem[10][coursemcount]=Integer.parseInt(coursemarks.get("diez"));
						coursem[11][coursemcount]=Integer.parseInt(coursemarks.get("once"));

						coursemcount++;

					}
					seriesname=new String[valuesPassRatio.size()];
					rank1 = new double [valuesPassRatio.size()];
					rank2 =  new double [valuesPassRatio.size()];
					rank3=  new double [valuesPassRatio.size()];


					for(Map<String, String> passratio : valuesPassRatio){


						seriesname[current] = passratio.get("seriesname");
						rank1[current]= Double.parseDouble(passratio.get("rank1"));
						rank2[current]= Double.parseDouble(passratio.get("rank2"));
						rank3[current]= Double.parseDouble(passratio.get("rank3"));
						current++;
					}

					criterion = new String[400];
					for(Map<String, String> efficiencycriterion : valuesEfficiencyCriterion){
						countcriterion = Integer.parseInt(efficiencycriterion.get("count"));
						for(int l=0;l<countcriterion; l++){
							criterion[l]= efficiencycriterion.get("criterion"+l);
						}
					}

					rate = new double [400][400];
					for(Map<String, String> efficiencyrate : valuesEfficiencyRate){
						for(int h=0;h<countcriterion;h++){
							rate[counter][h]= Double.parseDouble(efficiencyrate.get("rate"+h));
							//System.out.println(rate[counter][h]);
						}
						counter++;
					}

					Window.enableScrolling(true);
					Window.setMargin("0px");
					left.setHTML("<div style= 'height:"+(Window.getClientHeight()*0.5)+"px;height:50px; width:100px; font-size: 5em'><br><br><div Style ='color: #333;border-radius: 50px;'><</div>  </div>");
					//RootLayoutPanel.get().add(reportes);
					left.addClickHandler( new ClickHandler(){
						public void onClick(ClickEvent event){
							// implementar
							for(int h=abPanel.getWidgetCount()-1 ; h>0 ; h--){
								if(h!=0){
									if(abPanel.getWidget(h).isVisible()){
										abPanel.getWidget(h).setVisible(false);
										abPanel.getWidget(h-1).setVisible(true);
										((ToggleButton)botones.getWidget(h)).setDown(false);
										((ToggleButton)botones.getWidget(h)).setStylePrimaryName("Botonera-reports-sin-click");
										((ToggleButton)botones.getWidget(h-1)).setDown(true);
										((ToggleButton)botones.getWidget(h-1)).setStylePrimaryName("Botonera-reports-con-click");
										h= -2;
									}
								}
							}
						}
					});
					right.setHTML("<div style='height:"+(Window.getClientHeight()*0.5)+"px;width:100px; font-size: 5em'><br><br><div Style ='color: #333;border-radius: 50px;'> > </div> </div>");
					right.addClickHandler( new ClickHandler(){
						public void onClick(ClickEvent event){
							// implementar
							for(int h=0; h < abPanel.getWidgetCount(); h++){
								if(h!=abPanel.getWidgetCount()-1){
									if(abPanel.getWidget(h).isVisible()){
										abPanel.getWidget(h).setVisible(false);
										abPanel.getWidget(h+1).setVisible(true);
										((ToggleButton)botones.getWidget(h)).setDown(false);
										((ToggleButton)botones.getWidget(h)).setStylePrimaryName("Botonera-reports-sin-click");
										((ToggleButton)botones.getWidget(h+1)).setDown(true);
										((ToggleButton)botones.getWidget(h+1)).setStylePrimaryName("Botonera-reports-con-click");
										h= abPanel.getWidgetCount()+2;
									}
								}
							}
						}
					});
					ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
					chartLoader.loadApi(new Runnable() {
						@Override
						public void run() {
							//absolutePanel
							loadgradereport();
							sendCandlestick(seriesname, min, max, firstQ, thirdQ, mean, coursemcount);
							drawCourseMarks(coursem, coursemcount, seriesname);
							drawPassRatio(seriesname, rank1, rank2, rank3, current);
							drawEfficiency(criterion, rate, countcriterion, seriesname);


						}
					});
				}
				else if(action.equals("markingreport")){
					boton1.setSize("16em", "2em");
					boton2.setSize("16em", "2em");
					boton3.setSize("16em", "2em");
					boton4.setSize("16em", "2em");


					boton1.setText("Marking Status");
					boton2.setText("Contribution per marker");
					boton3.setText("Advance by criterion");
					boton4.setText("Advance per Marker");


					Map<String, String> values = AjaxRequest.getValueFromResult(result);
					List<Map<String, String>> valuesGrading = AjaxRequest.getValuesFromResultString(values.get("Grading"));
					List<Map<String, String>> valuesContributioners = AjaxRequest.getValuesFromResultString(values.get("Contributioners"));
					List<Map<String, String>> valuesContributions = AjaxRequest.getValuesFromResultString(values.get("Contributions"));
					List<Map<String, String>> valuesAdvancedescription = AjaxRequest.getValuesFromResultString(values.get("Advancedescription"));
					List<Map<String, String>> valuesAdvanceresponded = AjaxRequest.getValuesFromResultString(values.get("Advanceresponded"));
					List<Map<String, String>> valuesAdvanceregrading = AjaxRequest.getValuesFromResultString(values.get("Advanceregrading"));
					List<Map<String, String>> valuesAdvancegrading = AjaxRequest.getValuesFromResultString(values.get("Advancegrading"));
					List<Map<String, String>> valuesMarkeradvanceMarker = AjaxRequest.getValuesFromResultString(values.get("MarkeradvanceMarker"));
					List<Map<String, String>> valuesMarkeradvanceCorregido = AjaxRequest.getValuesFromResultString(values.get("MarkeradvanceCorregido"));
					List<Map<String, String>> valuesMarkeradvancePorcorregir = AjaxRequest.getValuesFromResultString(values.get("MarkeradvancePorcorregir"));
					List<Map<String, String>> valuesMarkeradvancePorrecorregir = AjaxRequest.getValuesFromResultString(values.get("MarkeradvancePorrecorregir"));

					for(Map<String, String> gradings : valuesGrading) {	
						missing = Integer.parseInt(gradings.get("missing"));
						submitted = Integer.parseInt(gradings.get("submitted"));
						grading = Integer.parseInt(gradings.get("grading"));
						graded = Integer.parseInt(gradings.get("graded"));
						regrading = Integer.parseInt(gradings.get("regrading"));
					}
					System.out.println(missing);
					int i=0;
					int k=0;
					for(Map<String, String> contributioners : valuesContributioners){
						users[i] = contributioners.get("user");		
						i++;
					}
					for(Map<String, String> contributions : valuesContributions){
						contribu[k] = Integer.parseInt(contributions.get("contrib"));
						k++;
					}
					advancedescriptions = new String [400];
					for(Map<String, String> advancedescription : valuesAdvancedescription){
						countadvance = Integer.parseInt(advancedescription.get("count"));
						for(int l=0;l<countadvance; l++){
							advancedescriptions[l]= advancedescription.get("description"+l);
						}
					}
					advancerespondeds = new int [400];
					for(Map<String, String> advanceresponded : valuesAdvanceresponded){
						for(int l=0;l<countadvance; l++){
							advancerespondeds[l]= Integer.parseInt(advanceresponded.get("responded"+l));

						}
					}
					advanceregradings= new int [400];
					for(Map<String, String> advanceregrading : valuesAdvanceregrading){
						for(int l=0;l<countadvance; l++){
							advanceregradings[l]= Integer.parseInt(advanceregrading.get("regrading"+l));

						}
					}advancegradings= new int [400];
					for(Map<String, String> advancegrading : valuesAdvancegrading){
						for(int l=0;l<countadvance; l++){
							advanceregradings[l]= Integer.parseInt(advancegrading.get("grading"+l));

						}
					}
					markeradvancemarkers= new String [400];
					for(Map<String, String> markeradvancemarker : valuesMarkeradvanceMarker){
						countmarkeradvance = Integer.parseInt(markeradvancemarker.get("count"));
						for(int l=0;l<countmarkeradvance; l++){
							markeradvancemarkers[l]= markeradvancemarker.get("corrector"+l);
						}
					}
					markeradvancecorregidos= new int [400];
					for(Map<String, String> markeradvancecorregido : valuesMarkeradvanceCorregido){
						for(int l=0;l<countmarkeradvance; l++){
							markeradvancecorregidos[l]= Integer.parseInt(markeradvancecorregido.get("corregido"+l));
						}
					}
					markeradvanceporcorregirs= new int [400];
					for(Map<String, String> markeradvanceporcorregir : valuesMarkeradvancePorcorregir){
						for(int l=0;l<countmarkeradvance; l++){
							markeradvanceporcorregirs[l]= Integer.parseInt(markeradvanceporcorregir.get("porcorregir"+l));
						}
					}
					markeradvanceporrecorregirs= new int [400];
					for(Map<String, String> markeradvanceporrecorregir : valuesMarkeradvancePorrecorregir){
						for(int l=0;l<countmarkeradvance; l++){
							markeradvanceporrecorregirs[l]= Integer.parseInt(markeradvanceporrecorregir.get("porrecorregir"+l));
						}
					}

					Window.enableScrolling(true);
					Window.setMargin("0px");
					left.setHTML("<div  style= 'height:"+(Window.getClientHeight()*0.5)+"px;height:50px; width:100px; font-size: 5em'><br><br><div Style ='color: #333;border-radius: 50px;'><</div>  </div>");
					//RootLayoutPanel.get().add(reportes);
					left.addClickHandler( new ClickHandler(){
						public void onClick(ClickEvent event){
							// implementar
							for(int h=abPanel.getWidgetCount()-1 ; h>0 ; h--){
								if(h!=0){
									if(abPanel.getWidget(h).isVisible()){
										abPanel.getWidget(h).setVisible(false);
										abPanel.getWidget(h-1).setVisible(true);
										((ToggleButton)botones.getWidget(h)).setDown(false);
										((ToggleButton)botones.getWidget(h)).setStylePrimaryName("Botonera-reports-sin-click");
										((ToggleButton)botones.getWidget(h-1)).setDown(true);
										((ToggleButton)botones.getWidget(h-1)).setStylePrimaryName("Botonera-reports-con-click");
										h= -2;
									}
								}
							}
						}
					});
					right.setHTML("<div style='height:"+(Window.getClientHeight()*0.5)+"px;width:100px; font-size: 5em'><br><br><div  Style = 'color: #333;border-radius: 50px;'> > </div> </div>");
					right.addClickHandler( new ClickHandler(){
						public void onClick(ClickEvent event){
							// implementar
							for(int h=0; h < abPanel.getWidgetCount(); h++){
								if(h!=abPanel.getWidgetCount()-1){
									if(abPanel.getWidget(h).isVisible()){
										abPanel.getWidget(h).setVisible(false);
										abPanel.getWidget(h+1).setVisible(true);
										((ToggleButton)botones.getWidget(h)).setDown(false);
										((ToggleButton)botones.getWidget(h)).setStylePrimaryName("Botonera-reports-sin-click");
										((ToggleButton)botones.getWidget(h+1)).setDown(true);
										((ToggleButton)botones.getWidget(h+1)).setStylePrimaryName("Botonera-reports-con-click");
										h= abPanel.getWidgetCount()+2;
									}
								}
							}
						}
					});

					ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
					chartLoader.loadApi(new Runnable() {
						final int contrib=contribu[0];
						final String user = users[0];
						@Override
						public void run() {

							//absolutePanel
							loadmarkingreport();
							drawStatus(missing, submitted, grading, graded, regrading);
							drawMarkers(user,contrib);
							drawAdvance(advancedescriptions, advancerespondeds, advanceregradings, advancegradings, countadvance);
							drawMarkersAdvance(markeradvancemarkers, markeradvancecorregidos, markeradvanceporcorregirs, markeradvanceporrecorregirs, countmarkeradvance);



						}
					});

				}




				boton1.setDown(true);
				boton1.setStylePrimaryName("Botonera-reports-con-click");
				boton2.setStylePrimaryName("Botonera-reports-sin-click");
				boton3.setStylePrimaryName("Botonera-reports-sin-click");
				boton4.setStylePrimaryName("Botonera-reports-sin-click");

				boton1.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event){
						boton1.setStylePrimaryName("Botonera-reports-con-click");
						for(int h=0; h < abPanel.getWidgetCount(); h++){
							if(h==0){
								abPanel.getWidget(h).setVisible(true);

							}else{
								abPanel.getWidget(h).setVisible(false);
							}
						}
						boton2.setDown(false);
						boton2.setStylePrimaryName("Botonera-reports-sin-click");
						boton3.setDown(false);
						boton3.setStylePrimaryName("Botonera-reports-sin-click");
						boton4.setDown(false);
						boton4.setStylePrimaryName("Botonera-reports-sin-click");
					}
				}
						);
				boton2.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event){
						boton2.setStylePrimaryName("Botonera-reports-con-click");
						for(int h=0; h < abPanel.getWidgetCount(); h++){
							if(h==1){
								abPanel.getWidget(h).setVisible(true);

							}else{
								abPanel.getWidget(h).setVisible(false);
							}
						}
						boton1.setDown(false);
						boton1.setStylePrimaryName("Botonera-reports-sin-click");
						boton3.setDown(false);
						boton3.setStylePrimaryName("Botonera-reports-sin-click");
						boton4.setDown(false);
						boton4.setStylePrimaryName("Botonera-reports-sin-click");
					}
				}
						);
				boton3.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event){
						boton3.setStylePrimaryName("Botonera-reports-con-click");
						for(int h=0; h < abPanel.getWidgetCount(); h++){
							if(h==2){
								abPanel.getWidget(h).setVisible(true);

							}else{
								abPanel.getWidget(h).setVisible(false);
							}
						}
						boton2.setDown(false);
						boton2.setStylePrimaryName("Botonera-reports-sin-click");
						boton1.setDown(false);
						boton1.setStylePrimaryName("Botonera-reports-sin-click");
						boton4.setDown(false);
						boton4.setStylePrimaryName("Botonera-reports-sin-click");
					}
				}
						);
				boton4.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event){
						boton4.setStylePrimaryName("Botonera-reports-con-click");
						for(int h=0; h < abPanel.getWidgetCount(); h++){
							if(h==3){
								abPanel.getWidget(h).setVisible(true);

							}else{
								abPanel.getWidget(h).setVisible(false);

							}
						}
						boton2.setDown(false);
						boton2.setStylePrimaryName("Botonera-reports-sin-click");
						boton3.setDown(false);
						boton3.setStylePrimaryName("Botonera-reports-sin-click");
						boton1.setDown(false);
						boton1.setStylePrimaryName("Botonera-reports-sin-click");
					}
				}

						);



			}
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Callback error");
			}

		});		

		RootPanel.get("reports").add(reportes);
	}

	private void loadgradereport(){
		double ancho = Window.getClientWidth();
		double largo = Window.getClientHeight();

		abPanel.add(getCandle(),(int)(ancho*0),(int)(largo*0));




		abPanel.add(getCourseMarks(),(int)(ancho*0),(int)(largo*0));
		getCourseMarks().setVisible(false);

		abPanel.add(getPassRatio(),(int)(ancho*0.05),(int)(largo*0));
		getPassRatio().setVisible(false);

		abPanel.add(getEfficiency(),(int)(ancho*0),(int)(largo*0));
		getEfficiency().setVisible(false);

	}
	private void loadmarkingreport() {
		double ancho = Window.getClientWidth();
		double largo = Window.getClientHeight();

		abPanel.add(getStatus(),(int)(ancho*0),(int)(largo*0));

		abPanel.add(getMarkers(),(int)(ancho*0),(int)(largo*0));
		getMarkers().setVisible(false);

		abPanel.add(getAdvance(),(int)(ancho*0),(int)(largo*0));
		getAdvance().setVisible(false);

		abPanel.add(getMarkersAdvance(),(int)(ancho*0),(int)(largo*0));
		getMarkersAdvance().setVisible(false);


	}
	private Widget getCandle(){
		if (candlestick == null) {
			candlestick = new CandlestickChart();
		}
		return candlestick;
	}
	private Widget getStatus() {
		if (statusChart == null) {
			statusChart = new BarChart();
		}
		return statusChart;
	}
	private Widget getMarkers() {
		if (markerChart == null) {
			markerChart = new BarChart();
		}
		return markerChart;
	}
	private Widget getMarks() {
		if (marksChart == null) {
			marksChart = new ComboChart();
		}
		return marksChart;
	}
	private Widget getCourseMarks() {
		if (coursemarksChart == null) {
			coursemarksChart = new AreaChart();
		}
		return coursemarksChart;
	}
	private Widget getPassRatio() {
		if (passratioChart == null) {
			passratioChart = new BarChart();
		}
		return passratioChart;
	}
	private Widget getEfficiency() {
		if (efficiencyChart == null) {
			efficiencyChart = new ColumnChart();
		}
		return efficiencyChart;
	}
	private Widget getAdvance() {
		if (advanceChart == null) {
			advanceChart = new ColumnChart();
		}
		return advanceChart;
	}
	private Widget getMarkersAdvance() {
		if (markersadvanceChart == null) {
			markersadvanceChart = new ColumnChart();
		}
		return markersadvanceChart;
	}
	private void drawStatus(int missing, int submitted, int grading, int graded, int regrading) {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Name");
		dataTable.addColumn(ColumnType.NUMBER, "Missing");
		dataTable.addColumn(ColumnType.NUMBER, "Submitted");
		dataTable.addColumn(ColumnType.NUMBER, "Grading");
		dataTable.addColumn(ColumnType.NUMBER, "Graded");
		dataTable.addColumn(ColumnType.NUMBER, "Regrading");


		dataTable.addRows(1);
		dataTable.setValue(0, 0, "");
		dataTable.setValue(0, 1, missing);
		dataTable.setValue(0, 2, submitted);
		dataTable.setValue(0, 3, grading);
		dataTable.setValue(0, 4, graded);
		dataTable.setValue(0, 5, regrading);


		// Chart options
		BarChartOptions option = BarChartOptions.create();
		option.setHeight(300);
		option.setWidth(500);
		option.setIsStacked(true);
		option.setTitle("Marking Status");
		// Draw the chart
		statusChart.draw(dataTable, option);
	}
	private void drawMarkers(String user, int contrib) {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Name");
		dataTable.addColumn(ColumnType.NUMBER, "user");


		dataTable.addRows(1);
		dataTable.setValue(0, 0, "");
		dataTable.setValue(0, 1, contrib);



		// Chart options
		BarChartOptions option = BarChartOptions.create();
		option.setHeight(300);
		option.setWidth(600);
		option.setIsStacked(true);
		option.setTitle("Contribution per marker");
		// Draw the chart
		markerChart.draw(dataTable, option);
	}
	private void drawCourseMarks(int [][] coursem, int coursemcount, String [] seriesname) {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Name");
		for(int i=0;i<coursemcount;i++){
			dataTable.addColumn(ColumnType.NUMBER, seriesname[i]);
		}

		dataTable.addRows(12);
		dataTable.setValue(0, 0, "<1.5");
		dataTable.setValue(1, 0, "");
		dataTable.setValue(2, 0, "<2.5");
		dataTable.setValue(3, 0, "");
		dataTable.setValue(4, 0, "<3.5");
		dataTable.setValue(5, 0, "");
		dataTable.setValue(6, 0, ">=4");
		dataTable.setValue(7, 0, "");
		dataTable.setValue(8, 0, ">=5");
		dataTable.setValue(9, 0, "");
		dataTable.setValue(10, 0, ">=6");
		dataTable.setValue(11, 0, "");
		for(int i=0;i<12;i++){
			for(int j=1;j<=coursemcount;j++){
				dataTable.setValue(i, j, coursem[i][j-1]);
			}


		}
		// Chart options
		AreaChartOptions option = AreaChartOptions.create();
		option.setHeight(300);
		option.setWidth(600);
		option.setTitle("Notas por curso");
		// Draw the chart
		coursemarksChart.draw(dataTable, option);
	}
	private void drawPassRatio(String[] seriesname,double[] rank1, double[] rank2, double[] rank3, int current) {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Name");
		dataTable.addColumn(ColumnType.NUMBER, "Menor a 3");
		dataTable.addColumn(ColumnType.NUMBER, "3 a 4");
		dataTable.addColumn(ColumnType.NUMBER, "Mayor que 4");
		dataTable.addRows(current);
		for(int i=0; i<current;i++){

			dataTable.setValue(i, 0, seriesname[i]);
			dataTable.setValue(i, 1, rank1[i]);
			dataTable.setValue(i, 2, rank2[i]);
			dataTable.setValue(i, 3, rank3[i]);
		}



		// Chart options
		BarChartOptions option = BarChartOptions.create();
		option.setHeight(300);
		option.setWidth(600);
		option.setIsStacked(true);
		option.setTitle("Aprobacion por curso");
		// Draw the chart
		passratioChart.draw(dataTable, option);
	}
	private void drawEfficiency(String [] criterion, double [][] rate, int count, String [] seriesname) {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Name");

		dataTable.addRows(count);
		int parallels =seriesname.length;
		for(int h=1;h<=parallels;h++){
			dataTable.addColumn(ColumnType.NUMBER, seriesname[h-1]);
			for(int i = 0; i<parallels; i++){
				for(int j=0;j<count;j++){
					dataTable.setValue(j,0, criterion[j]);
					dataTable.setValue(j, h, rate[i][j]);
				}
			}
		}
		// Chart options
		ColumnChartOptions option = ColumnChartOptions.create();
		option.setHeight(300);
		option.setWidth(600);
		option.setTitle("Eficiencia de Criterio");
		// Draw the chart
		efficiencyChart.draw(dataTable, option);
	}
	private void drawAdvance(String [] advancedescription, int [] advancerespondeds, int []advanceregradings, int []  advancegradings, int count) {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Name");
		dataTable.addColumn(ColumnType.NUMBER, "Responded");
		dataTable.addColumn(ColumnType.NUMBER, "Regrading");
		dataTable.addColumn(ColumnType.NUMBER, "Grading");


		dataTable.addRows(count);
		for(int i = 0; i<count; i++){
			dataTable.setValue(i,0, advancedescription[i]);
			dataTable.setValue(i, 1, advancerespondeds[i]);
			dataTable.setValue(i, 2, advanceregradings[i]);
			dataTable.setValue(i, 3, advancegradings[i]);
		}
		// Chart options
		ColumnChartOptions option = ColumnChartOptions.create();
		option.setHeight(300);
		option.setWidth(600);
		option.setIsStacked(true);
		option.setTitle("Avance por pregunta");
		// Draw the chart
		advanceChart.draw(dataTable, option);
	}
	private void drawMarkersAdvance(String [] markeradvancemarkers, int [] markeradvancecorregidos, int []markeradvanceporcorregirs, int []  markeradvanceporrecorregirs, int count) {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Name");
		dataTable.addColumn(ColumnType.NUMBER, "Responded");
		dataTable.addColumn(ColumnType.NUMBER, "Regrading");
		dataTable.addColumn(ColumnType.NUMBER, "Grading");


		dataTable.addRows(count);
		for(int i = 0; i<count; i++){
			dataTable.setValue(i,0, markeradvancemarkers[i]);
			dataTable.setValue(i, 1, markeradvancecorregidos[i]);
			dataTable.setValue(i, 2, markeradvanceporrecorregirs[i]);
			dataTable.setValue(i, 3, markeradvanceporcorregirs[i]);
		}
		// Chart options
		ColumnChartOptions option = ColumnChartOptions.create();
		option.setHeight(300);
		option.setWidth(600);
		option.setIsStacked(true);
		option.setTitle("Avance por Ayudante");
		// Draw the chart
		markersadvanceChart.draw(dataTable, option);
	}
	private void sendCandlestick(String [] seriesname, double [] min, double [] max, double [] firstQ, double [] thirdQ, double [] mean,  int count){
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Alguna basura mostrara esto");
		dataTable.addColumn(ColumnType.NUMBER, "min-max,firsQ-thirdQ");
		dataTable.addColumn(ColumnType.NUMBER, "");
		dataTable.addColumn(ColumnType.NUMBER, "");
		dataTable.addColumn(ColumnType.NUMBER, "");


		dataTable.addRows(count);
		for(int i = 0; i<count; i++){
			dataTable.setValue(i,0, seriesname[i]);
			dataTable.setValue(i, 1, min[i]);
			dataTable.setValue(i, 2, max[i]);
			dataTable.setValue(i, 3, firstQ[i]);
			dataTable.setValue(i, 4,thirdQ[i]);
		}
		CandlestickChartSeries series = CandlestickChartSeries.create();
		series.setColor("#2980b9");
		series.setVisibleInLegend(false);
		VAxis eje = VAxis.create();
		eje.setTicks(1,2,3,4,5,6,7);
		CandlestickChartOptions option = CandlestickChartOptions.create();
		option.setVAxis(0, eje);
		option.setTitle("Notas por curso");
		option.setHeight(300);
		option.setWidth(600);
		candlestick.draw(dataTable, option);




	}

}

