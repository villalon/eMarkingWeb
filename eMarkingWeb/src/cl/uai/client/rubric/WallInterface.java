package cl.uai.client.rubric;

import cl.uai.client.MarkingInterface;
import cl.uai.client.resources.Resources;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WallInterface extends Composite{

	private VerticalPanel mainPanel;
	//Frames for the 3 walls
	private Frame frameAdmin;
	//private Frame frameCoord;
	private Frame framePublic;
	
	/** The panels containing the comments **/
	private FlowPanel WallInterfaceAdministration = null;
	//private FlowPanel WallInterfaceCoordination = null;
	private FlowPanel WallInterfacePublic = null;
	
	private TabPanel wallTabs = null;
	
	/**
	 * Creates the interface
	 */
	public WallInterface() {
		
		/** Main panel for Chat **/
		this.mainPanel = new VerticalPanel();
		this.mainPanel.addStyleName(Resources.INSTANCE.css().previouscomments());
		
		/**
		*Embedded node.js Wall
		*mongoDB MUST be listening
		*Node.js+socket.io file "collaborativeFeatures.js" MUST be running on webcursos:3000 (node server instance).
		**/
		String usernameOnline = MarkingInterface.getUsername();
		String userOnline = MarkingInterface.getRealUsername();
		Integer idOnline = MarkingInterface.getUserID();
		Integer currentGroupID = MarkingInterface.getGroupID();
		String roleOnline = MarkingInterface.getUserRole();
		
		//Administration Tab
		this.frameAdmin = new Frame(
						"http://webcursos:3000/adminWall/adminWall.html?"
						+"username="+usernameOnline
						+"&user="+userOnline
						+"&id="+idOnline
						+"&groupID="+currentGroupID
						+"&role="+roleOnline
						);
		frameAdmin.getElement().setId("adminWallFrame");
		
		//Other Tab
		this.framePublic = new Frame(
						"http://webcursos:3000/adminWall/publicWall.html?"
						+"username="+usernameOnline
						+"&user="+userOnline
						+"&id="+idOnline
						+"&groupID="+currentGroupID
						+"&role="+roleOnline
						);
		framePublic.getElement().setId("publicWallFrame");
		
		/**
		 * CREATING TABS
		 */
		wallTabs = new TabPanel();
		
		// Add wall tabs
		WallInterfaceAdministration = new FlowPanel();
		//WallInterfaceCoordination = new FlowPanel();
		WallInterfacePublic = new FlowPanel();
		
		//Embed admin wall
		WallInterfaceAdministration.add(frameAdmin);

		//Embed public wall
		WallInterfacePublic.add(framePublic);
		
		wallTabs.add(WallInterfaceAdministration, "Administración");
		wallTabs.add(WallInterfacePublic, "Público");
		wallTabs.selectTab(0);
		
		// Add to mainPanel
		mainPanel.add(wallTabs);
		
		//Setting heights
		int wallHeight = Window.getClientHeight()*50/100;
		mainPanel.setHeight(wallHeight+"px");
		WallInterfaceAdministration.setSize("470px",wallHeight+"px");
		WallInterfacePublic.setSize("470px",wallHeight+"px");
		frameAdmin.setSize("470px", wallHeight+"px");
		framePublic.setSize("470px", wallHeight+"px");
		
		this.initWidget(mainPanel);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		if(MarkingInterface.getCollaborativeFeatures() == 0)
			return;
	}

}